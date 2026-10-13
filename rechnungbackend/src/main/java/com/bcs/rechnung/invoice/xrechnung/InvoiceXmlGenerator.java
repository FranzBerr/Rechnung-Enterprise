package com.bcs.rechnung.invoice.xrechnung;

import com.bcs.rechnung.invoice.xrechnung.dto.XRechnungBuyerDto;
import com.bcs.rechnung.invoice.xrechnung.dto.XRechnungDto;
import com.bcs.rechnung.invoice.xrechnung.dto.XRechnungItemDto;
import com.bcs.rechnung.invoice.xrechnung.dto.XRechnungSellerDto;
import com.bcs.rechnung.invoice.xrechnung.dto.XRechnungTotalsDto;
import org.springframework.stereotype.Component;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

//Für prettyPrint
import javax.xml.XMLConstants;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;


@Component
public class InvoiceXmlGenerator {

    private static final String RSM =
        "urn:un:unece:uncefact:data:standard:CrossIndustryInvoice:100";

    private static final String RAM =
        "urn:un:unece:uncefact:data:standard:ReusableAggregateBusinessInformationEntity:100";

    private static final String UDT =
        "urn:un:unece:uncefact:data:standard:UnqualifiedDataType:100";

    private static final String QDT =
        "urn:un:unece:uncefact:data:standard:QualifiedDataType:100";

    private static final String XRECHNUNG_GUIDELINE_ID =
        "urn:cen.eu:en16931:2017#compliant#urn:xeinkauf.de:kosit:xrechnung_3.0";

    private static final String DEFAULT_BUSINESS_PROCESS_ID =
        "urn:fdc:peppol.eu:2017:poacc:billing:01:1.0";

    private static final DateTimeFormatter DATE_FORMAT =
        DateTimeFormatter.ofPattern("yyyyMMdd");

    public byte[] generate(XRechnungDto invoice) {
        validate(invoice);
        try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            XMLOutputFactory factory = XMLOutputFactory.newFactory();
            XMLStreamWriter writer = factory.createXMLStreamWriter(output, StandardCharsets.UTF_8.name());
            writeDocument(writer, invoice);
            writer.flush();
            writer.close();
            byte[] unformattedXml = output.toByteArray();
            return prettyPrint(unformattedXml);
        } catch (XMLStreamException exception) {
            throw new IllegalStateException("Die XRechnung-XML konnte nicht erzeugt werden.", exception);
        } catch (Exception exception) {
            throw new IllegalStateException("Die XRechnung-XML konnte nicht geschrieben werden.", exception);
        }
    }

    private void writeDocument(
        XMLStreamWriter writer,
        XRechnungDto invoice
    ) throws XMLStreamException {
        writer.writeStartDocument(StandardCharsets.UTF_8.name(), "1.0");
        writer.writeCharacters("\n");

        writer.setPrefix("rsm", RSM);
        writer.setPrefix("ram", RAM);
        writer.setPrefix("udt", UDT);
        writer.setPrefix("qdt", QDT);

        writer.writeStartElement("rsm", "CrossIndustryInvoice", RSM);
        writer.writeNamespace("rsm", RSM);
        writer.writeNamespace("ram", RAM);
        writer.writeNamespace("udt", UDT);
        writer.writeNamespace("qdt", QDT);

        writeDocumentContext(writer);
        writeExchangedDocument(writer, invoice);
        writeSupplyChainTradeTransaction(writer, invoice);

        writer.writeEndElement();
        writer.writeEndDocument();
    }

    private void writeDocumentContext(
        XMLStreamWriter writer
    ) throws XMLStreamException {
        startRsm(writer, "ExchangedDocumentContext");

        startRam(writer, "BusinessProcessSpecifiedDocumentContextParameter");
        elementRam(writer, "ID", DEFAULT_BUSINESS_PROCESS_ID);
        writer.writeEndElement();

        startRam(writer, "GuidelineSpecifiedDocumentContextParameter");
        elementRam(writer, "ID", XRECHNUNG_GUIDELINE_ID);
        writer.writeEndElement();

        writer.writeEndElement();
    }

    private void writeExchangedDocument(
        XMLStreamWriter writer,
        XRechnungDto invoice
    ) throws XMLStreamException {
        startRsm(writer, "ExchangedDocument");

        elementRam(writer, "ID", invoice.getInvoiceNumber());
        elementRam(
            writer,
            "TypeCode",
            defaultValue(invoice.getInvoiceTypeCode(), "380")
        );

        startRam(writer, "IssueDateTime");
        dateElement(writer, "DateTimeString", invoice.getInvoiceDate());
        writer.writeEndElement();

        writer.writeEndElement();
    }

    private void writeSupplyChainTradeTransaction(
        XMLStreamWriter writer,
        XRechnungDto invoice
    ) throws XMLStreamException {

        startRsm(
            writer,
            "SupplyChainTradeTransaction"
        );

        writeLineItems(
            writer,
            invoice
        );

        writeHeaderTradeAgreement(
            writer,
            invoice
        );

        writeHeaderTradeDelivery(
            writer,
            invoice
        );

        writeHeaderTradeSettlement(
            writer,
            invoice
        );

        writer.writeEndElement();
    }

    private void writeLineItems(
        XMLStreamWriter writer,
        XRechnungDto invoice
    ) throws XMLStreamException {
        for (XRechnungItemDto item : invoice.getItems()) {
            startRam(writer, "IncludedSupplyChainTradeLineItem");

            startRam(writer, "AssociatedDocumentLineDocument");
            elementRam(
                writer,
                "LineID",
                String.valueOf(item.getPositionNumber())
            );
            writer.writeEndElement();

            startRam(writer, "SpecifiedTradeProduct");

            optionalElementRam(
                writer,
                "SellerAssignedID",
                item.getProductNumber()
            );

            elementRam(
                writer,
                "Name",
                item.getName()
            );

            optionalElementRam(
                writer,
                "Description",
                item.getDescription()
            );

            writer.writeEndElement();
            startRam(writer, "SpecifiedLineTradeAgreement");
            startRam(writer, "NetPriceProductTradePrice");
            amountElement(
                writer,
                "ChargeAmount",
                item.getUnitPriceNet(),
                null
            );
            writer.writeEndElement();
            writer.writeEndElement();

            startRam(writer, "SpecifiedLineTradeDelivery");
            quantityElement(
                writer,
                "BilledQuantity",
                item.getQuantity(),
                normalizeUnitCode(item.getUnit())
            );
            writer.writeEndElement();

            startRam(writer, "SpecifiedLineTradeSettlement");

            startRam(writer, "ApplicableTradeTax");
            elementRam(writer, "TypeCode", "VAT");
            elementRam(
                writer,
                "CategoryCode",
                defaultValue(item.getTaxCategoryCode(), "S")
            );
            elementRam(writer, "RateApplicablePercent", decimal(item.getVatRate(), 2));
            writer.writeEndElement();

            startRam(writer, "SpecifiedTradeSettlementLineMonetarySummation");
            amountElement(
                writer,
                "LineTotalAmount",
                item.getLineNet(),
                null
//                invoice.getCurrencyCode()
            );
            writer.writeEndElement();

            writer.writeEndElement();
            writer.writeEndElement();
        }
    }

    private void writeHeaderTradeAgreement(
        XMLStreamWriter writer,
        XRechnungDto invoice
    ) throws XMLStreamException {
        startRam(writer, "ApplicableHeaderTradeAgreement");

        elementRam(writer, "BuyerReference", invoice.getBuyerReference());

        writeSeller(writer, invoice.getSeller());
        writeBuyer(writer, invoice.getBuyer());

        if (notBlank(invoice.getOrderReference())) {
            startRam(writer, "BuyerOrderReferencedDocument");
            elementRam(writer, "IssuerAssignedID", invoice.getOrderReference());
            writer.writeEndElement();
        }

        writer.writeEndElement();
    }

    private void writeSeller(
        XMLStreamWriter writer,
        XRechnungSellerDto seller
    ) throws XMLStreamException {

        startRam(
            writer,
            "SellerTradeParty"
        );

        elementRam(
            writer,
            "Name",
            seller.getName()
        );
        if (notBlank(seller.getContactPerson())
            || notBlank(seller.getPhone())
            || notBlank(seller.getEmail())) {

            startRam(
                writer,
                "DefinedTradeContact"
            );

            optionalElementRam(
                writer,
                "PersonName",
                seller.getContactPerson()
            );

            if (notBlank(seller.getPhone())) {

                startRam(
                    writer,
                    "TelephoneUniversalCommunication"
                );

                elementRam(
                    writer,
                    "CompleteNumber",
                    seller.getPhone()
                );

                writer.writeEndElement();
            }

            if (notBlank(seller.getEmail())) {

                startRam(
                    writer,
                    "EmailURIUniversalCommunication"
                );

                elementRam(
                    writer,
                    "URIID",
                    seller.getEmail()
                );

                writer.writeEndElement();
            }

            writer.writeEndElement();
        }
        startRam(
            writer,
            "PostalTradeAddress"
        );

        elementRam(
            writer,
            "PostcodeCode",
            seller.getZipCode()
        );

        elementRam(
            writer,
            "LineOne",
            seller.getStreet()
        );

        elementRam(
            writer,
            "CityName",
            seller.getCity()
        );

        elementRam(
            writer,
            "CountryID",
            seller.getCountry()
        );

        writer.writeEndElement();

        /*
         * URIUniversalCommunication vor den Steuerregistrierungen.
         */
        if (notBlank(seller.getElectronicAddress())) {

            startRam(
                writer,
                "URIUniversalCommunication"
            );

            writer.writeStartElement(
                "ram",
                "URIID",
                RAM
            );

            writer.writeAttribute(
                "schemeID",
                defaultValue(
                    seller.getElectronicAddressScheme(),
                    "EM"
                )
            );

            writer.writeCharacters(
                seller.getElectronicAddress()
            );

            writer.writeEndElement();
            writer.writeEndElement();
        }

        if (notBlank(seller.getVatId())) {

            startRam(
                writer,
                "SpecifiedTaxRegistration"
            );

            writer.writeStartElement(
                "ram",
                "ID",
                RAM
            );

            writer.writeAttribute(
                "schemeID",
                "VA"
            );

            writer.writeCharacters(
                seller.getVatId()
            );

            writer.writeEndElement();
            writer.writeEndElement();
        }

        if (notBlank(seller.getTaxNumber())) {

            startRam(
                writer,
                "SpecifiedTaxRegistration"
            );

            writer.writeStartElement(
                "ram",
                "ID",
                RAM
            );

            writer.writeAttribute(
                "schemeID",
                "FC"
            );

            writer.writeCharacters(
                seller.getTaxNumber()
            );

            writer.writeEndElement();
            writer.writeEndElement();
        }

        writer.writeEndElement();
    }

    private void writeBuyer(
        XMLStreamWriter writer,
        XRechnungBuyerDto buyer
    ) throws XMLStreamException {
        startRam(writer, "BuyerTradeParty");

        if (notBlank(buyer.getLeitwegId())) {
            elementRam(writer, "ID", buyer.getLeitwegId());
        }

        elementRam(writer, "Name", buyer.getName());

        startRam(writer, "PostalTradeAddress");
        elementRam(writer, "PostcodeCode", buyer.getZipCode());
        elementRam(writer, "LineOne", buyer.getStreet());
        elementRam(writer, "CityName", buyer.getCity());
        elementRam(writer, "CountryID", buyer.getCountry());
        writer.writeEndElement();

        if (notBlank(buyer.getElectronicAddress())) {
            startRam(writer, "URIUniversalCommunication");
            writer.writeStartElement("ram", "URIID", RAM);
            if (notBlank(buyer.getElectronicAddressScheme())) {
                writer.writeAttribute(
                    "schemeID",
                    buyer.getElectronicAddressScheme()
                );
            }
            writer.writeCharacters(buyer.getElectronicAddress());
            writer.writeEndElement();
            writer.writeEndElement();
        }

        writer.writeEndElement();
    }

    private void writeHeaderTradeDelivery(
        XMLStreamWriter writer,
        XRechnungDto invoice
    ) throws XMLStreamException {
        startRam(writer, "ApplicableHeaderTradeDelivery");

        LocalDate serviceDate = invoice.getServiceDate();
        if (serviceDate != null) {
            startRam(writer, "ActualDeliverySupplyChainEvent");
            startRam(writer, "OccurrenceDateTime");
            dateElement(writer, "DateTimeString", serviceDate);
            writer.writeEndElement();
            writer.writeEndElement();
        }

        writer.writeEndElement();
    }

    private void writeHeaderTradeSettlement(
        XMLStreamWriter writer,
        XRechnungDto invoice
    ) throws XMLStreamException {

        XRechnungTotalsDto totals =
            invoice.getTotals();

        String currency =
            defaultValue(
                invoice.getCurrencyCode(),
                "EUR"
            );

        String categoryCode =
            resolveTaxCategory(
                invoice.getItems()
            );

        BigDecimal taxRate =
            resolveTaxRate(
                invoice.getItems()
            );

        /*
         * BG-19 / HEADER TRADE SETTLEMENT
         */
        startRam(
            writer,
            "ApplicableHeaderTradeSettlement"
        );

        /*
         * BT-83 Zahlungsreferenz
         */
        optionalElementRam(
            writer,
            "PaymentReference",
            invoice.getPaymentReference()
        );

        /*
         * BT-5 Rechnungswährung
         */
        elementRam(
            writer,
            "InvoiceCurrencyCode",
            currency
        );

        /*
         * BG-16 Zahlungsanweisungen
         */
        writePaymentMeans(
            writer,
            invoice
        );

        /*
         * BG-23 Umsatzsteueraufschlüsselung
         */
        startRam(
            writer,
            "ApplicableTradeTax"
        );

        /*
         * BT-117 Steuerbetrag
         */
        amountElement(
            writer,
            "CalculatedAmount",
            totals.getTaxAmount(),
            null
        );

        /*
         * BT-118 Steuerart
         */
        elementRam(
            writer,
            "TypeCode",
            "VAT"
        );

        /*
         * BT-116 Steuerbasis
         */
        amountElement(
            writer,
            "BasisAmount",
            totals.getSubtotalNet(),
            null
        );

        /*
         * BT-118 Steuerkategorie
         */
        elementRam(
            writer,
            "CategoryCode",
            categoryCode
        );

        /*
         * BT-119 Steuersatz
         */
        elementRam(
            writer,
            "RateApplicablePercent",
            decimal(
                taxRate,
                2
            )
        );

        /*
         * ApplicableTradeTax schließen
         */
        writer.writeEndElement();

        /*
         * BG-16 Zahlungsbedingungen
         */
        startRam(
            writer,
            "SpecifiedTradePaymentTerms"
        );

        /*
         * BT-9 Fälligkeitsdatum
         */
        startRam(
            writer,
            "DueDateDateTime"
        );

        dateElement(
            writer,
            "DateTimeString",
            invoice.getDueDate()
        );

        /*
         * DueDateDateTime schließen
         */
        writer.writeEndElement();

        /*
         * SpecifiedTradePaymentTerms schließen
         */
        writer.writeEndElement();

        /*
         * BG-22 Rechnungssummen
         */
        startRam(
            writer,
            "SpecifiedTradeSettlementHeaderMonetarySummation"
        );

        /*
         * BT-106 Summe der Positionsnettobeträge
         */
        amountElement(
            writer,
            "LineTotalAmount",
            totals.getSubtotalNet(),
            null
        );

        /*
         * BT-107 Summe der Zuschläge
         *
         * Da momentan keine Zuschläge vorhanden sind,
         * wird kein ChargeTotalAmount erzeugt.
         */

        /*
         * BT-108 Summe der Nachlässe
         *
         * Da momentan keine rechnungsbezogenen Nachlässe
         * vorhanden sind, wird kein AllowanceTotalAmount erzeugt.
         */

        /*
         * BT-109 Rechnungsbetrag ohne Umsatzsteuer
         */
        amountElement(
            writer,
            "TaxBasisTotalAmount",
            totals.getSubtotalNet(),
            null
        );

        /*
         * BT-110 Umsatzsteuerbetrag
         */
        amountElement(
            writer,
            "TaxTotalAmount",
            totals.getTaxAmount(),
            currency
        );

        /*
         * BT-112 Rechnungsbetrag inklusive Umsatzsteuer
         */
        amountElement(
            writer,
            "GrandTotalAmount",
            totals.getTotalGross(),
            null
        );

        /*
         * BT-115 Zahlbetrag
         */
        amountElement(
            writer,
            "DuePayableAmount",
            totals.getTotalGross(),
            null
        );

        /*
         * SpecifiedTradeSettlementHeaderMonetarySummation schließen
         */
        writer.writeEndElement();

        /*
         * ApplicableHeaderTradeSettlement schließen
         */
        writer.writeEndElement();
    }

    private String resolveTaxCategory(List<XRechnungItemDto> items) {
        if (items == null || items.isEmpty()) {
            return "S";
        }
        return defaultValue(items.get(0).getTaxCategoryCode(), "S");
    }

    private BigDecimal resolveTaxRate(List<XRechnungItemDto> items) {
        if (items == null || items.isEmpty() || items.get(0).getVatRate() == null) {
            return BigDecimal.ZERO;
        }
        return items.get(0).getVatRate();
    }

    private void validate(
        XRechnungDto invoice
    ) {
        if (invoice == null) {
            throw new IllegalArgumentException(
                "Die XRechnung darf nicht null sein."
            );
        }

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
            "Buyer Reference"
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
            "Summen"
        );

        if (invoice.getItems() == null
            || invoice.getItems().isEmpty()) {

            throw new IllegalArgumentException(
                "Die XRechnung muss mindestens "
                    + "eine Position enthalten."
            );
        }

        validatePaymentInformation(
            invoice
        );
    }
    private void validatePaymentInformation(
        XRechnungDto invoice
    ) {
        String paymentMeansCode =
            defaultValue(
                invoice.getPaymentMeansCode(),
                "58"
            );

        if ("58".equals(paymentMeansCode)) {
            requireText(
                invoice.getIban(),
                "IBAN bei SEPA-Überweisung"
            );
        }

        if (notBlank(invoice.getIban())) {
            validateIban(
                invoice.getIban()
            );
        }

        if (notBlank(invoice.getBic())) {
            validateBic(
                invoice.getBic()
            );
        }
    }
    private void validateIban(
        String iban
    ) {
        String normalizedIban =
            normalizeIban(
                iban
            );

        if (normalizedIban == null) {
            throw new IllegalArgumentException(
                "Die IBAN fehlt."
            );
        }

        if (!normalizedIban.matches(
            "[A-Z]{2}[0-9]{2}[A-Z0-9]+"
        )) {
            throw new IllegalArgumentException(
                "Die IBAN besitzt ein ungültiges Format."
            );
        }

        if (normalizedIban.length() < 15
            || normalizedIban.length() > 34) {

            throw new IllegalArgumentException(
                "Die IBAN muss zwischen 15 und 34 "
                    + "Zeichen enthalten."
            );
        }

        if (normalizedIban.startsWith("DE")
            && normalizedIban.length() != 22) {

            throw new IllegalArgumentException(
                "Eine deutsche IBAN muss genau "
                    + "22 Zeichen enthalten. Aktuelle Länge: "
                    + normalizedIban.length()
                    + "."
            );
        }

        String rearranged =
            normalizedIban.substring(4)
                + normalizedIban.substring(0, 4);

        int remainder =
            calculateIbanRemainder(
                rearranged
            );

        if (remainder != 1) {
            throw new IllegalArgumentException(
                "Die IBAN besitzt eine ungültige Prüfsumme."
            );
        }
    }
    private int calculateIbanRemainder(
        String value
    ) {
        int remainder =
            0;

        for (int index = 0;
             index < value.length();
             index++) {

            char character =
                value.charAt(index);

            if (Character.isDigit(character)) {
                remainder =
                    appendDigitToRemainder(
                        remainder,
                        character - '0'
                    );

            } else if (character >= 'A'
                && character <= 'Z') {

                int numericValue =
                    character - 'A' + 10;

                remainder =
                    appendDigitToRemainder(
                        remainder,
                        numericValue / 10
                    );

                remainder =
                    appendDigitToRemainder(
                        remainder,
                        numericValue % 10
                    );

            } else {
                throw new IllegalArgumentException(
                    "Die IBAN enthält ein ungültiges Zeichen: "
                        + character
                );
            }
        }

        return remainder;
    }
    private int appendDigitToRemainder(
        int remainder,
        int digit
    ) {
        return (
            remainder * 10
                + digit
        ) % 97;
    }

    private void validateBic(
        String bic
    ) {
        String normalizedBic =
            normalizeBic(
                bic
            );

        if (normalizedBic == null) {
            return;
        }

        if (!normalizedBic.matches(
            "[A-Z]{6}[A-Z0-9]{2}([A-Z0-9]{3})?"
        )) {
            throw new IllegalArgumentException(
                "Der BIC besitzt ein ungültiges Format. "
                    + "Erwartet werden 8 oder 11 Zeichen."
            );
        }
    }
    private void startRsm(XMLStreamWriter writer, String localName)
        throws XMLStreamException {
        writer.writeStartElement("rsm", localName, RSM);
    }

    private void startRam(XMLStreamWriter writer, String localName)
        throws XMLStreamException {
        writer.writeStartElement("ram", localName, RAM);
    }

    private void elementRam(
        XMLStreamWriter writer,
        String localName,
        String value
    ) throws XMLStreamException {
        writer.writeStartElement("ram", localName, RAM);
        writer.writeCharacters(value == null ? "" : value);
        writer.writeEndElement();
    }

    private void optionalElementRam(
        XMLStreamWriter writer,
        String localName,
        String value
    ) throws XMLStreamException {
        if (notBlank(value)) {
            elementRam(writer, localName, value);
        }
    }

    private void dateElement(
        XMLStreamWriter writer,
        String localName,
        LocalDate value
    ) throws XMLStreamException {
        writer.writeStartElement("udt", localName, UDT);
        writer.writeAttribute("format", "102");
        writer.writeCharacters(value.format(DATE_FORMAT));
        writer.writeEndElement();
    }

    private void amountElement(
        XMLStreamWriter writer,
        String localName,
        BigDecimal value,
        String currency
    ) throws XMLStreamException {
        writer.writeStartElement("ram", localName, RAM);
        if (notBlank(currency)) {
            writer.writeAttribute("currencyID", currency);
        }
        writer.writeCharacters(decimal(value, 2));
        writer.writeEndElement();
    }

    private void quantityElement(
        XMLStreamWriter writer,
        String localName,
        BigDecimal value,
        String unitCode
    ) throws XMLStreamException {
        writer.writeStartElement("ram", localName, RAM);
        writer.writeAttribute("unitCode", unitCode);
        writer.writeCharacters(decimal(value, 4));
        writer.writeEndElement();
    }

    private String normalizeUnitCode(String value) {
        if (!notBlank(value)) {
            return "C62";
        }

        String normalized = value.trim().toUpperCase();
        if ("STD".equals(normalized)) {
            return "HUR";
        }
        return normalized;
    }

    private String decimal(BigDecimal value, int scale) {
        BigDecimal safeValue = value == null ? BigDecimal.ZERO : value;
        return safeValue
            .setScale(scale, RoundingMode.HALF_UP)
            .toPlainString();
    }

    private String defaultValue(String value, String fallback) {
        return notBlank(value) ? value.trim() : fallback;
    }

    private boolean notBlank(String value) {
        return value != null && !value.trim().isEmpty();
    }

    private void requireText(String value, String fieldName) {
        if (!notBlank(value)) {
            throw new IllegalArgumentException(
                fieldName + " fehlt für die XRechnung."
            );
        }
    }

    private void requireNotNull(Object value, String fieldName) {
        if (value == null) {
            throw new IllegalArgumentException(
                fieldName + " fehlt für die XRechnung."
            );
        }
    }

    private byte[] prettyPrint(
        byte[] xml
    ) {
        if (xml == null || xml.length == 0) {
            throw new IllegalArgumentException(
                "Die zu formatierende XML darf nicht leer sein."
            );
        }

        try (
            ByteArrayInputStream input =
                new ByteArrayInputStream(xml);

            ByteArrayOutputStream output =
                new ByteArrayOutputStream()
        ) {
            TransformerFactory factory =
                TransformerFactory.newInstance();

            factory.setFeature(
                XMLConstants.FEATURE_SECURE_PROCESSING,
                true
            );

            setTransformerFactoryAttribute(
                factory,
                XMLConstants.ACCESS_EXTERNAL_DTD,
                ""
            );

            setTransformerFactoryAttribute(
                factory,
                XMLConstants.ACCESS_EXTERNAL_STYLESHEET,
                ""
            );

            Transformer transformer =
                factory.newTransformer();

            transformer.setOutputProperty(
                OutputKeys.METHOD,
                "xml"
            );

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
                OutputKeys.STANDALONE,
                "no"
            );

            setTransformerOutputProperty(
                transformer,
                "{http://xml.apache.org/xslt}indent-amount",
                "4"
            );

            transformer.transform(
                new StreamSource(input),
                new StreamResult(output)
            );

            return output.toByteArray();

        } catch (Exception exception) {
            throw new IllegalStateException(
                "Die XRechnung-XML konnte nicht formatiert werden.",
                exception
            );
        }
    }

    private void setTransformerFactoryAttribute(TransformerFactory factory, String name, String value) {
        try {
            factory.setAttribute(name, value);
        } catch (
            IllegalArgumentException exception) { /*
         * Manche Transformer-Implementierungen unterstützen
         * diese Sicherheitsoption nicht als Attribut. *
         * FEATURE_SECURE_PROCESSING bleibt trotzdem aktiv.
         */
        }
    }

    private void setTransformerOutputProperty(Transformer transformer, String name, String value) {
        try {
            transformer.setOutputProperty(name, value);
        } catch (IllegalArgumentException exception) {
            /*
             * indent-amount ist eine optionale Erweiterung.
             */
        }
    }

    private void writePaymentMeans(
        XMLStreamWriter writer,
        XRechnungDto invoice
    ) throws XMLStreamException {

        /*
         * BG-16 Zahlungsanweisungen
         */
        startRam(
            writer,
            "SpecifiedTradeSettlementPaymentMeans"
        );

        /*
         * BT-81 Zahlungsmittelcode
         *
         * 58 = SEPA-Überweisung
         */
        elementRam(
            writer,
            "TypeCode",
            defaultValue(
                invoice.getPaymentMeansCode(),
                "58"
            )
        );

        /*
         * BG-17 Zahlungskonto
         */
        if (notBlank(invoice.getIban())) {

            startRam(
                writer,
                "PayeePartyCreditorFinancialAccount"
            );

            /*
             * BT-84 IBAN
             */
            elementRam(
                writer,
                "IBANID",
                normalizeIban(
                    invoice.getIban()
                )
            );

            /*
             * BT-85 Kontoinhaber
             */
            optionalElementRam(
                writer,
                "AccountName",
                invoice.getAccountHolder()
            );

            /*
             * PayeePartyCreditorFinancialAccount schließen
             */
            writer.writeEndElement();
        }

        /*
         * Bankinstitut
         */
        if (notBlank(invoice.getBic())) {

            startRam(
                writer,
                "PayeeSpecifiedCreditorFinancialInstitution"
            );

            elementRam(
                writer,
                "BICID",
                normalizeBic(
                    invoice.getBic()
                )
            );

            /*
             * PayeeSpecifiedCreditorFinancialInstitution schließen
             */
            writer.writeEndElement();
        }

        /*
         * SpecifiedTradeSettlementPaymentMeans schließen
         */
        writer.writeEndElement();
    }

    private String normalizeIban(
        String iban
    ) {
        if (!notBlank(iban)) {
            return null;
        }

        return iban
            .replaceAll("\\s+", "")
            .toUpperCase();
    }

    private String normalizeBic(
        String bic
    ) {
        if (!notBlank(bic)) {
            return null;
        }

        return bic
            .replaceAll("\\s+", "")
            .toUpperCase();
    }
}
