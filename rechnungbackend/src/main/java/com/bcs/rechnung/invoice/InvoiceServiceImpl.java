package com.bcs.rechnung.invoice;

import com.bcs.rechnung.bank_account.BankAccountEntity;
import com.bcs.rechnung.bank_account.BankAccountRepository;
import com.bcs.rechnung.company.CompanyEntity;
import com.bcs.rechnung.company.CompanyRepository;
import com.bcs.rechnung.customer.CustomerEntity;
import com.bcs.rechnung.customer.CustomerRepository;
import com.bcs.rechnung.exception.ResourceNotFoundException;
import com.bcs.rechnung.invoice.dto.InvoiceDto;
import com.bcs.rechnung.invoice.dto.InvoiceItemRequest;
import com.bcs.rechnung.invoice.dto.InvoiceRequest;
import com.bcs.rechnung.product.ProductEntity;
import com.bcs.rechnung.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class InvoiceServiceImpl
    implements InvoiceService {

    private static final int PRICE_SCALE =
        4;

    private static final int QUANTITY_SCALE =
        4;

    private static final int PERCENT_SCALE =
        4;

    private static final RoundingMode MONEY_ROUNDING_MODE =
        RoundingMode.HALF_UP;

    private static final BigDecimal ONE_HUNDRED =
        BigDecimal.valueOf(
            100
        );

    private static final String DEFAULT_TAX_CATEGORY_CODE =
        "S";

    private final InvoiceRepository invoiceRepository;

    private final CompanyRepository companyRepository;

    private final CustomerRepository customerRepository;

    private final BankAccountRepository bankAccountRepository;

    private final ProductRepository productRepository;

    private final InvoiceMapper invoiceMapper;

    @Override
    @Transactional(readOnly = true)
    public List<InvoiceDto> findAll() {

        return invoiceRepository
            .findAll()
            .stream()
            .map(
                invoiceMapper::toDto
            )
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<InvoiceDto> findByStatus(
        InvoiceStatus status
    ) {
        Objects.requireNonNull(
            status,
            "Der Rechnungsstatus darf nicht null sein."
        );

        return invoiceRepository
            .findByStatusOrderByInvoiceDateDesc(
                status
            )
            .stream()
            .map(
                invoiceMapper::toDto
            )
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<InvoiceDto> findByCompanyId(
        Long companyId
    ) {
        requirePositiveId(
            companyId,
            "Firmen-ID"
        );

        return invoiceRepository
            .findByCompany_IdOrderByInvoiceDateDesc(
                companyId
            )
            .stream()
            .map(
                invoiceMapper::toDto
            )
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<InvoiceDto> findByCustomerId(
        Long customerId
    ) {
        requirePositiveId(
            customerId,
            "Kunden-ID"
        );

        return invoiceRepository
            .findByCustomer_IdOrderByInvoiceDateDesc(
                customerId
            )
            .stream()
            .map(
                invoiceMapper::toDto
            )
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public InvoiceDto findById(
        Long id
    ) {
        return invoiceMapper.toDto(
            loadInvoice(
                id
            )
        );
    }

    @Override
    public InvoiceDto create(
        InvoiceRequest request
    ) {
        Objects.requireNonNull(
            request,
            "Der Rechnungsrequest darf nicht null sein."
        );

        validateRequest(
            request
        );

        CompanyEntity company =
            loadCompany(
                request.companyId()
            );

        CustomerEntity customer =
            loadCustomer(
                request.customerId()
            );

        BankAccountEntity bankAccount =
            resolveBankAccount(
                request.bankAccountId(),
                company.getId()
            );

        validateAssignments(
            company,
            customer,
            bankAccount
        );

        InvoiceEntity invoice =
            createInvoiceHeader(
                request,
                company,
                customer,
                bankAccount
            );

        List<InvoiceItemEntity> items =
            createItems(
                request.items(),
                company
            );

        /*
         * replaceItems verbindet alle Positionen mit
         * der Rechnung und ruft recalculateTotals() auf.
         */
        invoice.replaceItems(
            items
        );

        validateCalculatedInvoice(
            invoice
        );

        InvoiceEntity savedInvoice =
            invoiceRepository.saveAndFlush(
                invoice
            );

        log.info(
            "Rechnungsentwurf mit ID {} und {} Positionen erstellt.",
            savedInvoice.getId(),
            savedInvoice.getItems().size()
        );

        return invoiceMapper.toDto(
            savedInvoice
        );
    }

    @Override
    public InvoiceDto complete(
        Long id
    ) {
        InvoiceEntity invoice =
            loadInvoice(
                id
            );

        requireStatus(
            invoice,
            InvoiceStatus.DRAFT
        );

        requireNotArchived(
            invoice
        );

        if (
            invoice.getItems() == null
                || invoice.getItems().isEmpty()
        ) {
            throw new IllegalStateException(
                "Eine Rechnung ohne Positionen "
                    + "kann nicht abgeschlossen werden."
            );
        }

        /*
         * Sämtliche Positionswerte und anschließend
         * alle Rechnungssummen neu berechnen.
         */
        recalculateInvoice(
            invoice
        );

        validateCalculatedInvoice(
            invoice
        );

        if (!invoice.hasConsistentTotals()) {
            throw new IllegalStateException(
                "Die Rechnungssummen stimmen nicht "
                    + "mit den Positionssummen überein."
            );
        }

        /*
         * Rechnungsnummer nur erzeugen, wenn bisher
         * keine endgültige Nummer vorhanden ist.
         */
        if (
            invoice.getInvoiceNumber() == null
                || invoice.getInvoiceNumber().isBlank()
                || invoice.getInvoiceNumber().startsWith(
                "DRAFT-"
            )
        ) {
            invoice.setInvoiceNumber(
                generateInvoiceNumber(
                    invoice
                )
            );
        }

        validateUniqueInvoiceNumber(
            invoice
        );

        invoice.setStatus(
            InvoiceStatus.CREATED
        );

        InvoiceEntity savedInvoice =
            invoiceRepository.saveAndFlush(
                invoice
            );

        log.info(
            "Rechnung {} mit ID {} abgeschlossen.",
            savedInvoice.getInvoiceNumber(),
            savedInvoice.getId()
        );

        return invoiceMapper.toDto(
            savedInvoice
        );
    }

    @Override
    public InvoiceDto markAsSent(
        Long id
    ) {
        InvoiceEntity invoice =
            loadInvoice(
                id
            );

        requireStatus(
            invoice,
            InvoiceStatus.CREATED
        );

        invoice.setStatus(
            InvoiceStatus.SENT
        );

        InvoiceEntity savedInvoice =
            invoiceRepository.saveAndFlush(
                invoice
            );

        log.info(
            "Rechnung {} als versendet markiert.",
            savedInvoice.getInvoiceNumber()
        );

        return invoiceMapper.toDto(
            savedInvoice
        );
    }

    @Override
    public InvoiceDto markAsPaid(
        Long id
    ) {
        InvoiceEntity invoice =
            loadInvoice(
                id
            );

        if (
            invoice.getStatus() != InvoiceStatus.CREATED
                && invoice.getStatus() != InvoiceStatus.SENT
        ) {
            throw new IllegalStateException(
                "Nur erstellte oder versendete Rechnungen "
                    + "können als bezahlt markiert werden."
            );
        }

        invoice.setStatus(
            InvoiceStatus.PAID
        );

        InvoiceEntity savedInvoice =
            invoiceRepository.saveAndFlush(
                invoice
            );

        log.info(
            "Rechnung {} als bezahlt markiert.",
            savedInvoice.getInvoiceNumber()
        );

        return invoiceMapper.toDto(
            savedInvoice
        );
    }

    @Override
    public InvoiceDto cancel(
        Long id
    ) {
        InvoiceEntity invoice =
            loadInvoice(
                id
            );

        if (
            invoice.getStatus() == InvoiceStatus.PAID
        ) {
            throw new IllegalStateException(
                "Eine bezahlte Rechnung kann nicht "
                    + "direkt storniert werden."
            );
        }

        if (
            invoice.getStatus() == InvoiceStatus.CANCELLED
        ) {
            throw new IllegalStateException(
                "Die Rechnung ist bereits storniert."
            );
        }

        invoice.setStatus(
            InvoiceStatus.CANCELLED
        );

        InvoiceEntity savedInvoice =
            invoiceRepository.saveAndFlush(
                invoice
            );

        log.info(
            "Rechnung {} storniert.",
            savedInvoice.getInvoiceNumber()
        );

        return invoiceMapper.toDto(
            savedInvoice
        );
    }

    @Override
    public void deleteDraft(
        Long id
    ) {
        InvoiceEntity invoice =
            loadInvoice(
                id
            );

        requireStatus(
            invoice,
            InvoiceStatus.DRAFT
        );

        requireNotArchived(
            invoice
        );

        invoiceRepository.delete(
            invoice
        );

        invoiceRepository.flush();

        log.info(
            "Rechnungsentwurf mit ID {} gelöscht.",
            id
        );
    }

    private InvoiceEntity createInvoiceHeader(
        InvoiceRequest request,
        CompanyEntity company,
        CustomerEntity customer,
        BankAccountEntity bankAccount
    ) {
        InvoiceEntity invoice =
            new InvoiceEntity();

        invoice.setCompany(
            company
        );

        invoice.setCustomer(
            customer
        );

        invoice.setBankAccount(
            bankAccount
        );

        /*
         * Da invoice_number in der Datenbank nicht null
         * und eindeutig ist, erhält der Entwurf zunächst
         * eine temporäre eindeutige Nummer.
         *
         * Nach dem ersten Speichern wird diese Nummer mit
         * der tatsächlichen ID aktualisiert.
         */
        invoice.setInvoiceNumber(
            createTemporaryInvoiceNumber()
        );

        invoice.setInvoiceDate(
            request.invoiceDate()
        );

        invoice.setServiceDate(
            request.serviceDate()
        );

        invoice.setDueDate(
            request.dueDate()
        );

        invoice.setPaymentTermDays(
            request.paymentTermDays()
        );

        invoice.setDiscountPercent(
            percent(
                zeroIfNull(
                    request.discountPercent()
                )
            )
        );

        invoice.setDiscountDays(
            request.discountDays() == null
                ? 0
                : request.discountDays()
        );

        invoice.setBuyerReference(
            normalizeText(
                request.buyerReference()
            )
        );

        invoice.setOrderReference(
            normalizeText(
                request.orderReference()
            )
        );

        invoice.setProjectReference(
            normalizeText(
                request.projectReference()
            )
        );

        invoice.setContractReference(
            normalizeText(
                request.contractReference()
            )
        );

        invoice.setPaymentReference(
            normalizeText(
                request.paymentReference()
            )
        );

        invoice.setNotes(
            normalizeText(
                request.notes()
            )
        );

        invoice.setStatus(
            InvoiceStatus.DRAFT
        );

        invoice.setArchived(
            false
        );

        applyCustomerSnapshot(
            invoice,
            customer
        );

        applyBankSnapshot(
            invoice,
            bankAccount
        );

        applyXRechnungDefaults(
            invoice
        );

        return invoice;
    }

    private void applyCustomerSnapshot(
        InvoiceEntity invoice,
        CustomerEntity customer
    ) {
        invoice.setCustomerNumber(
            customer.getCustomerNumber()
        );

        invoice.setCustomerName(
            customer.getName()
        );

        invoice.setCustomerStreet(
            customer.getStreet()
        );

        invoice.setCustomerZipCode(
            customer.getZipCode()
        );

        invoice.setCustomerCity(
            customer.getCity()
        );

        invoice.setCustomerCountry(
            customer.getCountry()
        );

        invoice.setCustomerVatId(
            customer.getVatId()
        );

        /*
         * Falls CustomerEntity diese Werte führt,
         * werden sie als Snapshot übernommen.
         */
        invoice.setCustomerElectronicAddress(
            normalizeText(
                customer.getElectronicAddress()
            )
        );

        invoice.setCustomerElectronicAddressScheme(
            normalizeText(
                customer.getElectronicAddressScheme()
            )
        );
    }

    private void applyBankSnapshot(
        InvoiceEntity invoice,
        BankAccountEntity bankAccount
    ) {
        if (bankAccount.getBank() == null) {
            throw new IllegalStateException(
                "Dem Bankkonto ist keine Bank zugeordnet."
            );
        }

        invoice.setBankName(
            bankAccount.getBank()
                .getBankName()
        );

        invoice.setIban(
            normalizeIban(
                bankAccount.getIban()
            )
        );

        invoice.setBic(
            normalizeText(
                bankAccount.getBank()
                    .getBic()
            )
        );
    }

    private void applyXRechnungDefaults(
        InvoiceEntity invoice
    ) {
        invoice.setInvoiceTypeCode(
            "380"
        );

        invoice.setPaymentMeansCode(
            "58"
        );

        invoice.setTaxCategoryCode(
            DEFAULT_TAX_CATEGORY_CODE
        );

        invoice.setCurrencyCode(
            "EUR"
        );

        invoice.setBusinessProcessId(
            "urn:fdc:peppol.eu:2017:poacc:billing:01:1.0"
        );

        invoice.setSpecificationIdentifier(
            "urn:xeinkauf.de:kosit:xrechnung_3.0"
        );

        if (
            invoice.getPaymentReference() == null
                || invoice.getPaymentReference().isBlank()
        ) {
            invoice.setPaymentReference(
                invoice.getInvoiceNumber()
            );
        }
    }

    private List<InvoiceItemEntity> createItems(
        List<InvoiceItemRequest> requests,
        CompanyEntity company
    ) {
        if (
            requests == null
                || requests.isEmpty()
        ) {
            throw new IllegalArgumentException(
                "Die Rechnung muss mindestens "
                    + "eine Position enthalten."
            );
        }

        List<InvoiceItemEntity> items =
            new ArrayList<>(
                requests.size()
            );

        int positionNumber =
            1;

        for (
            InvoiceItemRequest request :
            requests
        ) {
            if (request == null) {
                throw new IllegalArgumentException(
                    "Eine Rechnungsposition darf "
                        + "nicht null sein."
                );
            }

            validateItemRequest(
                request
            );

            ProductEntity product =
                loadProduct(
                    request.productId()
                );

            validateProductAssignment(
                company,
                product
            );

            InvoiceItemEntity item =
                createItem(
                    positionNumber,
                    request,
                    product
                );

            items.add(
                item
            );

            positionNumber++;
        }

        return items;
    }

    private InvoiceItemEntity createItem(
        int positionNumber,
        InvoiceItemRequest request,
        ProductEntity product
    ) {
        Objects.requireNonNull(
            request,
            "Die Rechnungsposition darf nicht null sein."
        );

        Objects.requireNonNull(
            product,
            "Das Produkt darf nicht null sein."
        );

        BigDecimal quantity =
            quantity(
                request.quantity()
            );

        BigDecimal unitPrice =
            price(
                request.unitPriceNet() == null
                    ? product.getNetPrice()
                    : request.unitPriceNet()
            );

        BigDecimal discountPercent =
            percent(
                zeroIfNull(
                    request.discountPercent()
                )
            );

        BigDecimal vatRate =
            percent(
                request.vatRate() == null
                    ? product.getVatRate()
                    : request.vatRate()
            );

        InvoiceItemEntity item =
            new InvoiceItemEntity();

        /*
         * Korrekte JPA-Beziehung setzen.
         *
         * InvoiceItemEntity besitzt kein Feld productId,
         * sondern eine Beziehung zu ProductEntity.
         */
        item.setProduct(
            product
        );

        item.setPositionNumber(
            positionNumber
        );

        item.setProductNumber(
            product.getProductNumber()
        );

        item.setProductName(
            product.getName()
        );

        item.setDescription(
            resolveDescription(
                request.description(),
                product
            )
        );

        item.setQuantity(
            quantity
        );

        item.setUnit(
            resolveUnit(
                request.unit(),
                product
            )
        );

        item.setUnitPriceNet(
            unitPrice
        );

        item.setDiscountPercent(
            discountPercent
        );

        item.setVatRate(
            vatRate
        );

        item.setTaxCategoryCode(
            normalizeTaxCategoryCode(
                product.getTaxCategoryCode()
            )
        );

        item.setItemClassificationCode(
            normalizeText(
                product.getItemClassificationCode()
            )
        );

        /*
         * Zentrale Positionsberechnung in
         * InvoiceItemEntity.
         */
        item.recalculateAmounts();

        if (!item.hasConsistentAmounts()) {
            throw new IllegalStateException(
                "Die berechneten Werte der Position "
                    + positionNumber
                    + " sind nicht konsistent."
            );
        }

        return item;
    }

    private void recalculateInvoice(
        InvoiceEntity invoice
    ) {
        Objects.requireNonNull(
            invoice,
            "Die Rechnung darf nicht null sein."
        );

        if (
            invoice.getItems() == null
                || invoice.getItems().isEmpty()
        ) {
            throw new IllegalStateException(
                "Die Rechnung enthält keine Positionen."
            );
        }

        for (
            InvoiceItemEntity item :
            invoice.getItems()
        ) {
            if (item == null) {
                throw new IllegalStateException(
                    "Die Rechnung enthält eine "
                        + "ungültige Position."
                );
            }

            item.recalculateAmounts();

            if (!item.hasConsistentAmounts()) {
                throw new IllegalStateException(
                    "Die Position "
                        + item.getPositionNumber()
                        + " besitzt inkonsistente Beträge."
                );
            }
        }

        /*
         * Zentrale Aggregation aus InvoiceItemEntity.
         */
        invoice.recalculateTotals();
    }

    private void validateCalculatedInvoice(
        InvoiceEntity invoice
    ) {
        if (
            invoice.getItems() == null
                || invoice.getItems().isEmpty()
        ) {
            throw new IllegalStateException(
                "Die Rechnung enthält keine Positionen."
            );
        }

        for (
            InvoiceItemEntity item :
            invoice.getItems()
        ) {
            if (item == null) {
                throw new IllegalStateException(
                    "Die Rechnung enthält eine "
                        + "ungültige Position."
                );
            }

            if (!item.hasConsistentAmounts()) {
                throw new IllegalStateException(
                    "Die Rechnungsposition "
                        + item.getPositionNumber()
                        + " besitzt inkonsistente Beträge."
                );
            }
        }

        if (!invoice.hasConsistentTotals()) {
            throw new IllegalStateException(
                "Die Rechnungssummen stimmen nicht "
                    + "mit den Positionssummen überein."
            );
        }

        BigDecimal expectedGross =
            invoice.getSubtotalNet()
                .add(
                    invoice.getTaxAmount()
                )
                .setScale(
                    2,
                    MONEY_ROUNDING_MODE
                );

        if (
            expectedGross.compareTo(
                invoice.getTotalGross()
            ) != 0
        ) {
            throw new IllegalStateException(
                "Der Gesamtbetrag entspricht nicht "
                    + "Nettosumme plus Steuerbetrag."
            );
        }
    }

    private void validateRequest(
        InvoiceRequest request
    ) {
        requirePositiveId(
            request.companyId(),
            "Firmen-ID"
        );

        requirePositiveId(
            request.customerId(),
            "Kunden-ID"
        );

        if (request.bankAccountId() != null) {
            requirePositiveId(
                request.bankAccountId(),
                "Bankkonto-ID"
            );
        }

        if (request.invoiceDate() == null) {
            throw new IllegalArgumentException(
                "Das Rechnungsdatum muss angegeben werden."
            );
        }

        if (request.dueDate() == null) {
            throw new IllegalArgumentException(
                "Das Fälligkeitsdatum muss angegeben werden."
            );
        }

        if (
            request.dueDate().isBefore(
                request.invoiceDate()
            )
        ) {
            throw new IllegalArgumentException(
                "Das Fälligkeitsdatum darf nicht vor "
                    + "dem Rechnungsdatum liegen."
            );
        }

        if (
            request.serviceDate() != null
                && request.serviceDate().isAfter(
                request.invoiceDate()
            )
        ) {
            throw new IllegalArgumentException(
                "Das Leistungsdatum darf nicht nach "
                    + "dem Rechnungsdatum liegen."
            );
        }

        if (
            request.paymentTermDays() == null
                || request.paymentTermDays() < 0
        ) {
            throw new IllegalArgumentException(
                "Das Zahlungsziel muss angegeben werden "
                    + "und darf nicht negativ sein."
            );
        }

        if (
            !request.dueDate().equals(
                request.invoiceDate().plusDays(
                    request.paymentTermDays()
                )
            )
        ) {
            throw new IllegalArgumentException(
                "Das Fälligkeitsdatum muss dem Rechnungsdatum "
                    + "zuzüglich Zahlungsziel entsprechen."
            );
        }

        validatePercent(
            request.discountPercent(),
            "Der Rechnungsskonto"
        );

        validateDiscountConfiguration(
            request
        );

        if (
            request.items() == null
                || request.items().isEmpty()
        ) {
            throw new IllegalArgumentException(
                "Die Rechnung muss mindestens "
                    + "eine Position enthalten."
            );
        }
    }

    private void validateDiscountConfiguration(
        InvoiceRequest request
    ) {
        BigDecimal discountPercent =
            zeroIfNull(
                request.discountPercent()
            );

        int discountDays =
            request.discountDays() == null
                ? 0
                : request.discountDays();

        if (discountDays < 0) {
            throw new IllegalArgumentException(
                "Die Skontofrist darf nicht negativ sein."
            );
        }

        if (
            discountPercent.compareTo(
                BigDecimal.ZERO
            ) > 0
                && discountDays <= 0
        ) {
            throw new IllegalArgumentException(
                "Bei einem Skontosatz größer als 0 "
                    + "muss die Skontofrist größer als 0 sein."
            );
        }

        if (
            discountPercent.compareTo(
                BigDecimal.ZERO
            ) == 0
                && discountDays != 0
        ) {
            throw new IllegalArgumentException(
                "Wenn kein Skonto gewährt wird, muss "
                    + "die Skontofrist 0 sein."
            );
        }

        if (
            request.paymentTermDays() != null
                && discountDays
                > request.paymentTermDays()
        ) {
            throw new IllegalArgumentException(
                "Die Skontofrist darf das Zahlungsziel "
                    + "nicht überschreiten."
            );
        }
    }

    private void validateItemRequest(
        InvoiceItemRequest request
    ) {
        requirePositiveId(
            request.productId(),
            "Produkt-ID"
        );

        if (
            request.quantity() == null
                || request.quantity().compareTo(
                BigDecimal.ZERO
            ) <= 0
        ) {
            throw new IllegalArgumentException(
                "Die Menge einer Rechnungsposition "
                    + "muss größer als 0 sein."
            );
        }

        if (
            request.unitPriceNet() == null
        ) {
            throw new IllegalArgumentException(
                "Der Netto-Einzelpreis muss "
                    + "angegeben werden."
            );
        }

        if (
            request.unitPriceNet().compareTo(
                BigDecimal.ZERO
            ) < 0
        ) {
            throw new IllegalArgumentException(
                "Der Netto-Einzelpreis darf "
                    + "nicht negativ sein."
            );
        }

        if (request.vatRate() == null) {
            throw new IllegalArgumentException(
                "Der Mehrwertsteuersatz muss "
                    + "angegeben werden."
            );
        }

        validatePercent(
            request.vatRate(),
            "Der Mehrwertsteuersatz"
        );

        validatePercent(
            request.discountPercent(),
            "Der Positionsrabatt"
        );
    }

    private void validateAssignments(
        CompanyEntity company,
        CustomerEntity customer,
        BankAccountEntity bankAccount
    ) {
        if (
            customer.getCompany() == null
                || !Objects.equals(
                customer.getCompany().getId(),
                company.getId()
            )
        ) {
            throw new IllegalArgumentException(
                "Der Kunde gehört nicht zur "
                    + "ausgewählten Firma."
            );
        }

        if (
            bankAccount.getCompany() == null
                || !Objects.equals(
                bankAccount.getCompany().getId(),
                company.getId()
            )
        ) {
            throw new IllegalArgumentException(
                "Das Bankkonto gehört nicht zur "
                    + "ausgewählten Firma."
            );
        }

        if (!company.isActive()) {
            throw new IllegalArgumentException(
                "Die ausgewählte Firma ist nicht aktiv."
            );
        }

        if (!customer.isActive()) {
            throw new IllegalArgumentException(
                "Der ausgewählte Kunde ist nicht aktiv."
            );
        }

        if (!bankAccount.isActive()) {
            throw new IllegalArgumentException(
                "Das ausgewählte Bankkonto ist nicht aktiv."
            );
        }

        if (
            bankAccount.getBank() == null
                || !bankAccount.getBank().isActive()
        ) {
            throw new IllegalArgumentException(
                "Die dem Bankkonto zugeordnete Bank "
                    + "ist nicht aktiv oder nicht vorhanden."
            );
        }
    }

    private void validateProductAssignment(
        CompanyEntity company,
        ProductEntity product
    ) {
        if (
            product.getCompany() == null
                || !Objects.equals(
                product.getCompany().getId(),
                company.getId()
            )
        ) {
            throw new IllegalArgumentException(
                "Das Produkt mit ID "
                    + product.getId()
                    + " gehört nicht zur ausgewählten Firma."
            );
        }

        if (!product.isActive()) {
            throw new IllegalArgumentException(
                "Das Produkt mit ID "
                    + product.getId()
                    + " ist nicht aktiv."
            );
        }
    }

    private BankAccountEntity resolveBankAccount(
        Long bankAccountId,
        Long companyId
    ) {
        if (bankAccountId != null) {
            return loadBankAccount(
                bankAccountId
            );
        }

        return bankAccountRepository
            .findByCompany_IdAndDefaultAccountTrue(
                companyId
            )
            .orElseThrow(() ->
                new ResourceNotFoundException(
                    "Für die Firma mit ID "
                        + companyId
                        + " wurde kein Standardbankkonto gefunden."
                )
            );
    }

    private InvoiceEntity loadInvoice(
        Long id
    ) {
        requirePositiveId(
            id,
            "Rechnungs-ID"
        );

        return invoiceRepository
            .findById(
                id
            )
            .orElseThrow(() ->
                new ResourceNotFoundException(
                    "Rechnung mit ID %d wurde nicht gefunden."
                        .formatted(
                            id
                        )
                )
            );
    }

    private CompanyEntity loadCompany(
        Long id
    ) {
        requirePositiveId(
            id,
            "Firmen-ID"
        );

        return companyRepository
            .findById(
                id
            )
            .orElseThrow(() ->
                new ResourceNotFoundException(
                    "Firma mit ID %d wurde nicht gefunden."
                        .formatted(
                            id
                        )
                )
            );
    }

    private CustomerEntity loadCustomer(
        Long id
    ) {
        requirePositiveId(
            id,
            "Kunden-ID"
        );

        return customerRepository
            .findById(
                id
            )
            .orElseThrow(() ->
                new ResourceNotFoundException(
                    "Kunde mit ID %d wurde nicht gefunden."
                        .formatted(
                            id
                        )
                )
            );
    }

    private BankAccountEntity loadBankAccount(
        Long id
    ) {
        requirePositiveId(
            id,
            "Bankkonto-ID"
        );

        return bankAccountRepository
            .findById(
                id
            )
            .orElseThrow(() ->
                new ResourceNotFoundException(
                    "Bankkonto mit ID %d wurde nicht gefunden."
                        .formatted(
                            id
                        )
                )
            );
    }

    private ProductEntity loadProduct(
        Long id
    ) {
        requirePositiveId(
            id,
            "Produkt-ID"
        );

        return productRepository
            .findById(
                id
            )
            .orElseThrow(() ->
                new ResourceNotFoundException(
                    "Produkt mit ID %d wurde nicht gefunden."
                        .formatted(
                            id
                        )
                )
            );
    }

    private void validateUniqueInvoiceNumber(
        InvoiceEntity invoice
    ) {
        String invoiceNumber =
            normalizeText(
                invoice.getInvoiceNumber()
            );

        if (invoiceNumber == null) {
            throw new IllegalStateException(
                "Die Rechnungsnummer fehlt."
            );
        }

        invoiceRepository
            .findByInvoiceNumberIgnoreCase(
                invoiceNumber
            )
            .filter(existing ->
                !Objects.equals(
                    existing.getId(),
                    invoice.getId()
                )
            )
            .ifPresent(existing -> {
                throw new IllegalStateException(
                    "Die Rechnungsnummer "
                        + invoiceNumber
                        + " ist bereits vergeben."
                );
            });
    }

    private void requireNotArchived(
        InvoiceEntity invoice
    ) {
        if (
            Boolean.TRUE.equals(
                invoice.getArchived()
            )
        ) {
            throw new IllegalStateException(
                "Eine archivierte Rechnung darf "
                    + "nicht mehr verändert werden."
            );
        }
    }

    private void requirePositiveId(
        Long id,
        String fieldName
    ) {
        if (
            id == null
                || id <= 0
        ) {
            throw new IllegalArgumentException(
                fieldName
                    + " muss größer als 0 sein."
            );
        }
    }

    private void requireStatus(
        InvoiceEntity invoice,
        InvoiceStatus requiredStatus
    ) {
        Objects.requireNonNull(
            invoice,
            "Die Rechnung darf nicht null sein."
        );

        if (
            invoice.getStatus() != requiredStatus
        ) {
            throw new IllegalStateException(
                "Die Rechnung muss den Status "
                    + requiredStatus
                    + " besitzen. Aktueller Status: "
                    + invoice.getStatus()
                    + "."
            );
        }
    }

    private String generateInvoiceNumber(
        InvoiceEntity invoice
    ) {
        if (invoice.getId() == null) {
            throw new IllegalStateException(
                "Die Rechnungsnummer kann erst nach "
                    + "dem Speichern erzeugt werden."
            );
        }

        if (invoice.getInvoiceDate() == null) {
            throw new IllegalStateException(
                "Für die Rechnungsnummer fehlt "
                    + "das Rechnungsdatum."
            );
        }

        return String.format(
            Locale.ROOT,
            "RE-%d-%06d",
            invoice.getInvoiceDate().getYear(),
            invoice.getId()
        );
    }

    private String createTemporaryInvoiceNumber() {
        return "DRAFT-"
            + java.util.UUID.randomUUID()
            .toString()
            .replace(
                "-",
                ""
            )
            .substring(
                0,
                20
            )
            .toUpperCase(
                Locale.ROOT
            );
    }

    private String resolveDescription(
        String requestedDescription,
        ProductEntity product
    ) {
        String description =
            normalizeMultilineText(
                requestedDescription
            );

        if (description != null) {
            return description;
        }

        description =
            normalizeMultilineText(
                product.getDescription()
            );

        if (description != null) {
            return description;
        }

        description =
            normalizeText(
                product.getName()
            );

        if (description == null) {
            throw new IllegalArgumentException(
                "Für das Produkt mit ID "
                    + product.getId()
                    + " ist keine Beschreibung vorhanden."
            );
        }

        return description;
    }

    private String resolveUnit(
        String requestedUnit,
        ProductEntity product
    ) {
        String unit =
            normalizeText(
                requestedUnit
            );

        if (unit != null) {
            return unit;
        }

        unit =
            normalizeText(
                product.getUnit()
            );

        if (unit == null) {
            throw new IllegalArgumentException(
                "Für das Produkt mit ID "
                    + product.getId()
                    + " ist keine Mengeneinheit vorhanden."
            );
        }

        return unit;
    }

    private String normalizeTaxCategoryCode(
        String value
    ) {
        String normalized =
            normalizeText(
                value
            );

        if (normalized == null) {
            return DEFAULT_TAX_CATEGORY_CODE;
        }

        normalized =
            normalized.toUpperCase(
                Locale.ROOT
            );

        return switch (normalized) {
            case "S", "Z", "E", "AE",
                 "K", "G", "O", "L", "M" ->
                normalized;

            default ->
                throw new IllegalArgumentException(
                    "Ungültige Steuerkategorie: "
                        + normalized
                );
        };
    }

    private String normalizeText(
        String value
    ) {
        if (value == null) {
            return null;
        }

        String normalized =
            value.trim();

        return normalized.isEmpty()
            ? null
            : normalized;
    }

    private String normalizeMultilineText(
        String value
    ) {
        if (value == null) {
            return null;
        }

        String normalized =
            value
                .replace(
                    "\r\n",
                    "\n"
                )
                .replace(
                    '\r',
                    '\n'
                )
                .trim();

        return normalized.isEmpty()
            ? null
            : normalized;
    }

    private String normalizeIban(
        String value
    ) {
        String normalized =
            normalizeText(
                value
            );

        if (normalized == null) {
            throw new IllegalArgumentException(
                "Die IBAN muss angegeben werden."
            );
        }

        return normalized
            .replaceAll(
                "\\s+",
                ""
            )
            .toUpperCase(
                Locale.ROOT
            );
    }

    private BigDecimal quantity(
        BigDecimal value
    ) {
        if (
            value == null
                || value.compareTo(
                BigDecimal.ZERO
            ) <= 0
        ) {
            throw new IllegalArgumentException(
                "Die Menge muss größer als 0 sein."
            );
        }

        return value.setScale(
            QUANTITY_SCALE,
            MONEY_ROUNDING_MODE
        );
    }

    private BigDecimal price(
        BigDecimal value
    ) {
        if (value == null) {
            throw new IllegalArgumentException(
                "Der Netto-Einzelpreis muss "
                    + "angegeben werden."
            );
        }

        if (
            value.compareTo(
                BigDecimal.ZERO
            ) < 0
        ) {
            throw new IllegalArgumentException(
                "Der Netto-Einzelpreis darf "
                    + "nicht negativ sein."
            );
        }

        return value.setScale(
            PRICE_SCALE,
            MONEY_ROUNDING_MODE
        );
    }

    private BigDecimal percent(
        BigDecimal value
    ) {
        BigDecimal normalized =
            zeroIfNull(
                value
            );

        validatePercent(
            normalized,
            "Der Prozentwert"
        );

        return normalized.setScale(
            PERCENT_SCALE,
            MONEY_ROUNDING_MODE
        );
    }

    private void validatePercent(
        BigDecimal value,
        String fieldName
    ) {
        if (value == null) {
            return;
        }

        if (
            value.compareTo(
                BigDecimal.ZERO
            ) < 0
        ) {
            throw new IllegalArgumentException(
                fieldName
                    + " darf nicht negativ sein."
            );
        }

        if (
            value.compareTo(
                ONE_HUNDRED
            ) > 0
        ) {
            throw new IllegalArgumentException(
                fieldName
                    + " darf maximal 100 % betragen."
            );
        }
    }

    private BigDecimal zeroIfNull(
        BigDecimal value
    ) {
        return value == null
            ? BigDecimal.ZERO
            : value;
    }
}
