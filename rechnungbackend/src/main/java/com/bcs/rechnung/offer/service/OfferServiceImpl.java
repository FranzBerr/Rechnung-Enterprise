package com.bcs.rechnung.offer.service;

import com.bcs.rechnung.company.CompanyEntity;
import com.bcs.rechnung.company.CompanyRepository;
import com.bcs.rechnung.customer.CustomerEntity;
import com.bcs.rechnung.customer.CustomerRepository;
import com.bcs.rechnung.exception.ResourceNotFoundException;
import com.bcs.rechnung.offer.OfferEntity;
import com.bcs.rechnung.offer.OfferItemEntity;
import com.bcs.rechnung.offer.OfferStatus;
import com.bcs.rechnung.offer.dto.OfferDto;
import com.bcs.rechnung.offer.dto.OfferItemRequest;
import com.bcs.rechnung.offer.dto.OfferRequest;
import com.bcs.rechnung.offer.mapper.OfferMapper;
import com.bcs.rechnung.offer.repository.OfferRepository;
import com.bcs.rechnung.product.ProductEntity;
import com.bcs.rechnung.product.ProductRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class OfferServiceImpl
    implements OfferService {

    private static final int MONEY_SCALE =
        2;

    private static final int CALCULATION_SCALE =
        8;

    private static final RoundingMode ROUNDING_MODE =
        RoundingMode.HALF_UP;

    private static final BigDecimal ONE_HUNDRED =
        BigDecimal.valueOf(100);

    private static final String DEFAULT_CURRENCY =
        "EUR";

    private static final int DEFAULT_PAYMENT_TERM_DAYS =
        14;

    private static final String OFFER_NUMBER_PREFIX =
        "AN";

    private static final int OFFER_NUMBER_LENGTH =
        6;

    private final OfferRepository offerRepository;

    private final CompanyRepository companyRepository;

    private final CustomerRepository customerRepository;

    private final ProductRepository productRepository;

    private final OfferMapper offerMapper;

    private final EntityManager entityManager;


    // ---------------------------------------------------------
    // Lesen
    // ---------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public List<OfferDto> findAll() {

        return offerMapper.toDtos(
            offerRepository
                .findAllByOrderByOfferDateDesc()
        );
    }


    @Override
    @Transactional(readOnly = true)
    public OfferDto findById(
        Long id
    ) {

        return offerMapper.toDto(
            findOffer(
                id
            )
        );
    }


    // ---------------------------------------------------------
    // Erstellen
    // ---------------------------------------------------------

    @Override
    public OfferDto create(
        OfferRequest request
    ) {

        validateRequest(
            request
        );

        String offerNumber =
            generateOfferNumber(
                request.offerDate()
            );

        CompanyEntity company =
            findCompany(
                request.companyId()
            );

        CustomerEntity customer =
            findCustomer(
                request.customerId()
            );

        OfferEntity offer =
            new OfferEntity();

        offer.setOfferNumber(
            offerNumber
        );

        offer.setOfferDate(
            request.offerDate()
        );

        offer.setValidUntil(
            request.validUntil()
        );

        offer.setCompany(
            company
        );

        offer.setCustomer(
            customer
        );

        applyHeader(
            offer,
            request
        );

        applyCustomerSnapshot(
            offer,
            request,
            customer
        );

        addItems(
            offer,
            request.items()
        );

        calculateOfferTotals(
            offer
        );

        OfferEntity saved =
            offerRepository.saveAndFlush(
                offer
            );

        return offerMapper.toDto(
            saved
        );
    }


    // ---------------------------------------------------------
    // Ändern
    // ---------------------------------------------------------

    @Override
    public OfferDto update(
        Long id,
        OfferRequest request
    ) {

        validateRequest(
            request
        );

        OfferEntity offer =
            findOffer(
                id
            );

        validateNotArchived(
            offer
        );

        CompanyEntity company =
            findCompany(
                request.companyId()
            );

        CustomerEntity customer =
            findCustomer(
                request.customerId()
            );

        offer.setOfferDate(
            request.offerDate()
        );

        offer.setValidUntil(
            request.validUntil()
        );

        offer.setCompany(
            company
        );

        offer.setCustomer(
            customer
        );

        applyHeader(
            offer,
            request
        );

        applyCustomerSnapshot(
            offer,
            request,
            customer
        );

        clearItemsAndFlush(
            offer
        );

        addItems(
            offer,
            request.items()
        );

        calculateOfferTotals(
            offer
        );

        entityManager.flush();

        return offerMapper.toDto(
            offer
        );
    }


    // ---------------------------------------------------------
    // Löschen
    // ---------------------------------------------------------

    @Override
    public void delete(
        Long id
    ) {

        OfferEntity offer =
            findOffer(
                id
            );

        validateNotArchived(
            offer
        );

        offerRepository.delete(
            offer
        );
    }


    // ---------------------------------------------------------
    // Angebotsnummer
    // ---------------------------------------------------------

    private synchronized String generateOfferNumber(
        LocalDate offerDate
    ) {

        if (offerDate == null) {
            throw new IllegalArgumentException(
                "Das Angebotsdatum darf nicht null sein."
            );
        }

        String prefix =
            OFFER_NUMBER_PREFIX
                + "-"
                + offerDate.getYear()
                + "-";

        int lastSequence =
            offerRepository
                .findTopByOfferNumberStartingWithOrderByOfferNumberDesc(
                    prefix
                )
                .map(
                    OfferEntity::getOfferNumber
                )
                .map(
                    offerNumber ->
                        extractSequenceNumber(
                            offerNumber,
                            prefix
                        )
                )
                .orElse(
                    0
                );

        int nextSequence =
            lastSequence + 1;

        String offerNumber =
            prefix
                + String.format(
                "%0"
                    + OFFER_NUMBER_LENGTH
                    + "d",
                nextSequence
            );

        validateOfferNumber(
            null,
            offerNumber
        );

        return offerNumber;
    }


    private int extractSequenceNumber(
        String offerNumber,
        String prefix
    ) {

        if (
            offerNumber == null
                || !offerNumber.startsWith(
                prefix
            )
        ) {
            throw new IllegalStateException(
                "Ungültige Angebotsnummer: "
                    + offerNumber
            );
        }

        String sequence =
            offerNumber.substring(
                prefix.length()
            );

        try {
            return Integer.parseInt(
                sequence
            );
        } catch (
            NumberFormatException exception
        ) {
            throw new IllegalStateException(
                "Ungültige Angebotsnummer: "
                    + offerNumber,
                exception
            );
        }
    }


    // ---------------------------------------------------------
    // Angebotskopf
    // ---------------------------------------------------------

    private void applyHeader(
        OfferEntity offer,
        OfferRequest request
    ) {

        offer.setStatus(
            request.status() == null
                ? OfferStatus.DRAFT
                : request.status()
        );

        offer.setCurrencyCode(
            defaultValue(
                request.currencyCode(),
                DEFAULT_CURRENCY
            )
        );

        offer.setPaymentTermDays(
            request.paymentTermDays() == null
                ? DEFAULT_PAYMENT_TERM_DAYS
                : request.paymentTermDays()
        );

        offer.setIntroductionText(
            normalizeNullable(
                request.introductionText()
            )
        );

        offer.setClosingText(
            normalizeNullable(
                request.closingText()
            )
        );

        offer.setNotes(
            normalizeNullable(
                request.notes()
            )
        );

        offer.setActive(
            request.active()
        );
    }


    // ---------------------------------------------------------
    // Kundensnapshot
    // ---------------------------------------------------------

    private void applyCustomerSnapshot(
        OfferEntity offer,
        OfferRequest request,
        CustomerEntity customer
    ) {

        offer.setCustomerNumber(
            normalizeRequired(
                customer.getCustomerNumber(),
                "Kundennummer"
            )
        );

        offer.setCustomerName(
            fallbackRequired(
                request.customerName(),
                customer.getName(),
                "Kundenname"
            )
        );

        offer.setCustomerContactPerson(
            fallback(
                request.customerContactPerson(),
                customer.getContactPerson()
            )
        );

        offer.setCustomerDepartment(
            fallback(
                request.customerDepartment(),
                customer.getDepartment()
            )
        );

        offer.setCustomerStreet(
            fallback(
                request.customerStreet(),
                customer.getStreet()
            )
        );

        offer.setCustomerZipCode(
            fallback(
                request.customerZipCode(),
                customer.getZipCode()
            )
        );

        offer.setCustomerCity(
            fallback(
                request.customerCity(),
                customer.getCity()
            )
        );

        offer.setCustomerCountry(
            fallback(
                request.customerCountry(),
                customer.getCountry()
            )
        );

        offer.setCustomerVatId(
            fallback(
                request.customerVatId(),
                customer.getVatId()
            )
        );
    }


    // ---------------------------------------------------------
    // Bestehende Positionen entfernen
    // ---------------------------------------------------------

    private void clearItemsAndFlush(
        OfferEntity offer
    ) {

        offer.clearItems();

        entityManager.flush();
    }


    // ---------------------------------------------------------
    // Neue Positionen hinzufügen
    // ---------------------------------------------------------

    private void addItems(
        OfferEntity offer,
        List<OfferItemRequest> requests
    ) {

        validateUniquePositionNumbers(
            requests
        );

        if (requests == null) {
            return;
        }

        for (
            OfferItemRequest request :
            requests
        ) {

            OfferItemEntity item =
                createItem(
                    request
                );

            offer.addItem(
                item
            );
        }
    }


    // ---------------------------------------------------------
    // Position erstellen
    // ---------------------------------------------------------

    private OfferItemEntity createItem(
        OfferItemRequest request
    ) {

        validateItemRequest(
            request
        );

        OfferItemEntity item =
            new OfferItemEntity();

        item.setPositionNumber(
            request.positionNumber()
        );

        item.setQuantity(
            request.quantity()
        );

        item.setDiscountPercent(
            zeroIfNull(
                request.discountPercent()
            )
        );

        item.setVatRate(
            request.vatRate()
        );

        item.setOptional(
            request.optional()
        );

        item.setNotes(
            normalizeNullable(
                request.notes()
            )
        );

        if (
            request.productId() != null
        ) {
            applyProductSnapshot(
                item,
                request
            );
        } else {
            applyFreeItem(
                item,
                request
            );
        }

        calculateItemAmounts(
            item
        );

        return item;
    }


    // ---------------------------------------------------------
    // Produktposition
    // ---------------------------------------------------------

    private void applyProductSnapshot(
        OfferItemEntity item,
        OfferItemRequest request
    ) {

        ProductEntity product =
            findProduct(
                request.productId()
            );

        item.setProduct(
            product
        );

        item.setProductNumber(
            normalizeNullable(
                product.getProductNumber()
            )
        );

        item.setProductName(
            normalizeRequired(
                product.getName(),
                "Produktbezeichnung"
            )
        );

        item.setDescription(
            fallback(
                request.description(),
                product.getDescription()
            )
        );

        item.setUnit(
            fallbackRequired(
                request.unit(),
                product.getUnit(),
                "Einheit"
            )
        );

        BigDecimal unitPriceNet =
            request.unitPriceNet() == null
                ? product.getNetPrice()
                : request.unitPriceNet();

        validateUnitPrice(
            unitPriceNet
        );

        item.setUnitPriceNet(
            unitPriceNet
        );
    }


    // ---------------------------------------------------------
    // Freie Position
    // ---------------------------------------------------------

    private void applyFreeItem(
        OfferItemEntity item,
        OfferItemRequest request
    ) {

        item.setProduct(
            null
        );

        item.setProductNumber(
            normalizeNullable(
                request.productNumber()
            )
        );

        item.setProductName(
            normalizeRequired(
                request.productName(),
                "Produktbezeichnung"
            )
        );

        item.setDescription(
            normalizeNullable(
                request.description()
            )
        );

        item.setUnit(
            normalizeRequired(
                request.unit(),
                "Einheit"
            )
        );

        validateUnitPrice(
            request.unitPriceNet()
        );

        item.setUnitPriceNet(
            request.unitPriceNet()
        );
    }


    // ---------------------------------------------------------
    // Positionsberechnung
    // ---------------------------------------------------------

    private void calculateItemAmounts(
        OfferItemEntity item
    ) {

        BigDecimal quantity =
            item.getQuantity();

        BigDecimal unitPriceNet =
            item.getUnitPriceNet();

        BigDecimal discountPercent =
            zeroIfNull(
                item.getDiscountPercent()
            );

        BigDecimal vatRate =
            zeroIfNull(
                item.getVatRate()
            );

        BigDecimal amountBeforeDiscount =
            quantity.multiply(
                unitPriceNet
            );

        BigDecimal discountAmount =
            amountBeforeDiscount
                .multiply(
                    discountPercent
                )
                .divide(
                    ONE_HUNDRED,
                    CALCULATION_SCALE,
                    ROUNDING_MODE
                );

        BigDecimal lineNet =
            amountBeforeDiscount
                .subtract(
                    discountAmount
                )
                .setScale(
                    MONEY_SCALE,
                    ROUNDING_MODE
                );

        BigDecimal taxAmount =
            lineNet
                .multiply(
                    vatRate
                )
                .divide(
                    ONE_HUNDRED,
                    MONEY_SCALE,
                    ROUNDING_MODE
                );

        BigDecimal lineGross =
            lineNet
                .add(
                    taxAmount
                )
                .setScale(
                    MONEY_SCALE,
                    ROUNDING_MODE
                );

        item.setLineNet(
            lineNet
        );

        item.setTaxAmount(
            taxAmount
        );

        item.setLineGross(
            lineGross
        );
    }


    // ---------------------------------------------------------
    // Angebotssummen
    // ---------------------------------------------------------

    private void calculateOfferTotals(
        OfferEntity offer
    ) {

        BigDecimal subtotalNet =
            offer.getItems()
                .stream()
                .filter(
                    item ->
                        !item.isOptional()
                )
                .map(
                    OfferItemEntity::getLineNet
                )
                .map(
                    this::zeroIfNull
                )
                .reduce(
                    BigDecimal.ZERO,
                    BigDecimal::add
                )
                .setScale(
                    MONEY_SCALE,
                    ROUNDING_MODE
                );

        BigDecimal taxAmount =
            offer.getItems()
                .stream()
                .filter(
                    item ->
                        !item.isOptional()
                )
                .map(
                    OfferItemEntity::getTaxAmount
                )
                .map(
                    this::zeroIfNull
                )
                .reduce(
                    BigDecimal.ZERO,
                    BigDecimal::add
                )
                .setScale(
                    MONEY_SCALE,
                    ROUNDING_MODE
                );

        BigDecimal totalGross =
            subtotalNet
                .add(
                    taxAmount
                )
                .setScale(
                    MONEY_SCALE,
                    ROUNDING_MODE
                );

        offer.setSubtotalNet(
            subtotalNet
        );

        offer.setTaxAmount(
            taxAmount
        );

        offer.setTotalGross(
            totalGross
        );
    }


    // ---------------------------------------------------------
    // Entity suchen
    // ---------------------------------------------------------

    private OfferEntity findOffer(
        Long id
    ) {

        validateId(
            id,
            "Angebots-ID"
        );

        return offerRepository
            .findById(
                id
            )
            .orElseThrow(() ->
                new ResourceNotFoundException(
                    "Angebot mit ID "
                        + id
                        + " wurde nicht gefunden."
                )
            );
    }


    private CompanyEntity findCompany(
        Long id
    ) {

        validateId(
            id,
            "Firmen-ID"
        );

        return companyRepository
            .findById(
                id
            )
            .orElseThrow(() ->
                new ResourceNotFoundException(
                    "Firma mit ID "
                        + id
                        + " wurde nicht gefunden."
                )
            );
    }


    private CustomerEntity findCustomer(
        Long id
    ) {

        validateId(
            id,
            "Kunden-ID"
        );

        return customerRepository
            .findById(
                id
            )
            .orElseThrow(() ->
                new ResourceNotFoundException(
                    "Kunde mit ID "
                        + id
                        + " wurde nicht gefunden."
                )
            );
    }


    private ProductEntity findProduct(
        Long id
    ) {

        validateId(
            id,
            "Produkt-ID"
        );

        return productRepository
            .findById(
                id
            )
            .orElseThrow(() ->
                new ResourceNotFoundException(
                    "Produkt mit ID "
                        + id
                        + " wurde nicht gefunden."
                )
            );
    }


    // ---------------------------------------------------------
    // Request validieren
    // ---------------------------------------------------------

    private void validateRequest(
        OfferRequest request
    ) {

        if (
            request == null
        ) {
            throw new IllegalArgumentException(
                "OfferRequest darf nicht null sein."
            );
        }

        if (
            request.offerDate() == null
        ) {
            throw new IllegalArgumentException(
                "Das Angebotsdatum darf nicht null sein."
            );
        }

        if (
            request.companyId() == null
        ) {
            throw new IllegalArgumentException(
                "Die Firmen-ID darf nicht null sein."
            );
        }

        if (
            request.customerId() == null
        ) {
            throw new IllegalArgumentException(
                "Die Kunden-ID darf nicht null sein."
            );
        }

        if (
            request.validUntil() != null
                && request.validUntil()
                .isBefore(
                    request.offerDate()
                )
        ) {
            throw new IllegalArgumentException(
                "Das Gültigkeitsdatum darf nicht "
                    + "vor dem Angebotsdatum liegen."
            );
        }

        if (
            request.paymentTermDays() != null
                && request.paymentTermDays() < 0
        ) {
            throw new IllegalArgumentException(
                "Das Zahlungsziel darf nicht negativ sein."
            );
        }
    }


    private void validateItemRequest(
        OfferItemRequest request
    ) {

        if (
            request == null
        ) {
            throw new IllegalArgumentException(
                "OfferItemRequest darf nicht null sein."
            );
        }

        if (
            request.positionNumber() == null
                || request.positionNumber() <= 0
        ) {
            throw new IllegalArgumentException(
                "Die Positionsnummer muss größer als 0 sein."
            );
        }

        if (
            request.quantity() == null
                || request.quantity()
                .compareTo(
                    BigDecimal.ZERO
                ) <= 0
        ) {
            throw new IllegalArgumentException(
                "Die Menge muss größer als 0 sein."
            );
        }

        if (
            request.vatRate() == null
        ) {
            throw new IllegalArgumentException(
                "Der Umsatzsteuersatz darf nicht null sein."
            );
        }

        validatePercentage(
            request.vatRate(),
            "Umsatzsteuersatz"
        );

        validatePercentage(
            zeroIfNull(
                request.discountPercent()
            ),
            "Rabatt"
        );

        if (
            request.productId() == null
                && request.unitPriceNet() == null
        ) {
            throw new IllegalArgumentException(
                "Bei einer freien Position muss der "
                    + "Netto-Einzelpreis angegeben werden."
            );
        }
    }


    private void validateUniquePositionNumbers(
        List<OfferItemRequest> requests
    ) {

        if (
            requests == null
        ) {
            return;
        }

        Set<Integer> positionNumbers =
            new HashSet<>();

        for (
            OfferItemRequest request :
            requests
        ) {

            if (
                request == null
                    || request.positionNumber() == null
            ) {
                continue;
            }

            if (
                !positionNumbers.add(
                    request.positionNumber()
                )
            ) {
                throw new IllegalArgumentException(
                    "Die Positionsnummer "
                        + request.positionNumber()
                        + " ist mehrfach vorhanden."
                );
            }
        }
    }


    private void validateOfferNumber(
        Long currentId,
        String offerNumber
    ) {

        offerRepository
            .findByOfferNumber(
                offerNumber
            )
            .filter(existing ->
                currentId == null
                    || !existing
                    .getId()
                    .equals(
                        currentId
                    )
            )
            .ifPresent(existing -> {
                throw new IllegalArgumentException(
                    "Die Angebotsnummer "
                        + offerNumber
                        + " existiert bereits."
                );
            });
    }


    private void validateNotArchived(
        OfferEntity offer
    ) {

        if (
            offer.isArchived()
        ) {
            throw new IllegalStateException(
                "Das Angebot "
                    + offer.getOfferNumber()
                    + " ist bereits archiviert und darf "
                    + "nicht mehr geändert oder gelöscht werden."
            );
        }
    }


    private void validateUnitPrice(
        BigDecimal unitPrice
    ) {

        if (
            unitPrice == null
        ) {
            throw new IllegalArgumentException(
                "Der Netto-Einzelpreis darf nicht null sein."
            );
        }

        if (
            unitPrice.compareTo(
                BigDecimal.ZERO
            ) < 0
        ) {
            throw new IllegalArgumentException(
                "Der Netto-Einzelpreis darf nicht negativ sein."
            );
        }
    }


    private void validatePercentage(
        BigDecimal value,
        String fieldName
    ) {

        if (
            value == null
        ) {
            throw new IllegalArgumentException(
                fieldName
                    + " darf nicht null sein."
            );
        }

        if (
            value.compareTo(
                BigDecimal.ZERO
            ) < 0
                || value.compareTo(
                ONE_HUNDRED
            ) > 0
        ) {
            throw new IllegalArgumentException(
                fieldName
                    + " muss zwischen 0 und 100 liegen."
            );
        }
    }


    private void validateId(
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


    // ---------------------------------------------------------
    // Texte
    // ---------------------------------------------------------

    private String fallback(
        String value,
        String fallbackValue
    ) {

        String normalized =
            normalizeNullable(
                value
            );

        if (
            normalized != null
        ) {
            return normalized;
        }

        return normalizeNullable(
            fallbackValue
        );
    }


    private String fallbackRequired(
        String value,
        String fallbackValue,
        String fieldName
    ) {

        String result =
            fallback(
                value,
                fallbackValue
            );

        if (
            result == null
        ) {
            throw new IllegalArgumentException(
                fieldName
                    + " darf nicht leer sein."
            );
        }

        return result;
    }


    private String defaultValue(
        String value,
        String defaultValue
    ) {

        String normalized =
            normalizeNullable(
                value
            );

        return normalized == null
            ? defaultValue
            : normalized;
    }


    private String normalizeRequired(
        String value,
        String fieldName
    ) {

        String normalized =
            normalizeNullable(
                value
            );

        if (
            normalized == null
        ) {
            throw new IllegalArgumentException(
                fieldName
                    + " darf nicht leer sein."
            );
        }

        return normalized;
    }


    private String normalizeNullable(
        String value
    ) {

        if (
            value == null
        ) {
            return null;
        }

        String normalized =
            value.trim();

        return normalized.isEmpty()
            ? null
            : normalized;
    }


    // ---------------------------------------------------------
    // Zahlen
    // ---------------------------------------------------------

    private BigDecimal zeroIfNull(
        BigDecimal value
    ) {

        return value == null
            ? BigDecimal.ZERO
            : value;
    }
}
