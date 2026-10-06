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

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class InvoiceServiceImpl implements InvoiceService {

    private static final int MONEY_SCALE = 2;

    private static final RoundingMode MONEY_ROUNDING_MODE =
        RoundingMode.HALF_UP;

    private final InvoiceRepository invoiceRepository;
    private final CompanyRepository companyRepository;
    private final CustomerRepository customerRepository;
    private final BankAccountRepository bankAccountRepository;
    private final ProductRepository productRepository;
    private final InvoiceMapper invoiceMapper;

    @Override
    @Transactional(readOnly = true)
    public List<InvoiceDto> findAll() {
        return invoiceRepository.findAll()
            .stream()
            .map(invoiceMapper::toDto)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<InvoiceDto> findByStatus(
        InvoiceStatus status
    ) {
        return invoiceRepository
            .findByStatusOrderByInvoiceDateDesc(status)
            .stream()
            .map(invoiceMapper::toDto)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<InvoiceDto> findByCompanyId(
        Long companyId
    ) {
        return invoiceRepository
            .findByCompany_IdOrderByInvoiceDateDesc(companyId)
            .stream()
            .map(invoiceMapper::toDto)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<InvoiceDto> findByCustomerId(
        Long customerId
    ) {
        return invoiceRepository
            .findByCustomer_IdOrderByInvoiceDateDesc(customerId)
            .stream()
            .map(invoiceMapper::toDto)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public InvoiceDto findById(
        Long id
    ) {
        return invoiceMapper.toDto(
            loadInvoice(id)
        );
    }

    @Override
    public InvoiceDto create(
        InvoiceRequest request
    ) {
        CompanyEntity company =
            loadCompany(request.companyId());

        CustomerEntity customer =
            loadCustomer(request.customerId());

        BankAccountEntity bankAccount =
            loadBankAccount(request.bankAccountId());

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

        invoice.replaceItems(items);
        calculateTotals(invoice);

        InvoiceEntity savedInvoice =
            invoiceRepository.save(invoice);

        log.info(
            "Rechnungsentwurf mit ID {} erstellt.",
            savedInvoice.getId()
        );

        return invoiceMapper.toDto(savedInvoice);
    }

    @Override
    public InvoiceDto complete(
        Long id
    ) {
        InvoiceEntity invoice = loadInvoice(id);

        requireStatus(
            invoice,
            InvoiceStatus.DRAFT
        );

        invoice.setInvoiceNumber(
            generateInvoiceNumber(invoice)
        );

        invoice.setStatus(
            InvoiceStatus.CREATED
        );

        InvoiceEntity savedInvoice =
            invoiceRepository.save(invoice);

        return invoiceMapper.toDto(savedInvoice);
    }

    @Override
    public InvoiceDto markAsSent(
        Long id
    ) {
        InvoiceEntity invoice = loadInvoice(id);

        requireStatus(
            invoice,
            InvoiceStatus.CREATED
        );

        invoice.setStatus(
            InvoiceStatus.SENT
        );

        return invoiceMapper.toDto(
            invoiceRepository.save(invoice)
        );
    }

    @Override
    public InvoiceDto markAsPaid(
        Long id
    ) {
        InvoiceEntity invoice = loadInvoice(id);

        if (invoice.getStatus() != InvoiceStatus.CREATED
            && invoice.getStatus() != InvoiceStatus.SENT) {

            throw new IllegalStateException(
                "Nur erstellte oder versendete Rechnungen "
                    + "können als bezahlt markiert werden."
            );
        }

        invoice.setStatus(
            InvoiceStatus.PAID
        );

        return invoiceMapper.toDto(
            invoiceRepository.save(invoice)
        );
    }

    @Override
    public InvoiceDto cancel(
        Long id
    ) {
        InvoiceEntity invoice = loadInvoice(id);

        if (invoice.getStatus() == InvoiceStatus.PAID) {
            throw new IllegalStateException(
                "Eine bezahlte Rechnung kann nicht storniert werden."
            );
        }

        if (invoice.getStatus() == InvoiceStatus.CANCELLED) {
            throw new IllegalStateException(
                "Die Rechnung ist bereits storniert."
            );
        }

        invoice.setStatus(
            InvoiceStatus.CANCELLED
        );

        return invoiceMapper.toDto(
            invoiceRepository.save(invoice)
        );
    }

    @Override
    public void deleteDraft(
        Long id
    ) {
        InvoiceEntity invoice = loadInvoice(id);

        requireStatus(
            invoice,
            InvoiceStatus.DRAFT
        );

        invoiceRepository.delete(invoice);
    }

    private InvoiceEntity createInvoiceHeader(
        InvoiceRequest request,
        CompanyEntity company,
        CustomerEntity customer,
        BankAccountEntity bankAccount
    ) {
        InvoiceEntity invoice =
            new InvoiceEntity();

        invoice.setCompany(company);
        invoice.setCustomer(customer);
        invoice.setBankAccount(bankAccount);

        invoice.setInvoiceDate(
            request.invoiceDate()
        );

        invoice.setServiceDate(
            request.serviceDate()
        );

        invoice.setPaymentTermDays(
            request.paymentTermDays()
        );

        invoice.setDueDate(
            request.invoiceDate()
                .plusDays(
                    request.paymentTermDays()
                )
        );

        invoice.setDiscountPercent(
            zeroIfNull(
                request.discountPercent()
            )
        );

        invoice.setDiscountDays(
            request.discountDays() == null
                ? 0
                : request.discountDays()
        );

        invoice.setNotes(
            normalizeText(request.notes())
        );

        invoice.setStatus(
            InvoiceStatus.DRAFT
        );

        applyCustomerSnapshot(
            invoice,
            customer
        );

        applyBankSnapshot(
            invoice,
            bankAccount
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
    }

    private void applyBankSnapshot(
        InvoiceEntity invoice,
        BankAccountEntity bankAccount
    ) {
        invoice.setBankName(
            bankAccount.getBank()
                .getBankName()
        );

        invoice.setIban(
            bankAccount.getIban()
        );

        invoice.setBic(
            bankAccount.getBank()
                .getBic()
        );
    }

    private List<InvoiceItemEntity> createItems(
        List<InvoiceItemRequest> requests,
        CompanyEntity company
    ) {
        List<InvoiceItemEntity> items =
            new ArrayList<>(requests.size());

        int positionNumber = 1;

        for (InvoiceItemRequest request : requests) {
            ProductEntity product =
                loadProduct(request.productId());

            if (!product.getCompany()
                .getId()
                .equals(company.getId())) {

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

            items.add(
                createItem(
                    positionNumber,
                    request,
                    product
                )
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

        BigDecimal unitPrice =
            request.unitPriceNet() == null
                ? product.getNetPrice()
                : request.unitPriceNet();

        BigDecimal discountPercent =
            zeroIfNull(
                request.discountPercent()
            );

        InvoiceItemEntity item =
            new InvoiceItemEntity();

        item.setProductId(
            product.getId()
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
            request.quantity()
        );

        item.setUnit(
            product.getUnit()
        );

        item.setUnitPriceNet(
            unitPrice
        );

        item.setDiscountPercent(
            discountPercent
        );

        item.setVatRate(
            request.vatRate()
        );

        calculateItemAmounts(
            item
        );

        return item;
    }

    private void calculateItemAmounts(
        InvoiceItemEntity item
    ) {
        BigDecimal discountFactor =
            BigDecimal.ONE.subtract(
                item.getDiscountPercent()
                    .movePointLeft(2)
            );

        BigDecimal lineNet =
            item.getQuantity()
                .multiply(item.getUnitPriceNet())
                .multiply(discountFactor)
                .setScale(
                    MONEY_SCALE,
                    MONEY_ROUNDING_MODE
                );

        BigDecimal taxAmount =
            lineNet
                .multiply(
                    item.getVatRate()
                        .movePointLeft(2)
                )
                .setScale(
                    MONEY_SCALE,
                    MONEY_ROUNDING_MODE
                );

        BigDecimal lineGross =
            lineNet
                .add(taxAmount)
                .setScale(
                    MONEY_SCALE,
                    MONEY_ROUNDING_MODE
                );

        item.setLineNet(lineNet);
        item.setTaxAmount(taxAmount);
        item.setLineGross(lineGross);
    }

    private void calculateTotals(
        InvoiceEntity invoice
    ) {
        BigDecimal subtotalNet =
            invoice.getItems()
                .stream()
                .map(InvoiceItemEntity::getLineNet)
                .reduce(
                    BigDecimal.ZERO,
                    BigDecimal::add
                )
                .setScale(
                    MONEY_SCALE,
                    MONEY_ROUNDING_MODE
                );

        BigDecimal taxAmount =
            invoice.getItems()
                .stream()
                .map(InvoiceItemEntity::getTaxAmount)
                .reduce(
                    BigDecimal.ZERO,
                    BigDecimal::add
                )
                .setScale(
                    MONEY_SCALE,
                    MONEY_ROUNDING_MODE
                );

        invoice.setSubtotalNet(subtotalNet);
        invoice.setTaxAmount(taxAmount);

        invoice.setTotalGross(
            subtotalNet
                .add(taxAmount)
                .setScale(
                    MONEY_SCALE,
                    MONEY_ROUNDING_MODE
                )
        );
    }

    private void validateAssignments(
        CompanyEntity company,
        CustomerEntity customer,
        BankAccountEntity bankAccount
    ) {
        if (!customer.getCompany()
            .getId()
            .equals(company.getId())) {

            throw new IllegalArgumentException(
                "Der Kunde gehört nicht zur ausgewählten Firma."
            );
        }

        if (!bankAccount.getCompany()
            .getId()
            .equals(company.getId())) {

            throw new IllegalArgumentException(
                "Das Bankkonto gehört nicht zur ausgewählten Firma."
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
    }

    private InvoiceEntity loadInvoice(
        Long id
    ) {
        return invoiceRepository.findById(id)
            .orElseThrow(() ->
                new ResourceNotFoundException(
                    "Rechnung mit ID %d wurde nicht gefunden."
                        .formatted(id)
                )
            );
    }

    private CompanyEntity loadCompany(
        Long id
    ) {
        return companyRepository.findById(id)
            .orElseThrow(() ->
                new ResourceNotFoundException(
                    "Firma mit ID %d wurde nicht gefunden."
                        .formatted(id)
                )
            );
    }

    private CustomerEntity loadCustomer(
        Long id
    ) {
        return customerRepository.findById(id)
            .orElseThrow(() ->
                new ResourceNotFoundException(
                    "Kunde mit ID %d wurde nicht gefunden."
                        .formatted(id)
                )
            );
    }

    private BankAccountEntity loadBankAccount(
        Long id
    ) {
        return bankAccountRepository.findById(id)
            .orElseThrow(() ->
                new ResourceNotFoundException(
                    "Bankkonto mit ID %d wurde nicht gefunden."
                        .formatted(id)
                )
            );
    }

    private ProductEntity loadProduct(
        Long id
    ) {
        return productRepository.findById(id)
            .orElseThrow(() ->
                new ResourceNotFoundException(
                    "Produkt mit ID %d wurde nicht gefunden."
                        .formatted(id)
                )
            );
    }

    private void requireStatus(
        InvoiceEntity invoice,
        InvoiceStatus requiredStatus
    ) {
        if (invoice.getStatus() != requiredStatus) {
            throw new IllegalStateException(
                "Die Rechnung muss den Status "
                    + requiredStatus
                    + " besitzen."
            );
        }
    }

    private String generateInvoiceNumber(
        InvoiceEntity invoice
    ) {
        return String.format(
            Locale.ROOT,
            "RE-%d-%06d",
            invoice.getInvoiceDate().getYear(),
            invoice.getId()
        );
    }

    private String resolveDescription(
        String requestedDescription,
        ProductEntity product
    ) {
        String description =
            normalizeText(requestedDescription);

        if (description != null) {
            return description;
        }

        description =
            normalizeText(product.getDescription());

        if (description != null) {
            return description;
        }

        return product.getName();
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

    private BigDecimal zeroIfNull(
        BigDecimal value
    ) {
        return value == null
            ? BigDecimal.ZERO
            : value;
    }
}
