package com.bcs.rechnung.invoice.document;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
@Tag(
    name = "Invoice Documents",
    description = "Erzeugen und Archivieren von Dokumenten"
)
public class InvoiceDocumentController {

    private final InvoiceDocumentService service;

    @PostMapping("/{invoiceId}/documents/save")
    @Operation(
        summary = "PDF und XRechnung erzeugen, validieren und archivieren"
    )
    @ApiResponses({

        @ApiResponse(
            responseCode = "200",
            description = "Dokumente erfolgreich erzeugt und archiviert"
        ),

        @ApiResponse(
            responseCode = "404",
            description = "Rechnung nicht gefunden",
            content = @Content(
                examples = @ExampleObject(
                    name = "Rechnung nicht gefunden",
                    value = """
                    {
                      "status": 404,
                      "error": "Not Found",
                      "message": "Rechnung mit ID 999 wurde nicht gefunden.",
                      "timestamp": "2026-10-10T17:10:15"
                    }
                    """
                )
            )
        ),

        @ApiResponse(
            responseCode = "409",
            description = "Rechnung wurde bereits archiviert",
            content = @Content(
                schema = @Schema(
                    implementation =
                        InvoiceAlreadyArchivedResponse.class
                ),
                examples = @ExampleObject(
                    name = "Bereits archiviert",
                    value = """
                    {
                      "status": 409,
                      "error": "Conflict",
                      "message": "Die Rechnung RE-2026-000002 wurde bereits am 10.10.2026 16:55:20 archiviert.",
                      "invoiceId": 4,
                      "invoiceNumber": "RE-2026-000002",
                      "archivedAt": "2026-10-10T16:55:20.6806421",
                      "timestamp": "2026-10-10T17:12:45"
                    }
                    """
                )
            )
        ),

        @ApiResponse(
            responseCode = "422",
            description = "XRechnung Validierung fehlgeschlagen",
            content = @Content(
                examples = @ExampleObject(
                    name = "KoSIT Validation Error",
                    value = """
                    {
                      "status": 422,
                      "error": "Unprocessable Entity",
                      "message": "XRechnung Validierung fehlgeschlagen.",
                      "invoiceNumber": "RE-2026-000002",
                      "errors": [
                        "BT-10 Buyer reference fehlt"
                      ],
                      "warnings": [
                        "Empfohlene PEPPOL Angabe fehlt"
                      ]
                    }
                    """
                )
            )
        )
    })
    public ResponseEntity<ArchivedInvoiceDocuments>
    generateValidateAndArchive(
        @PathVariable Long invoiceId
    ) {

        return ResponseEntity.ok(
            service.generateValidateAndArchive(
                invoiceId
            )
        );
    }

    @PostMapping("/{invoiceId}/documents/archive")
    @Operation(
        summary = "Vorhandene Dokumente validieren und archivieren"
    )
    @ApiResponses({

        @ApiResponse(
            responseCode = "200",
            description = "Dokumente erfolgreich archiviert"
        ),

        @ApiResponse(
            responseCode = "404",
            description = "PDF, XML oder Rechnung nicht gefunden",
            content = @Content(
                examples = @ExampleObject(
                    name = "Dokument nicht gefunden",
                    value = """
                    {
                      "status": 404,
                      "error": "Not Found",
                      "message": "PDF-Rechnung wurde nicht gefunden: D:/archive/RE-2026-000002.pdf",
                      "timestamp": "2026-10-10T17:05:44"
                    }
                    """
                )
            )
        ),

        @ApiResponse(
            responseCode = "409",
            description = "Rechnung wurde bereits archiviert",
            content = @Content(
                schema = @Schema(
                    implementation =
                        InvoiceAlreadyArchivedResponse.class
                ),
                examples = @ExampleObject(
                    name = "Bereits archiviert",
                    value = """
                    {
                      "status": 409,
                      "error": "Conflict",
                      "message": "Die Rechnung RE-2026-000002 wurde bereits am 10.10.2026 16:55:20 archiviert.",
                      "invoiceId": 4,
                      "invoiceNumber": "RE-2026-000002",
                      "archivedAt": "2026-10-10T16:55:20.6806421",
                      "timestamp": "2026-10-10T17:12:45"
                    }
                    """
                )
            )
        ),

        @ApiResponse(
            responseCode = "422",
            description = "KoSIT Validierung fehlgeschlagen",
            content = @Content(
                examples = @ExampleObject(
                    name = "XRechnung ungültig",
                    value = """
                    {
                      "status": 422,
                      "error": "Unprocessable Entity",
                      "message": "Die XRechnung ist nicht gültig.",
                      "invoiceNumber": "RE-2026-000002",
                      "errors": [
                        "BT-49 Buyer electronic address fehlt"
                      ]
                    }
                    """
                )
            )
        )
    })
    public ResponseEntity<ArchivedInvoiceDocuments>
    archiveExistingDocuments(
        @PathVariable Long invoiceId
    ) {

        return ResponseEntity.ok(
            service.archiveExistingDocuments(
                invoiceId
            )
        );
    }
}
