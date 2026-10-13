package com.bcs.rechnung.invoice.document;

import com.bcs.rechnung.invoice.document.dto.ArchivedDocumentDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/document-archive")
@RequiredArgsConstructor
@Tag(
    name = "Document Archive",
    description = "Archivierte Rechnungsdokumente"
)
public class InvoiceDocumentArchiveController {

    private final InvoiceDocumentArchiveService service;

    @GetMapping
    @Operation(
        summary = "Alle Archivdatensätze lesen"
    )
    public ResponseEntity<List<ArchivedDocumentDto>>
    findAll() {

        return ResponseEntity.ok(
            service.findAll()
        );
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Archivdatensatz über Archiv-ID lesen"
    )
    public ResponseEntity<ArchivedDocumentDto>
    findById(
        @PathVariable Long id
    ) {

        return ResponseEntity.ok(
            service.findById(
                id
            )
        );
    }

    @GetMapping("/invoice/{invoiceId}")
    @Operation(
        summary = "Archivdatensatz über Rechnungs-ID lesen"
    )
    public ResponseEntity<ArchivedDocumentDto>
    findByInvoiceId(
        @PathVariable Long invoiceId
    ) {

        return ResponseEntity.ok(
            service.findByInvoiceId(
                invoiceId
            )
        );
    }

    @GetMapping("/invoice-number/{invoiceNumber}")
    @Operation(
        summary = "Archivdatensatz über Rechnungsnummer lesen"
    )
    public ResponseEntity<ArchivedDocumentDto>
    findByInvoiceNumber(
        @PathVariable String invoiceNumber
    ) {

        return ResponseEntity.ok(
            service.findByInvoiceNumber(
                invoiceNumber
            )
        );
    }

    @GetMapping("/{id}/pdf")
    @Operation(
        summary = "PDF über Archiv-ID herunterladen"
    )
    public ResponseEntity<Resource>
    downloadPdf(
        @PathVariable Long id
    ) {

        return service.downloadPdf(
            id
        );
    }

    @GetMapping("/{id}/xml")
    @Operation(
        summary = "XML über Archiv-ID herunterladen"
    )
    public ResponseEntity<Resource>
    downloadXml(
        @PathVariable Long id
    ) {

        return service.downloadXml(
            id
        );
    }

    @GetMapping("/{id}/validation-report")
    @Operation(
        summary = "Prüfbericht über Archiv-ID herunterladen"
    )
    public ResponseEntity<Resource>
    downloadValidationReport(
        @PathVariable Long id
    ) {

        return service.downloadValidationReport(
            id
        );
    }

    @GetMapping("/{id}/validator-log")
    @Operation(
        summary = "Validator Log über Archiv-ID herunterladen"
    )
    public ResponseEntity<Resource>
    downloadValidatorLog(
        @PathVariable Long id
    ) {

        return service.downloadValidatorLog(
            id
        );
    }

    @GetMapping("/invoice/{invoiceId}/pdf")
    @Operation(
        summary = "PDF über Rechnungs-ID herunterladen"
    )
    public ResponseEntity<Resource>
    downloadPdfByInvoiceId(
        @PathVariable Long invoiceId
    ) {

        return service.downloadPdfByInvoiceId(
            invoiceId
        );
    }

    @GetMapping("/invoice/{invoiceId}/xml")
    @Operation(
        summary = "XML über Rechnungs-ID herunterladen"
    )
    public ResponseEntity<Resource>
    downloadXmlByInvoiceId(
        @PathVariable Long invoiceId
    ) {

        return service.downloadXmlByInvoiceId(
            invoiceId
        );
    }

    @GetMapping(
        "/invoice/{invoiceId}/validation-report"
    )
    @Operation(
        summary = "Prüfbericht über Rechnungs-ID herunterladen"
    )
    public ResponseEntity<Resource>
    downloadValidationReportByInvoiceId(
        @PathVariable Long invoiceId
    ) {

        return service.downloadValidationReportByInvoiceId(
            invoiceId
        );
    }

    @GetMapping(
        "/invoice/{invoiceId}/validator-log"
    )
    @Operation(
        summary = "Validator Log über Rechnungs-ID herunterladen"
    )
    public ResponseEntity<Resource>
    downloadValidatorLogByInvoiceId(
        @PathVariable Long invoiceId
    ) {

        return service.downloadValidatorLogByInvoiceId(
            invoiceId
        );
    }
    @GetMapping("/{id}/zip")
    @Operation(
        summary = "Archiv als ZIP herunterladen"
    )
    public ResponseEntity<Resource>
    downloadZip(
        @PathVariable Long id
    ) {

        return service.downloadZip(
            id
        );
    }

    @GetMapping(
        "/invoice/{invoiceId}/zip"
    )
    @Operation(
        summary = "Archiv als ZIP über Rechnungs-ID herunterladen"
    )
    public ResponseEntity<Resource>
    downloadZipByInvoiceId(
        @PathVariable Long invoiceId
    ) {

        return service.downloadZipByInvoiceId(
            invoiceId
        );
    }
}
