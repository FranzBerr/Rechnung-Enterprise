package com.bcs.rechnung.invoice.document;

import com.bcs.rechnung.invoice.document.dto.ArchivedDocumentDto;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface InvoiceDocumentArchiveService {

    /**
     * Erzeugt, validiert und archiviert die Dokumente
     * einer Rechnung.
     *
     * @param invoiceId ID der Rechnung
     * @return gespeicherter Archivdatensatz
     */
    ArchivedDocumentDto archive(
        Long invoiceId
    );

    /**
     * Macht den Archivstatus einer Rechnung rückgängig.
     *
     * Dabei werden:
     *
     * - archived auf false gesetzt
     * - archivedAt auf null gesetzt
     * - archivePath auf null gesetzt
     * - der Datensatz aus invoice_document_archive gelöscht
     *
     * Die physischen Dateien im Archivverzeichnis
     * werden nicht gelöscht.
     *
     * @param invoiceId ID der Rechnung
     */
    void undoArchive(
        Long invoiceId
    );

    /**
     * Prüft, ob eine Rechnung vollständig archiviert ist.
     *
     * Zusätzlich zum Archivstatus werden die gespeicherten
     * Archivdateien geprüft.
     *
     * @param invoiceId ID der Rechnung
     * @return true, wenn die Rechnung vollständig archiviert ist
     */
    boolean isArchived(
        Long invoiceId
    );

    /**
     * Liefert alle Archivdatensätze.
     */
    List<ArchivedDocumentDto> findAll();

    /**
     * Liefert einen Archivdatensatz anhand seiner Archiv-ID.
     */
    ArchivedDocumentDto findById(
        Long id
    );

    /**
     * Liefert den Archivdatensatz einer Rechnung.
     */
    ArchivedDocumentDto findByInvoiceId(
        Long invoiceId
    );

    /**
     * Liefert den Archivdatensatz anhand der Rechnungsnummer.
     */
    ArchivedDocumentDto findByInvoiceNumber(
        String invoiceNumber
    );

    /**
     * Lädt die archivierte PDF anhand der Archiv-ID.
     */
    ResponseEntity<Resource> downloadPdf(
        Long id
    );

    /**
     * Lädt die archivierte XRechnung-XML
     * anhand der Archiv-ID.
     */
    ResponseEntity<Resource> downloadXml(
        Long id
    );

    /**
     * Lädt den KoSIT-Prüfbericht
     * anhand der Archiv-ID.
     */
    ResponseEntity<Resource> downloadValidationReport(
        Long id
    );

    /**
     * Lädt das Validator-Protokoll
     * anhand der Archiv-ID.
     */
    ResponseEntity<Resource> downloadValidatorLog(
        Long id
    );

    /**
     * Lädt die archivierte PDF
     * anhand der Rechnungs-ID.
     */
    ResponseEntity<Resource> downloadPdfByInvoiceId(
        Long invoiceId
    );

    /**
     * Lädt die archivierte XRechnung-XML
     * anhand der Rechnungs-ID.
     */
    ResponseEntity<Resource> downloadXmlByInvoiceId(
        Long invoiceId
    );

    /**
     * Lädt den KoSIT-Prüfbericht
     * anhand der Rechnungs-ID.
     */
    ResponseEntity<Resource> downloadValidationReportByInvoiceId(
        Long invoiceId
    );

    /**
     * Lädt das Validator-Protokoll
     * anhand der Rechnungs-ID.
     */
    ResponseEntity<Resource> downloadValidatorLogByInvoiceId(
        Long invoiceId
    );

    /**
     * Lädt alle archivierten Rechnungsdokumente
     * als ZIP anhand der Archiv-ID.
     */
    ResponseEntity<Resource> downloadZip(
        Long id
    );

    /**
     * Lädt alle archivierten Rechnungsdokumente
     * als ZIP anhand der Rechnungs-ID.
     */
    ResponseEntity<Resource> downloadZipByInvoiceId(
        Long invoiceId
    );
}
