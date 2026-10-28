package com.bcs.rechnung.invoice.xrechnung.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Zentrales DTO für die Erzeugung einer XRechnung.
 *
 * Dieses DTO enthält ausschließlich die Daten,
 * die der InvoiceXmlGenerator für die XML-Erzeugung benötigt.
 *
 * Dadurch bleibt der XML-Generator unabhängig von
 * JPA-Entities und Hibernate-Beziehungen.
 */
@Getter
@Setter
public class XRechnungDto {

    // ---------------------------------------------------------
    // Rechnungsgrunddaten
    // ---------------------------------------------------------

    private String invoiceNumber;

    private LocalDate invoiceDate;

    private LocalDate serviceDate;

    private LocalDate dueDate;

    private LocalDate taxPointDate;

    // ---------------------------------------------------------
    // Referenzen
    // ---------------------------------------------------------

    private String buyerReference;

    private String orderReference;

    private String contractReference;

    private String projectReference;

    private String deliveryNoteReference;

    private String paymentReference;

    // ---------------------------------------------------------
    // XRechnung / EN 16931
    // ---------------------------------------------------------

    /**
     * Beispiel:
     *
     * 380 = Rechnung
     */
    private String invoiceTypeCode;

    /**
     * Beispiel:
     *
     * 58 = SEPA-Überweisung
     */
    private String paymentMeansCode;

    /**
     * ISO-4217-Währungscode.
     *
     * Beispiel:
     *
     * EUR
     */
    private String currencyCode;

    /**
     * Geschäftsprozesskennung.
     *
     * Beispiel:
     *
     * urn:fdc:peppol.eu:2017:poacc:billing:01:1.0
     */
    private String businessProcessId;

    /**
     * Spezifikationskennung.
     *
     * Beispiel:
     *
     * urn:xeinkauf.de:kosit:xrechnung_3.0
     */
    private String specificationIdentifier;

    // ---------------------------------------------------------
    // Verkäufer
    // ---------------------------------------------------------

    private XRechnungSellerDto seller;

    // ---------------------------------------------------------
    // Käufer
    // ---------------------------------------------------------

    private XRechnungBuyerDto buyer;

    // ---------------------------------------------------------
    // Zahlungsinformationen / Bankverbindung
    // ---------------------------------------------------------

    private String bankName;

    private String iban;

    private String bic;

    private String accountHolder;

    // ---------------------------------------------------------
    // Summen
    // ---------------------------------------------------------

    private XRechnungTotalsDto totals;

    // ---------------------------------------------------------
    // Hinweise
    // ---------------------------------------------------------

    private String notes;

    // ---------------------------------------------------------
    // Rechnungspositionen
    // ---------------------------------------------------------

    private List<XRechnungItemDto> items =
        new ArrayList<>();
}
