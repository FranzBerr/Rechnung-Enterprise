package com.bcs.rechnung.invoice.document;

import com.bcs.rechnung.invoice.document.dto.ArchivedDocumentDto;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface InvoiceDocumentArchiveService {

    List<ArchivedDocumentDto> findAll();

    ArchivedDocumentDto findById(
        Long id
    );

    ArchivedDocumentDto findByInvoiceId(
        Long invoiceId
    );

    ArchivedDocumentDto findByInvoiceNumber(
        String invoiceNumber
    );

    ResponseEntity<Resource> downloadPdf(
        Long id
    );

    ResponseEntity<Resource> downloadXml(
        Long id
    );

    ResponseEntity<Resource> downloadValidationReport(
        Long id
    );

    ResponseEntity<Resource> downloadValidatorLog(
        Long id
    );

    ResponseEntity<Resource> downloadPdfByInvoiceId(
        Long invoiceId
    );

    ResponseEntity<Resource> downloadXmlByInvoiceId(
        Long invoiceId
    );

    ResponseEntity<Resource> downloadValidationReportByInvoiceId(
        Long invoiceId
    );

    ResponseEntity<Resource> downloadValidatorLogByInvoiceId(
        Long invoiceId
    );

    ResponseEntity<Resource> downloadZip(
        Long id
    );

    ResponseEntity<Resource> downloadZipByInvoiceId(
        Long invoiceId
    );
}
