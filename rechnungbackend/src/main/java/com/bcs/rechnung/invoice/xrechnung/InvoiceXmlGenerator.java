package com.bcs.rechnung.invoice.xrechnung;

import com.bcs.rechnung.invoice.xrechnung.dto.XRechnungBuyerDto;
import com.bcs.rechnung.invoice.xrechnung.dto.XRechnungDto;
import com.bcs.rechnung.invoice.xrechnung.dto.XRechnungItemDto;
import com.bcs.rechnung.invoice.xrechnung.dto.XRechnungSellerDto;
import com.bcs.rechnung.invoice.xrechnung.dto.XRechnungTotalsDto;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

@Component
public class InvoiceXmlGenerator {

    private static final String UBL_INVOICE_NAMESPACE =
        "urn:oasis:names:specification:ubl:schema:xsd:Invoice-2";

    private static final String CAC_NAMESPACE =
        "urn:oasis:names:specification:ubl:schema:xsd:"
            + "CommonAggregateComponents-2";

    private static final String CBC_NAMESPACE =
        "urn:oasis:names:specification:ubl:schema:xsd:"
            + "CommonBasicComponents-2";

    private static final String DEFAULT_CURRENCY =
        "EUR";

    private static final String DEFAULT_INVOICE_TYPE_CODE =
        "380";

    private static final String DEFAULT_PAYMENT_MEANS_CODE =
        "58";

    private static final String DEFAULT_TAX_CATEGORY =
        "S";

    private static final String DEFAULT_UNIT_CODE =
        "C62";

    private static final String DEFAULT_ELECTRONIC_ADDRESS_SCHEME =
        "EM";

    private static final String DEFAULT_SPECIFICATION_IDENTIFIER =
        "urn:cen.eu:en16931:2017#compliant#"
            + "urn:xeinkauf.de:kosit:xrechnung_3.0";

    private static final String DEFAULT_BUSINESS_PROCESS_ID =
        "urn:fdc:peppol.eu:2017:poacc:billing:01:1.0";

    private static final int MONEY_SCALE =
        2;

    private static final int QUANTITY_SCALE =
        4;

    private static final int PRICE_SCALE =
        4;

    private static final int RATE_SCALE =
        2;

    private static final RoundingMode MONEY_ROUNDING =
        RoundingMode.HALF_UP;

    public byte[] generate(
        XRechnungDto invoice
    ) {
        validateInvoice(
            invoice
        );

        try {
            DocumentBuilderFactory factory =
                createDocumentBuilderFactory();

            DocumentBuilder documentBuilder =
                factory.newDocumentBuilder();

            Document document =
                documentBuilder.newDocument();

            Element root =
                document.createElementNS(
                    UBL_INVOICE_NAMESPACE,
                    "Invoice"
                );

            root.setAttributeNS(
                XMLConstants.XMLNS_ATTRIBUTE_NS_URI,
                "xmlns:cac",
                CAC_NAMESPACE
            );

            root.setAttributeNS(
                XMLConstants.XMLNS_ATTRIBUTE_NS_URI,
                "xmlns:cbc",
                CBC_NAMESPACE
            );

            document.appendChild(
                root
            );

            appendHeader(
                document,
                root,
                invoice
            );

            appendInvoicePeriod(
                document,
                root,
                invoice
            );

            appendOrderReference(
                document,
                root,
                invoice
            );

            appendSeller(
                document,
                root,
                invoice.getSeller()
            );

            appendBuyer(
                document,
                root,
                invoice.getBuyer()
            );

            appendPaymentMeans(
                document,
                root,
                invoice
            );

            appendTaxTotal(
                document,
                root,
                invoice
            );

            appendLegalMonetaryTotal(
                document,
                root,
                invoice
            );

            appendInvoiceLines(
                document,
                root,
                invoice
            );

            return transformToBytes(
                document
            );

        } catch (Exception exception) {
            throw new IllegalStateException(
                "Die XRechnung "
                    + safe(
                    invoice.getInvoiceNumber()
                )
                    + " konnte nicht erzeugt werden.",
                exception
            );
        }
    }

    private DocumentBuilderFactory createDocumentBuilderFactory()
        throws Exception {

        DocumentBuilderFactory factory =
            DocumentBuilderFactory.newInstance();

        factory.setNamespaceAware(
            true
        );

        factory.setFeature(
            "http://apache.org/xml/features/"
                + "disallow-doctype-decl",
            true
        );

        factory.setFeature(
            "http://xml.org/sax/features/"
                + "external-general-entities",
            false
        );

        factory.setFeature(
            "http://xml.org/sax/features/"
                + "external-parameter-entities",
            false
        );

        factory.setFeature(
            "http://apache.org/xml/features/"
                + "nonvalidating/load-external-dtd",
            false
        );

        factory.setXIncludeAware(
            false
        );

        factory.setExpandEntityReferences(
            false
        );

        return factory;
    }

    private void appendHeader(
        Document document,
        Element root,
        XRechnungDto invoice
    ) {
        appendTextElement(
            document,
            root,
            CBC_NAMESPACE,
            "cbc:CustomizationID",
            defaultValue(
                invoice.getSpecificationIdentifier(),
                DEFAULT_SPECIFICATION_IDENTIFIER
            )
        );

        appendTextElement(
            document,
            root,
            CBC_NAMESPACE,
            "cbc:ProfileID",
            defaultValue(
                invoice.getBusinessProcessId(),
                DEFAULT_BUSINESS_PROCESS_ID
            )
        );

        appendTextElement(
            document,
            root,
            CBC_NAMESPACE,
            "cbc:ID",
            invoice.getInvoiceNumber()
        );

        appendDateElement(
            document,
            root,
            "cbc:IssueDate",
            invoice.getInvoiceDate()
        );

        appendDateElement(
            document,
            root,
            "cbc:DueDate",
            invoice.getDueDate()
        );

        appendTextElement(
            document,
            root,
            CBC_NAMESPACE,
            "cbc:InvoiceTypeCode",
            defaultValue(
                invoice.getInvoiceTypeCode(),
                DEFAULT_INVOICE_TYPE_CODE
            )
        );

        appendOptionalTextElement(
            document,
            root,
            CBC_NAMESPACE,
            "cbc:Note",
            invoice.getNotes()
        );

        appendTextElement(
            document,
            root,
            CBC_NAMESPACE,
            "cbc:DocumentCurrencyCode",
            currency(
                invoice
            )
        );

        if (invoice.getTaxPointDate() != null) {
            appendDateElement(
                document,
                root,
                "cbc:TaxPointDate",
                invoice.getTaxPointDate()
            );
        }

        appendTextElement(
            document,
            root,
            CBC_NAMESPACE,
            "cbc:BuyerReference",
            invoice.getBuyerReference()
        );
    }

    private void appendInvoicePeriod(
        Document document,
        Element root,
        XRechnungDto invoice
    ) {
        if (invoice.getServiceDate() == null) {
            return;
        }

        Element invoicePeriod =
            appendElement(
                document,
                root,
                CAC_NAMESPACE,
                "cac:InvoicePeriod"
            );

        appendDateElement(
            document,
            invoicePeriod,
            "cbc:StartDate",
            invoice.getServiceDate()
        );

        appendDateElement(
            document,
            invoicePeriod,
            "cbc:EndDate",
            invoice.getServiceDate()
        );
    }

    private void appendOrderReference(
        Document document,
        Element root,
        XRechnungDto invoice
    ) {
        if (!notBlank(
            invoice.getOrderReference()
        )) {
            return;
        }

        Element orderReference =
            appendElement(
                document,
                root,
                CAC_NAMESPACE,
                "cac:OrderReference"
            );

        appendTextElement(
            document,
            orderReference,
            CBC_NAMESPACE,
            "cbc:ID",
            invoice.getOrderReference()
        );
    }

    private void appendSeller(
        Document document,
        Element root,
        XRechnungSellerDto seller
    ) {
        requireNotNull(
            seller,
            "Verkäufer"
        );

        Element supplierParty =
            appendElement(
                document,
                root,
                CAC_NAMESPACE,
                "cac:AccountingSupplierParty"
            );

        Element party =
            appendElement(
                document,
                supplierParty,
                CAC_NAMESPACE,
                "cac:Party"
            );

        appendEndpointId(
            document,
            party,
            seller.getElectronicAddress(),
            seller.getElectronicAddressScheme()
        );

        Element partyName =
            appendElement(
                document,
                party,
                CAC_NAMESPACE,
                "cac:PartyName"
            );

        appendTextElement(
            document,
            partyName,
            CBC_NAMESPACE,
            "cbc:Name",
            seller.getName()
        );

        appendPostalAddress(
            document,
            party,
            seller.getStreet(),
            seller.getZipCode(),
            seller.getCity(),
            seller.getCountry()
        );

        appendSellerTaxScheme(
            document,
            party,
            seller
        );

        Element legalEntity =
            appendElement(
                document,
                party,
                CAC_NAMESPACE,
                "cac:PartyLegalEntity"
            );

        appendTextElement(
            document,
            legalEntity,
            CBC_NAMESPACE,
            "cbc:RegistrationName",
            seller.getName()
        );

        appendSellerContact(
            document,
            party,
            seller
        );
    }

    private void appendSellerTaxScheme(
        Document document,
        Element party,
        XRechnungSellerDto seller
    ) {
        String vatId =
            normalize(
                seller.getVatId()
            );

        if (vatId != null) {
            Element taxScheme =
                appendElement(
                    document,
                    party,
                    CAC_NAMESPACE,
                    "cac:PartyTaxScheme"
                );

            appendTextElement(
                document,
                taxScheme,
                CBC_NAMESPACE,
                "cbc:CompanyID",
                vatId
            );

            Element vatScheme =
                appendElement(
                    document,
                    taxScheme,
                    CAC_NAMESPACE,
                    "cac:TaxScheme"
                );

            appendTextElement(
                document,
                vatScheme,
                CBC_NAMESPACE,
                "cbc:ID",
                "VAT"
            );
        }

        String taxNumber =
            normalize(
                seller.getTaxNumber()
            );

        if (taxNumber != null) {
            Element taxScheme =
                appendElement(
                    document,
                    party,
                    CAC_NAMESPACE,
                    "cac:PartyTaxScheme"
                );

            appendTextElement(
                document,
                taxScheme,
                CBC_NAMESPACE,
                "cbc:CompanyID",
                taxNumber
            );

            Element fiscalScheme =
                appendElement(
                    document,
                    taxScheme,
                    CAC_NAMESPACE,
                    "cac:TaxScheme"
                );

            appendTextElement(
                document,
                fiscalScheme,
                CBC_NAMESPACE,
                "cbc:ID",
                "FC"
            );
        }
    }

    private void appendSellerContact(
        Document document,
        Element party,
        XRechnungSellerDto seller
    ) {
        if (!notBlank(
            seller.getContactPerson()
        )
            && !notBlank(
            seller.getPhone()
        )
            && !notBlank(
            seller.getEmail()
        )) {

            return;
        }

        Element contact =
            appendElement(
                document,
                party,
                CAC_NAMESPACE,
                "cac:Contact"
            );

        appendOptionalTextElement(
            document,
            contact,
            CBC_NAMESPACE,
            "cbc:Name",
            seller.getContactPerson()
        );

        appendOptionalTextElement(
            document,
            contact,
            CBC_NAMESPACE,
            "cbc:Telephone",
            seller.getPhone()
        );

        appendOptionalTextElement(
            document,
            contact,
            CBC_NAMESPACE,
            "cbc:ElectronicMail",
            seller.getEmail()
        );
    }

    private void appendBuyer(
        Document document,
        Element root,
        XRechnungBuyerDto buyer
    ) {
        requireNotNull(
            buyer,
            "Käufer"
        );

        Element customerParty =
            appendElement(
                document,
                root,
                CAC_NAMESPACE,
                "cac:AccountingCustomerParty"
            );

        Element party =
            appendElement(
                document,
                customerParty,
                CAC_NAMESPACE,
                "cac:Party"
            );

        appendEndpointId(
            document,
            party,
            buyer.getElectronicAddress(),
            buyer.getElectronicAddressScheme()
        );

        Element partyName =
            appendElement(
                document,
                party,
                CAC_NAMESPACE,
                "cac:PartyName"
            );

        appendTextElement(
            document,
            partyName,
            CBC_NAMESPACE,
            "cbc:Name",
            buyer.getName()
        );

        appendPostalAddress(
            document,
            party,
            buyer.getStreet(),
            buyer.getZipCode(),
            buyer.getCity(),
            buyer.getCountry()
        );

        Element legalEntity =
            appendElement(
                document,
                party,
                CAC_NAMESPACE,
                "cac:PartyLegalEntity"
            );

        appendTextElement(
            document,
            legalEntity,
            CBC_NAMESPACE,
            "cbc:RegistrationName",
            buyer.getName()
        );
    }

    private void appendEndpointId(
        Document document,
        Element party,
        String address,
        String scheme
    ) {
        if (!notBlank(
            address
        )) {
            return;
        }

        Element endpointId =
            appendTextElement(
                document,
                party,
                CBC_NAMESPACE,
                "cbc:EndpointID",
                address
            );

        endpointId.setAttribute(
            "schemeID",
            defaultValue(
                scheme,
                DEFAULT_ELECTRONIC_ADDRESS_SCHEME
            )
        );
    }

    private void appendPostalAddress(
        Document document,
        Element party,
        String street,
        String zipCode,
        String city,
        String country
    ) {
        Element postalAddress =
            appendElement(
                document,
                party,
                CAC_NAMESPACE,
                "cac:PostalAddress"
            );

        appendTextElement(
            document,
            postalAddress,
            CBC_NAMESPACE,
            "cbc:StreetName",
            street
        );

        appendTextElement(
            document,
            postalAddress,
            CBC_NAMESPACE,
            "cbc:CityName",
            city
        );

        appendTextElement(
            document,
            postalAddress,
            CBC_NAMESPACE,
            "cbc:PostalZone",
            zipCode
        );

        Element countryElement =
            appendElement(
                document,
                postalAddress,
                CAC_NAMESPACE,
                "cac:Country"
            );

        appendTextElement(
            document,
            countryElement,
            CBC_NAMESPACE,
            "cbc:IdentificationCode",
            country
        );
    }

    private void appendPaymentMeans(
        Document document,
        Element root,
        XRechnungDto invoice
    ) {
        Element paymentMeans =
            appendElement(
                document,
                root,
                CAC_NAMESPACE,
                "cac:PaymentMeans"
            );

        appendTextElement(
            document,
            paymentMeans,
            CBC_NAMESPACE,
            "cbc:PaymentMeansCode",
            defaultValue(
                invoice.getPaymentMeansCode(),
                DEFAULT_PAYMENT_MEANS_CODE
            )
        );

        appendOptionalTextElement(
            document,
            paymentMeans,
            CBC_NAMESPACE,
            "cbc:PaymentID",
            invoice.getPaymentReference()
        );

        Element payeeAccount =
            appendElement(
                document,
                paymentMeans,
                CAC_NAMESPACE,
                "cac:PayeeFinancialAccount"
            );

        appendTextElement(
            document,
            payeeAccount,
            CBC_NAMESPACE,
            "cbc:ID",
            normalizeIban(
                invoice.getIban()
            )
        );

        appendOptionalTextElement(
            document,
            payeeAccount,
            CBC_NAMESPACE,
            "cbc:Name",
            invoice.getAccountHolder()
        );

        if (notBlank(
            invoice.getBic()
        )) {
            Element financialInstitution =
                appendElement(
                    document,
                    payeeAccount,
                    CAC_NAMESPACE,
                    "cac:FinancialInstitutionBranch"
                );

            appendTextElement(
                document,
                financialInstitution,
                CBC_NAMESPACE,
                "cbc:ID",
                invoice.getBic()
            );
        }
    }

    private void appendTaxTotal(
        Document document,
        Element root,
        XRechnungDto invoice
    ) {
        List<TaxBreakdown> taxBreakdowns =
            calculateTaxBreakdowns(
                invoice
            );

        BigDecimal totalTaxAmount =
            taxBreakdowns.stream()
                .map(
                    TaxBreakdown::taxAmount
                )
                .reduce(
                    BigDecimal.ZERO,
                    BigDecimal::add
                )
                .setScale(
                    MONEY_SCALE,
                    MONEY_ROUNDING
                );

        validateTotalTaxAmount(
            invoice,
            totalTaxAmount
        );

        Element taxTotal =
            appendElement(
                document,
                root,
                CAC_NAMESPACE,
                "cac:TaxTotal"
            );

        appendAmountElement(
            document,
            taxTotal,
            "cbc:TaxAmount",
            totalTaxAmount,
            currency(
                invoice
            )
        );

        for (
            TaxBreakdown breakdown :
            taxBreakdowns
        ) {
            appendTaxSubtotal(
                document,
                taxTotal,
                invoice,
                breakdown
            );
        }
    }

    private void appendTaxSubtotal(
        Document document,
        Element taxTotal,
        XRechnungDto invoice,
        TaxBreakdown breakdown
    ) {
        Element taxSubtotal =
            appendElement(
                document,
                taxTotal,
                CAC_NAMESPACE,
                "cac:TaxSubtotal"
            );

        appendAmountElement(
            document,
            taxSubtotal,
            "cbc:TaxableAmount",
            breakdown.taxableAmount(),
            currency(
                invoice
            )
        );

        appendAmountElement(
            document,
            taxSubtotal,
            "cbc:TaxAmount",
            breakdown.taxAmount(),
            currency(
                invoice
            )
        );

        Element taxCategory =
            appendElement(
                document,
                taxSubtotal,
                CAC_NAMESPACE,
                "cac:TaxCategory"
            );

        appendTextElement(
            document,
            taxCategory,
            CBC_NAMESPACE,
            "cbc:ID",
            breakdown.key()
                .categoryCode()
        );

        if (requiresVatRate(
            breakdown.key()
                .categoryCode()
        )) {
            appendDecimalElement(
                document,
                taxCategory,
                "cbc:Percent",
                breakdown.key()
                    .vatRate(),
                RATE_SCALE
            );
        }

        Element taxScheme =
            appendElement(
                document,
                taxCategory,
                CAC_NAMESPACE,
                "cac:TaxScheme"
            );

        appendTextElement(
            document,
            taxScheme,
            CBC_NAMESPACE,
            "cbc:ID",
            "VAT"
        );
    }

    private List<TaxBreakdown> calculateTaxBreakdowns(
        XRechnungDto invoice
    ) {
        Map<TaxKey, BigDecimal> taxableAmounts =
            new LinkedHashMap<>();

        for (
            XRechnungItemDto item :
            invoice.getItems()
        ) {
            String categoryCode =
                defaultValue(
                    item.getTaxCategoryCode(),
                    DEFAULT_TAX_CATEGORY
                );

            BigDecimal vatRate =
                normalizeRate(
                    item.getVatRate()
                );

            TaxKey key =
                new TaxKey(
                    categoryCode,
                    vatRate
                );

            BigDecimal lineNet =
                money(
                    item.getLineNet()
                );

            taxableAmounts.merge(
                key,
                lineNet,
                BigDecimal::add
            );
        }

        List<TaxBreakdown> result =
            new ArrayList<>();

        for (
            Map.Entry<TaxKey, BigDecimal> entry :
            taxableAmounts.entrySet()
        ) {
            TaxKey key =
                entry.getKey();

            BigDecimal taxableAmount =
                money(
                    entry.getValue()
                );

            BigDecimal taxAmount =
                calculateTaxAmount(
                    taxableAmount,
                    key.vatRate(),
                    key.categoryCode()
                );

            result.add(
                new TaxBreakdown(
                    key,
                    taxableAmount,
                    taxAmount
                )
            );
        }

        result.sort(
            Comparator
                .comparing(
                    (TaxBreakdown value) ->
                        value.key()
                            .categoryCode()
                )
                .thenComparing(
                    value ->
                        value.key()
                            .vatRate()
                )
        );

        return result;
    }

    private BigDecimal calculateTaxAmount(
        BigDecimal taxableAmount,
        BigDecimal vatRate,
        String categoryCode
    ) {
        if (!requiresVatRate(
            categoryCode
        )) {
            return BigDecimal.ZERO
                .setScale(
                    MONEY_SCALE,
                    MONEY_ROUNDING
                );
        }

        return taxableAmount
            .multiply(
                vatRate
            )
            .divide(
                BigDecimal.valueOf(
                    100
                ),
                MONEY_SCALE,
                MONEY_ROUNDING
            );
    }

    private boolean requiresVatRate(
        String categoryCode
    ) {
        String normalized =
            defaultValue(
                categoryCode,
                DEFAULT_TAX_CATEGORY
            ).toUpperCase(
                Locale.ROOT
            );

        return "S".equals(
            normalized
        )
            || "Z".equals(
            normalized
        );
    }

    private void validateTotalTaxAmount(
        XRechnungDto invoice,
        BigDecimal calculatedTaxAmount
    ) {
        XRechnungTotalsDto totals =
            invoice.getTotals();

        requireNotNull(
            totals,
            "Rechnungssummen"
        );

        BigDecimal mappedTaxAmount =
            money(
                totals.getTaxAmount()
            );

        if (mappedTaxAmount.compareTo(
            calculatedTaxAmount
        ) != 0) {
            throw new IllegalStateException(
                "Der Steuerbetrag der Rechnung "
                    + invoice.getInvoiceNumber()
                    + " stimmt nicht mit der Summe "
                    + "der Steueraufschlüsselungen überein. "
                    + "Gespeichert: "
                    + amount(
                    mappedTaxAmount
                )
                    + ", berechnet: "
                    + amount(
                    calculatedTaxAmount
                )
                    + "."
            );
        }
    }

    private void appendLegalMonetaryTotal(
        Document document,
        Element root,
        XRechnungDto invoice
    ) {
        XRechnungTotalsDto totals =
            invoice.getTotals();

        requireNotNull(
            totals,
            "Rechnungssummen"
        );

        BigDecimal subtotalNet =
            money(
                totals.getSubtotalNet()
            );

        BigDecimal taxAmount =
            money(
                totals.getTaxAmount()
            );

        BigDecimal totalGross =
            money(
                totals.getTotalGross()
            );

        validateMonetaryTotals(
            invoice,
            subtotalNet,
            taxAmount,
            totalGross
        );

        Element monetaryTotal =
            appendElement(
                document,
                root,
                CAC_NAMESPACE,
                "cac:LegalMonetaryTotal"
            );

        appendAmountElement(
            document,
            monetaryTotal,
            "cbc:LineExtensionAmount",
            subtotalNet,
            currency(
                invoice
            )
        );

        appendAmountElement(
            document,
            monetaryTotal,
            "cbc:TaxExclusiveAmount",
            subtotalNet,
            currency(
                invoice
            )
        );

        appendAmountElement(
            document,
            monetaryTotal,
            "cbc:TaxInclusiveAmount",
            totalGross,
            currency(
                invoice
            )
        );

        appendAmountElement(
            document,
            monetaryTotal,
            "cbc:PayableAmount",
            totalGross,
            currency(
                invoice
            )
        );
    }

    private void validateMonetaryTotals(
        XRechnungDto invoice,
        BigDecimal subtotalNet,
        BigDecimal taxAmount,
        BigDecimal totalGross
    ) {
        BigDecimal calculatedSubtotal =
            invoice.getItems()
                .stream()
                .map(
                    XRechnungItemDto::getLineNet
                )
                .filter(
                    Objects::nonNull
                )
                .map(
                    this::money
                )
                .reduce(
                    BigDecimal.ZERO,
                    BigDecimal::add
                )
                .setScale(
                    MONEY_SCALE,
                    MONEY_ROUNDING
                );

        if (subtotalNet.compareTo(
            calculatedSubtotal
        ) != 0) {
            throw new IllegalStateException(
                "Die Nettosumme der Rechnung "
                    + invoice.getInvoiceNumber()
                    + " stimmt nicht mit der Summe "
                    + "der Positionsnettos überein. "
                    + "Gespeichert: "
                    + amount(
                    subtotalNet
                )
                    + ", berechnet: "
                    + amount(
                    calculatedSubtotal
                )
                    + "."
            );
        }

        BigDecimal calculatedGross =
            subtotalNet
                .add(
                    taxAmount
                )
                .setScale(
                    MONEY_SCALE,
                    MONEY_ROUNDING
                );

        if (totalGross.compareTo(
            calculatedGross
        ) != 0) {
            throw new IllegalStateException(
                "Die Bruttosumme der Rechnung "
                    + invoice.getInvoiceNumber()
                    + " stimmt nicht mit Nettosumme "
                    + "plus Steuerbetrag überein. "
                    + "Gespeichert: "
                    + amount(
                    totalGross
                )
                    + ", berechnet: "
                    + amount(
                    calculatedGross
                )
                    + "."
            );
        }
    }

    private void appendInvoiceLines(
        Document document,
        Element root,
        XRechnungDto invoice
    ) {
        for (
            XRechnungItemDto item :
            invoice.getItems()
        ) {
            appendInvoiceLine(
                document,
                root,
                invoice,
                item
            );
        }
    }

    private void appendInvoiceLine(
        Document document,
        Element root,
        XRechnungDto invoice,
        XRechnungItemDto item
    ) {
        Element invoiceLine =
            appendElement(
                document,
                root,
                CAC_NAMESPACE,
                "cac:InvoiceLine"
            );

        appendTextElement(
            document,
            invoiceLine,
            CBC_NAMESPACE,
            "cbc:ID",
            String.valueOf(
                item.getPositionNumber()
            )
        );

        Element quantity =
            appendDecimalElement(
                document,
                invoiceLine,
                "cbc:InvoicedQuantity",
                item.getQuantity(),
                QUANTITY_SCALE
            );

        String unitCode =
            normalizeUnitCode(
                item.getUnit()
            );

        quantity.setAttribute(
            "unitCode",
            unitCode
        );

        appendAmountElement(
            document,
            invoiceLine,
            "cbc:LineExtensionAmount",
            money(
                item.getLineNet()
            ),
            currency(
                invoice
            )
        );

        appendLineAllowance(
            document,
            invoiceLine,
            invoice,
            item
        );

        appendItem(
            document,
            invoiceLine,
            item
        );

        appendPrice(
            document,
            invoiceLine,
            invoice,
            item
        );
    }

    private void appendLineAllowance(
        Document document,
        Element invoiceLine,
        XRechnungDto invoice,
        XRechnungItemDto item
    ) {
        BigDecimal discountPercent =
            zeroIfNull(
                item.getDiscountPercent()
            );

        if (discountPercent.compareTo(
            BigDecimal.ZERO
        ) <= 0) {
            return;
        }

        BigDecimal quantity =
            zeroIfNull(
                item.getQuantity()
            );

        BigDecimal unitPriceNet =
            zeroIfNull(
                item.getUnitPriceNet()
            );

        /*
         * Basisbetrag vor Positionsrabatt.
         *
         * 3 × 95,00 EUR = 285,00 EUR
         */
        BigDecimal baseAmount =
            quantity
                .multiply(
                    unitPriceNet
                )
                .setScale(
                    MONEY_SCALE,
                    MONEY_ROUNDING
                );

        /*
         * Rabattbetrag unmittelbar aus Basisbetrag
         * und Prozentsatz berechnen.
         *
         * 285,00 × 10 / 100 = 28,50 EUR
         */
        BigDecimal allowanceAmount =
            baseAmount
                .multiply(
                    discountPercent
                )
                .divide(
                    BigDecimal.valueOf(
                        100
                    ),
                    MONEY_SCALE,
                    MONEY_ROUNDING
                );

        /*
         * Kontrollieren, ob der ausgewiesene Rabatt
         * zum gespeicherten Positionsnetto passt.
         */
        BigDecimal expectedLineNet =
            baseAmount
                .subtract(
                    allowanceAmount
                )
                .setScale(
                    MONEY_SCALE,
                    MONEY_ROUNDING
                );

        BigDecimal storedLineNet =
            money(
                item.getLineNet()
            );

        if (expectedLineNet.compareTo(
            storedLineNet
        ) != 0) {
            throw new IllegalStateException(
                "Der Positionsrabatt der Position "
                    + item.getPositionNumber()
                    + " ist nicht konsistent. "
                    + "Erwartetes Positionsnetto: "
                    + amount(
                    expectedLineNet
                )
                    + ", gespeichertes Positionsnetto: "
                    + amount(
                    storedLineNet
                )
                    + "."
            );
        }

        Element allowance =
            appendElement(
                document,
                invoiceLine,
                CAC_NAMESPACE,
                "cac:AllowanceCharge"
            );

        appendTextElement(
            document,
            allowance,
            CBC_NAMESPACE,
            "cbc:ChargeIndicator",
            "false"
        );

        appendTextElement(
            document,
            allowance,
            CBC_NAMESPACE,
            "cbc:AllowanceChargeReason",
            "Positionsrabatt"
        );

        /*
         * Prozentwert ausgeben:
         *
         * 10 bedeutet 10 Prozent.
         * Nicht 0,1 ausgeben.
         */
        appendDecimalElement(
            document,
            allowance,
            "cbc:MultiplierFactorNumeric",
            discountPercent,
            RATE_SCALE
        );

        appendAmountElement(
            document,
            allowance,
            "cbc:Amount",
            allowanceAmount,
            currency(
                invoice
            )
        );

        appendAmountElement(
            document,
            allowance,
            "cbc:BaseAmount",
            baseAmount,
            currency(
                invoice
            )
        );
    }

    private void appendItem(
        Document document,
        Element invoiceLine,
        XRechnungItemDto item
    ) {
        Element itemElement =
            appendElement(
                document,
                invoiceLine,
                CAC_NAMESPACE,
                "cac:Item"
            );

        appendOptionalTextElement(
            document,
            itemElement,
            CBC_NAMESPACE,
            "cbc:Description",
            item.getDescription()
        );

        String itemName =
            firstNonBlank(
                item.getProductName(),
                item.getDescription(),
                item.getProductNumber()
            );

        appendTextElement(
            document,
            itemElement,
            CBC_NAMESPACE,
            "cbc:Name",
            itemName
        );

        if (notBlank(
            item.getProductNumber()
        )) {
            Element sellerItemIdentification =
                appendElement(
                    document,
                    itemElement,
                    CAC_NAMESPACE,
                    "cac:SellersItemIdentification"
                );

            appendTextElement(
                document,
                sellerItemIdentification,
                CBC_NAMESPACE,
                "cbc:ID",
                item.getProductNumber()
            );
        }

        Element taxCategory =
            appendElement(
                document,
                itemElement,
                CAC_NAMESPACE,
                "cac:ClassifiedTaxCategory"
            );

        String categoryCode =
            defaultValue(
                item.getTaxCategoryCode(),
                DEFAULT_TAX_CATEGORY
            );

        appendTextElement(
            document,
            taxCategory,
            CBC_NAMESPACE,
            "cbc:ID",
            categoryCode
        );

        if (requiresVatRate(
            categoryCode
        )) {
            appendDecimalElement(
                document,
                taxCategory,
                "cbc:Percent",
                normalizeRate(
                    item.getVatRate()
                ),
                RATE_SCALE
            );
        }

        Element taxScheme =
            appendElement(
                document,
                taxCategory,
                CAC_NAMESPACE,
                "cac:TaxScheme"
            );

        appendTextElement(
            document,
            taxScheme,
            CBC_NAMESPACE,
            "cbc:ID",
            "VAT"
        );
    }

    private void appendPrice(
        Document document,
        Element invoiceLine,
        XRechnungDto invoice,
        XRechnungItemDto item
    ) {
        Element price =
            appendElement(
                document,
                invoiceLine,
                CAC_NAMESPACE,
                "cac:Price"
            );

        appendAmountElement(
            document,
            price,
            "cbc:PriceAmount",
            price(
                item.getUnitPriceNet()
            ),
            currency(
                invoice
            )
        );

        Element baseQuantity =
            appendDecimalElement(
                document,
                price,
                "cbc:BaseQuantity",
                BigDecimal.ONE,
                0
            );

//        baseQuantity.setAttribute(
//            "unitCode",
//            defaultValue(
//                item.getUnit(),
//                DEFAULT_UNIT_CODE
//            )
//        );
        baseQuantity.setAttribute(
            "unitCode",
            normalizeUnitCode(
                item.getUnit()
            )
        );
    }

    private byte[] transformToBytes(
        Document document
    ) throws Exception {

        TransformerFactory transformerFactory =
            TransformerFactory.newInstance();

        transformerFactory.setFeature(
            XMLConstants.FEATURE_SECURE_PROCESSING,
            true
        );

        Transformer transformer =
            transformerFactory.newTransformer();

        transformer.setOutputProperty(
            OutputKeys.ENCODING,
            StandardCharsets.UTF_8.name()
        );

        transformer.setOutputProperty(
            OutputKeys.INDENT,
            "yes"
        );

        transformer.setOutputProperty(
            OutputKeys.OMIT_XML_DECLARATION,
            "no"
        );

        transformer.setOutputProperty(
            "{http://xml.apache.org/xslt}indent-amount",
            "2"
        );

        try (
            ByteArrayOutputStream output =
                new ByteArrayOutputStream()
        ) {
            transformer.transform(
                new DOMSource(
                    document
                ),
                new StreamResult(
                    output
                )
            );

            return output.toByteArray();
        }
    }

    private Element appendElement(
        Document document,
        Element parent,
        String namespace,
        String qualifiedName
    ) {
        Element element =
            document.createElementNS(
                namespace,
                qualifiedName
            );

        parent.appendChild(
            element
        );

        return element;
    }

    private Element appendTextElement(
        Document document,
        Element parent,
        String namespace,
        String qualifiedName,
        String value
    ) {
        String normalized =
            normalize(
                value
            );

        if (normalized == null) {
            throw new IllegalStateException(
                "Der Wert für "
                    + qualifiedName
                    + " fehlt."
            );
        }

        Element element =
            appendElement(
                document,
                parent,
                namespace,
                qualifiedName
            );

        element.setTextContent(
            normalized
        );

        return element;
    }

    private void appendOptionalTextElement(
        Document document,
        Element parent,
        String namespace,
        String qualifiedName,
        String value
    ) {
        String normalized =
            normalize(
                value
            );

        if (normalized == null) {
            return;
        }

        Element element =
            appendElement(
                document,
                parent,
                namespace,
                qualifiedName
            );

        element.setTextContent(
            normalized
        );
    }

    private Element appendDateElement(
        Document document,
        Element parent,
        String qualifiedName,
        LocalDate value
    ) {
        requireNotNull(
            value,
            qualifiedName
        );

        return appendTextElement(
            document,
            parent,
            CBC_NAMESPACE,
            qualifiedName,
            value.toString()
        );
    }

    private Element appendAmountElement(
        Document document,
        Element parent,
        String qualifiedName,
        BigDecimal value,
        String currency
    ) {
        Element element =
            appendTextElement(
                document,
                parent,
                CBC_NAMESPACE,
                qualifiedName,
                amount(
                    value
                )
            );

        element.setAttribute(
            "currencyID",
            currency
        );

        return element;
    }

    private Element appendDecimalElement(
        Document document,
        Element parent,
        String qualifiedName,
        BigDecimal value,
        int scale
    ) {
        requireNotNull(
            value,
            qualifiedName
        );

        BigDecimal normalized =
            value.setScale(
                scale,
                MONEY_ROUNDING
            );

        return appendTextElement(
            document,
            parent,
            CBC_NAMESPACE,
            qualifiedName,
            decimal(
                normalized
            )
        );
    }

    private BigDecimal money(
        BigDecimal value
    ) {
        return zeroIfNull(
            value
        ).setScale(
            MONEY_SCALE,
            MONEY_ROUNDING
        );
    }

    private BigDecimal price(
        BigDecimal value
    ) {
        return zeroIfNull(
            value
        ).setScale(
            PRICE_SCALE,
            MONEY_ROUNDING
        );
    }

    private BigDecimal normalizeRate(
        BigDecimal value
    ) {
        return zeroIfNull(
            value
        ).setScale(
            RATE_SCALE,
            MONEY_ROUNDING
        );
    }

    private String amount(
        BigDecimal value
    ) {
        return money(
            value
        ).toPlainString();
    }

    private String decimal(
        BigDecimal value
    ) {
        return value
            .stripTrailingZeros()
            .toPlainString();
    }

    private String currency(
        XRechnungDto invoice
    ) {
        return defaultValue(
            invoice.getCurrencyCode(),
            DEFAULT_CURRENCY
        ).toUpperCase(
            Locale.ROOT
        );
    }

    private String normalizeIban(
        String value
    ) {
        String normalized =
            normalize(
                value
            );

        if (normalized == null) {
            throw new IllegalStateException(
                "Die IBAN fehlt für die XRechnung."
            );
        }

        return normalized.replaceAll(
            "\\s+",
            ""
        );
    }

    private String firstNonBlank(
        String... values
    ) {
        if (values == null) {
            return "Rechnungsposition";
        }

        for (String value : values) {
            String normalized =
                normalize(
                    value
                );

            if (normalized != null) {
                int lineBreak =
                    normalized.indexOf(
                        '\n'
                    );

                return lineBreak < 0
                    ? normalized
                    : normalized
                    .substring(
                        0,
                        lineBreak
                    )
                    .trim();
            }
        }

        return "Rechnungsposition";
    }

    private String defaultValue(
        String value,
        String defaultValue
    ) {
        String normalized =
            normalize(
                value
            );

        return normalized == null
            ? defaultValue
            : normalized;
    }

    private String normalize(
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

    private boolean notBlank(
        String value
    ) {
        return normalize(
            value
        ) != null;
    }

    private String safe(
        String value
    ) {
        String normalized =
            normalize(
                value
            );

        return normalized == null
            ? ""
            : normalized;
    }

    private BigDecimal zeroIfNull(
        BigDecimal value
    ) {
        return value == null
            ? BigDecimal.ZERO
            : value;
    }

    private void requireNotNull(
        Object value,
        String fieldName
    ) {
        if (value == null) {
            throw new IllegalStateException(
                fieldName
                    + " darf nicht null sein."
            );
        }
    }

    private void validateInvoice(
        XRechnungDto invoice
    ) {
        requireNotNull(
            invoice,
            "XRechnung"
        );

        requireText(
            invoice.getInvoiceNumber(),
            "Rechnungsnummer"
        );

        requireNotNull(
            invoice.getInvoiceDate(),
            "Rechnungsdatum"
        );

        requireNotNull(
            invoice.getDueDate(),
            "Fälligkeitsdatum"
        );

        requireText(
            invoice.getBuyerReference(),
            "Käuferreferenz"
        );

        requireNotNull(
            invoice.getSeller(),
            "Verkäufer"
        );

        requireNotNull(
            invoice.getBuyer(),
            "Käufer"
        );

        requireNotNull(
            invoice.getTotals(),
            "Rechnungssummen"
        );

        if (invoice.getItems() == null
            || invoice.getItems().isEmpty()) {

            throw new IllegalStateException(
                "Die XRechnung muss mindestens "
                    + "eine Position enthalten."
            );
        }

        for (
            XRechnungItemDto item :
            invoice.getItems()
        ) {
            validateItem(
                item
            );
        }
    }

    private void validateItem(
        XRechnungItemDto item
    ) {
        requireNotNull(
            item,
            "Rechnungsposition"
        );

        requireNotNull(
            item.getPositionNumber(),
            "Positionsnummer"
        );

        requireText(
            item.getDescription(),
            "Positionsbeschreibung"
        );

        requireNotNull(
            item.getQuantity(),
            "Positionsmenge"
        );

        requireNotNull(
            item.getUnitPriceNet(),
            "Netto-Einzelpreis"
        );

        requireNotNull(
            item.getVatRate(),
            "Umsatzsteuersatz"
        );

        requireNotNull(
            item.getLineNet(),
            "Positionsnettosumme"
        );

        if (item.getQuantity().compareTo(
            BigDecimal.ZERO
        ) <= 0) {
            throw new IllegalStateException(
                "Die Menge der Position "
                    + item.getPositionNumber()
                    + " muss größer als 0 sein."
            );
        }

        if (item.getUnitPriceNet().compareTo(
            BigDecimal.ZERO
        ) < 0) {
            throw new IllegalStateException(
                "Der Netto-Einzelpreis der Position "
                    + item.getPositionNumber()
                    + " darf nicht negativ sein."
            );
        }

        if (item.getVatRate().compareTo(
            BigDecimal.ZERO
        ) < 0) {
            throw new IllegalStateException(
                "Der Umsatzsteuersatz der Position "
                    + item.getPositionNumber()
                    + " darf nicht negativ sein."
            );
        }

        BigDecimal discountPercent =
            zeroIfNull(
                item.getDiscountPercent()
            );

        if (discountPercent.compareTo(
            BigDecimal.ZERO
        ) < 0
            || discountPercent.compareTo(
            BigDecimal.valueOf(
                100
            )
        ) > 0) {

            throw new IllegalStateException(
                "Der Rabatt der Position "
                    + item.getPositionNumber()
                    + " muss zwischen 0 und 100 Prozent liegen."
            );
        }
    }

    private void requireText(
        String value,
        String fieldName
    ) {
        if (!notBlank(
            value
        )) {
            throw new IllegalStateException(
                fieldName
                    + " fehlt für die XRechnung."
            );
        }
    }
    private String normalizeUnitCode(
        String unit
    ) {
        String normalized =
            normalize(
                unit
            );

        if (normalized == null) {
            return DEFAULT_UNIT_CODE;
        }

        return switch (
            normalized.toUpperCase(
                Locale.ROOT
            )
            ) {
            case "STD",
                 "STUNDE",
                 "STUNDEN",
                 "HOUR",
                 "HOURS" -> "HUR";

            case "STK",
                 "STÜCK",
                 "STUECK",
                 "PIECE",
                 "PIECES" -> "C62";

            case "TAG",
                 "TAGE",
                 "DAY",
                 "DAYS" -> "DAY";

            default ->
                normalized.toUpperCase(
                    Locale.ROOT
                );
        };
    }

    private record TaxKey(
        String categoryCode,
        BigDecimal vatRate
    ) {
    }

    private record TaxBreakdown(
        TaxKey key,
        BigDecimal taxableAmount,
        BigDecimal taxAmount
    ) {
    }
}
