package com.bcs.rechnung.invoice.xrechnung;

import com.bcs.rechnung.company.CompanyEntity;
import com.bcs.rechnung.exception.ResourceNotFoundException;
import com.bcs.rechnung.invoice.InvoiceEntity;
import com.bcs.rechnung.invoice.InvoiceItemEntity;
import com.bcs.rechnung.invoice.InvoiceRepository;
import com.bcs.rechnung.invoice.xrechnung.dto.XRechnungBuyerDto;
import com.bcs.rechnung.invoice.xrechnung.dto.XRechnungDto;
import com.bcs.rechnung.invoice.xrechnung.dto.XRechnungItemDto;
import com.bcs.rechnung.invoice.xrechnung.dto.XRechnungSellerDto;
import com.bcs.rechnung.invoice.xrechnung.dto.XRechnungTotalsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InvoiceXmlServiceImpl implements InvoiceXmlService {

    private final InvoiceRepository invoiceRepository;
    private final InvoiceXmlGenerator invoiceXmlGenerator;

    @Override
    public byte[] generateXml(Long invoiceId) {
        validateInvoiceId(invoiceId);

        InvoiceEntity invoice = invoiceRepository.findById(invoiceId)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Rechnung mit ID " + invoiceId + " wurde nicht gefunden."
            ));

        validateInvoice(invoice);

        XRechnungDto xRechnung = mapInvoice(invoice);

        return invoiceXmlGenerator.generate(xRechnung);
    }

    private XRechnungDto mapInvoice(InvoiceEntity invoice) {
        XRechnungDto dto = new XRechnungDto();

        dto.setInvoiceNumber(normalize(invoice.getInvoiceNumber()));
        dto.setInvoiceDate(invoice.getInvoiceDate());
        dto.setServiceDate(invoice.getServiceDate());
        dto.setDueDate(invoice.getDueDate());
        dto.setBuyerReference(normalize(invoice.getBuyerReference()));
        dto.setOrderReference(normalize(invoice.getOrderReference()));
        dto.setInvoiceTypeCode(defaultValue(invoice.getInvoiceTypeCode(), "380"));
        dto.setPaymentMeansCode(defaultValue(invoice.getPaymentMeansCode(), "58"));
        dto.setCurrencyCode(defaultValue(invoice.getCurrencyCode(), "EUR"));
        dto.setPaymentReference(defaultValue(
            invoice.getPaymentReference(),
            invoice.getInvoiceNumber()
        ));

        dto.setBankName(invoice.getBankName());
        dto.setIban(invoice.getIban());
        dto.setBic(invoice.getBic());
        dto.setAccountHolder(invoice.getCompany().getName());

        dto.setSeller(mapSeller(invoice));
        dto.setBuyer(mapBuyer(invoice));
        dto.setTotals(mapTotals(invoice));
        dto.setItems(mapItems(invoice));

        return dto;
    }

    private XRechnungSellerDto mapSeller(InvoiceEntity invoice) {
        CompanyEntity company = invoice.getCompany();

        XRechnungSellerDto seller = new XRechnungSellerDto();
        seller.setName(normalize(company.getName()));
        seller.setStreet(normalize(company.getStreet()));
        seller.setZipCode(normalize(company.getZipCode()));
        seller.setCity(normalize(company.getCity()));
        seller.setCountry(normalizeCountryCode(company.getCountry()));
        seller.setVatId(normalize(company.getVatId()));
        seller.setTaxNumber(normalize(company.getTaxNumber()));
        seller.setContactPerson(
            company.getContactPerson()
        );
        seller.setEmail(normalize(company.getEmail()));
        seller.setPhone(normalize(company.getPhone()));
        seller.setElectronicAddress(normalize(company.getEmail()));

        seller.setElectronicAddressScheme(
            "EM"
        );

        return seller;
    }

    private XRechnungBuyerDto mapBuyer(InvoiceEntity invoice) {
        XRechnungBuyerDto buyer = new XRechnungBuyerDto();

        buyer.setName(normalize(invoice.getCustomerName()));
        buyer.setStreet(normalize(invoice.getCustomerStreet()));
        buyer.setZipCode(normalize(invoice.getCustomerZipCode()));
        buyer.setCity(normalize(invoice.getCustomerCity()));
        buyer.setCountry(normalizeCountryCode(invoice.getCustomerCountry()));
        buyer.setLeitwegId(normalize(invoice.getCustomerLeitwegId()));

        String electronicAddress = normalize(invoice.getCustomerElectronicAddress());
        String electronicAddressScheme = normalize(
            invoice.getCustomerElectronicAddressScheme()
        );

        if (electronicAddress == null && invoice.getCustomer() != null) {
            electronicAddress = normalize(
                invoice.getCustomer().getElectronicAddress()
            );
        }

        if (electronicAddressScheme == null && invoice.getCustomer() != null) {
            electronicAddressScheme = normalize(
                invoice.getCustomer().getElectronicAddressScheme()
            );
        }

        if (electronicAddress == null && buyer.getLeitwegId() != null) {
            electronicAddress = buyer.getLeitwegId();
        }

        if (electronicAddressScheme == null && buyer.getLeitwegId() != null) {
            electronicAddressScheme = "0204";
        }

        buyer.setElectronicAddress(electronicAddress);
        buyer.setElectronicAddressScheme(electronicAddressScheme);

        return buyer;
    }

    private XRechnungTotalsDto mapTotals(InvoiceEntity invoice) {
        XRechnungTotalsDto totals = new XRechnungTotalsDto();

        totals.setSubtotalNet(zeroIfNull(invoice.getSubtotalNet()));
        totals.setTaxAmount(zeroIfNull(invoice.getTaxAmount()));
        totals.setTotalGross(zeroIfNull(invoice.getTotalGross()));

        return totals;
    }

    private List<XRechnungItemDto> mapItems(InvoiceEntity invoice) {
        List<XRechnungItemDto> items = new ArrayList<>();

        if (invoice.getItems() == null) {
            return items;
        }

        for (InvoiceItemEntity item : invoice.getItems()) {
            items.add(mapItem(item));
        }

        return items;
    }

    private XRechnungItemDto mapItem(
        InvoiceItemEntity item
    ) {
        XRechnungItemDto dto =
            new XRechnungItemDto();

        dto.setPositionNumber(
            item.getPositionNumber()
        );

        dto.setProductNumber(
            normalize(item.getProductNumber())
        );

        dto.setName(
            normalize(item.getProductName())
        );

        dto.setDescription(
            normalizeMultilineText(
                item.getDescription()
            )
        );

        dto.setQuantity(
            item.getQuantity()
        );

        dto.setUnit(
            defaultValue(
                item.getUnit(),
                "C62"
            )
        );

        dto.setUnitPriceNet(
            zeroIfNull(item.getUnitPriceNet())
        );

        dto.setVatRate(
            zeroIfNull(item.getVatRate())
        );

        dto.setTaxCategoryCode(
            defaultValue(
                item.getTaxCategoryCode(),
                "S"
            )
        );

        dto.setLineNet(
            zeroIfNull(item.getLineNet())
        );

        dto.setTaxAmount(
            zeroIfNull(item.getTaxAmount())
        );

        dto.setLineGross(
            zeroIfNull(item.getLineGross())
        );

        return dto;
    }
    private String normalizeMultilineText(
        String value
    ) {
        if (value == null) {
            return null;
        }

        String normalized =
            value
                .replace("\r\n", "\n")
                .replace('\r', '\n')
                .trim();

        return normalized.isEmpty()
            ? null
            : normalized;
    }
    private String firstLine(
        String text
    ) {
        if (text == null || text.isBlank()) {
            return null;
        }

        String normalized =
            text.replace("\r\n", "\n")
                .replace('\r', '\n');

        int pos = normalized.indexOf('\n');

        if (pos < 0) {
            return normalized.trim();
        }

        return normalized.substring(0, pos).trim();
    }
    private void validateInvoice(InvoiceEntity invoice) {
        requireText(invoice.getInvoiceNumber(), "Rechnungsnummer");
        requireNotNull(invoice.getInvoiceDate(), "Rechnungsdatum");
        requireNotNull(invoice.getDueDate(), "Fälligkeitsdatum");
        requireText(invoice.getBuyerReference(), "Buyer Reference");
        requireText(invoice.getInvoiceTypeCode(), "Rechnungstyp");
        requireText(invoice.getPaymentMeansCode(), "Zahlungsart");
        requireText(invoice.getCurrencyCode(), "Währungscode");
        requireText(invoice.getBusinessProcessId(), "Geschäftsprozess-ID");
        requireText(invoice.getSpecificationIdentifier(), "Spezifikationskennung");

        requireNotNull(invoice.getCompany(), "Verkäufer");
        validateSeller(invoice.getCompany());
        validateBuyer(invoice);
        validateTotals(invoice);

        if (invoice.getItems() == null || invoice.getItems().isEmpty()) {
            throw new IllegalStateException(
                "Die XRechnung muss mindestens eine Rechnungsposition enthalten."
            );
        }

        for (InvoiceItemEntity item : invoice.getItems()) {
            validateItem(item);
        }
    }

    private void validateSeller(CompanyEntity company) {
        requireText(company.getName(), "Verkäufername");
        requireText(company.getStreet(), "Verkäuferstraße");
        requireText(company.getZipCode(), "Verkäufer-Postleitzahl");
        requireText(company.getCity(), "Verkäuferort");
        requireText(company.getCountry(), "Verkäuferland");

        if (normalize(company.getVatId()) == null
            && normalize(company.getTaxNumber()) == null) {
            throw new IllegalStateException(
                "Für die XRechnung muss beim Verkäufer entweder "
                    + "die USt-IdNr. oder die Steuernummer hinterlegt sein."
            );
        }
    }

    private void validateBuyer(InvoiceEntity invoice) {
        requireText(invoice.getCustomerName(), "Kundenname");
        requireText(invoice.getCustomerStreet(), "Kundenstraße");
        requireText(invoice.getCustomerZipCode(), "Kunden-Postleitzahl");
        requireText(invoice.getCustomerCity(), "Kundenort");
        requireText(invoice.getCustomerCountry(), "Kundenland");

        String electronicAddress = normalize(invoice.getCustomerElectronicAddress());
        String electronicAddressScheme = normalize(
            invoice.getCustomerElectronicAddressScheme()
        );

        if (electronicAddress == null && invoice.getCustomer() != null) {
            electronicAddress = normalize(
                invoice.getCustomer().getElectronicAddress()
            );
        }

        if (electronicAddressScheme == null && invoice.getCustomer() != null) {
            electronicAddressScheme = normalize(
                invoice.getCustomer().getElectronicAddressScheme()
            );
        }

        if (electronicAddress == null
            && normalize(invoice.getCustomerLeitwegId()) == null) {
            throw new IllegalStateException(
                "Elektronische Adresse des Käufers fehlt für die XRechnung."
            );
        }

        if (electronicAddressScheme == null
            && normalize(invoice.getCustomerLeitwegId()) == null) {
            throw new IllegalStateException(
                "Adressschema des Käufers fehlt für die XRechnung."
            );
        }
    }

    private void validateTotals(InvoiceEntity invoice) {
        requireNotNull(invoice.getSubtotalNet(), "Nettosumme");
        requireNotNull(invoice.getTaxAmount(), "Steuerbetrag");
        requireNotNull(invoice.getTotalGross(), "Bruttosumme");

        BigDecimal expectedGross = invoice.getSubtotalNet()
            .add(invoice.getTaxAmount());

        if (expectedGross.compareTo(invoice.getTotalGross()) != 0) {
            throw new IllegalStateException(
                "Die Bruttosumme entspricht nicht Nettosumme plus Steuerbetrag."
            );
        }
    }

    private void validateItem(InvoiceItemEntity item) {
        requireNotNull(item.getPositionNumber(), "Positionsnummer");
        requireText(item.getDescription(), "Positionsbeschreibung");
        requireNotNull(item.getQuantity(), "Positionsmenge");
        requireText(item.getUnit(), "Mengeneinheit");
        requireNotNull(item.getUnitPriceNet(), "Netto-Einzelpreis");
        requireNotNull(item.getVatRate(), "Umsatzsteuersatz");
        requireText(item.getTaxCategoryCode(), "Steuerkategorie");
        requireNotNull(item.getLineNet(), "Positionsnettosumme");

        if (item.getQuantity().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalStateException(
                "Die Menge der Position "
                    + item.getPositionNumber()
                    + " muss größer als 0 sein."
            );
        }

        if (item.getUnitPriceNet().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalStateException(
                "Der Netto-Einzelpreis der Position "
                    + item.getPositionNumber()
                    + " darf nicht negativ sein."
            );
        }

        if (item.getVatRate().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalStateException(
                "Der Umsatzsteuersatz der Position "
                    + item.getPositionNumber()
                    + " darf nicht negativ sein."
            );
        }
    }

    private void validateInvoiceId(Long invoiceId) {
        if (invoiceId == null || invoiceId <= 0) {
            throw new IllegalArgumentException(
                "Die Rechnungs-ID muss größer als 0 sein."
            );
        }
    }

    private void requireText(String value, String fieldName) {
        if (normalize(value) == null) {
            throw new IllegalStateException(
                fieldName + " fehlt für die XRechnung."
            );
        }
    }

    private void requireNotNull(Object value, String fieldName) {
        if (value == null) {
            throw new IllegalStateException(
                fieldName + " fehlt für die XRechnung."
            );
        }
    }

    private String defaultValue(String value, String defaultValue) {
        String normalized = normalize(value);
        return normalized == null ? defaultValue : normalized;
    }

    private String normalize(String value) {
        if (value == null) {
            return null;
        }

        String normalized = value.trim();
        return normalized.isEmpty() ? null : normalized;
    }

    private String normalizeCountryCode(String value) {
        String normalized = normalize(value);

        if (normalized == null) {
            return null;
        }

        if ("Deutschland".equalsIgnoreCase(normalized)
            || "Germany".equalsIgnoreCase(normalized)) {
            return "DE";
        }

        return normalized.toUpperCase(Locale.ROOT);
    }

    private BigDecimal zeroIfNull(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }
}
