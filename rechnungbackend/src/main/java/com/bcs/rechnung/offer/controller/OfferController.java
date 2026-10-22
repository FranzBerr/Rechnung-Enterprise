package com.bcs.rechnung.offer.controller;

import com.bcs.rechnung.offer.archive.OfferArchiveService;
import com.bcs.rechnung.offer.dto.OfferDto;
import com.bcs.rechnung.offer.dto.OfferRequest;
import com.bcs.rechnung.offer.pdf.OfferPdfService;
import com.bcs.rechnung.offer.service.OfferService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

@RestController
@RequestMapping("/api/offers")
@RequiredArgsConstructor
@Tag(
    name = "Offers",
    description = "Verwaltung, PDF-Erzeugung und Archivierung von Angeboten"
)
public class OfferController {

    private final OfferService offerService;

    private final OfferPdfService offerPdfService;

    private final OfferArchiveService offerArchiveService;

    // =========================================================
    // GET ALL
    // =========================================================

    @GetMapping
    @Operation(
        summary = "Alle Angebote abrufen",
        description = "Liefert alle vorhandenen Angebote."
    )
    @ApiResponses({

        @ApiResponse(
            responseCode = "200",
            description = "Angebote erfolgreich geladen",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                examples = @ExampleObject(
                    name = "Angebotsliste",
                    value = """
                    [
                      {
                        "id": 1,
                        "offerNumber": "AN-2026-000001",
                        "offerDate": "2026-10-21",
                        "validUntil": "2026-11-20",
                        "status": "CREATED",
                        "companyId": 1,
                        "customerId": 1,
                        "customerNumber": "K1145",
                        "customerName": "ProTip AG",
                        "customerContactPerson": null,
                        "customerDepartment": null,
                        "customerStreet": "Moitzfeld 96",
                        "customerZipCode": "51429",
                        "customerCity": "Bergisch Gladbach",
                        "customerCountry": "Deutschland",
                        "customerVatId": null,
                        "currencyCode": "EUR",
                        "paymentTermDays": 30,
                        "subtotalNet": 750.00,
                        "taxAmount": 142.50,
                        "totalGross": 892.50,
                        "introductionText": "Vielen Dank für Ihre Anfrage.",
                        "closingText": "Wir freuen uns auf Ihren Auftrag.",
                        "notes": null,
                        "active": true,
                        "archived": false,
                        "archivedAt": null,
                        "archivePath": null,
                        "items": []
                      }
                    ]
                    """
                )
            )
        )

    })
    public ResponseEntity<List<OfferDto>> findAll() {

        return ResponseEntity.ok(
            offerService.findAll()
        );
    }

    // =========================================================
    // GET BY ID
    // =========================================================

    @GetMapping("/{id}")
    @Operation(
        summary = "Angebot anhand der ID abrufen"
    )
    @ApiResponses({

        @ApiResponse(
            responseCode = "200",
            description = "Angebot erfolgreich geladen",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(
                    implementation = OfferDto.class
                ),
                examples = @ExampleObject(
                    name = "Angebot",
                    value = """
                    {
                      "id": 1,
                      "offerNumber": "AN-2026-000001",
                      "offerDate": "2026-10-21",
                      "validUntil": "2026-11-20",
                      "status": "CREATED",
                      "companyId": 1,
                      "customerId": 1,
                      "customerNumber": "K1145",
                      "customerName": "ProTip AG",
                      "customerContactPerson": null,
                      "customerDepartment": null,
                      "customerStreet": "Moitzfeld 96",
                      "customerZipCode": "51429",
                      "customerCity": "Bergisch Gladbach",
                      "customerCountry": "Deutschland",
                      "customerVatId": null,
                      "currencyCode": "EUR",
                      "paymentTermDays": 30,
                      "subtotalNet": 750.00,
                      "taxAmount": 142.50,
                      "totalGross": 892.50,
                      "introductionText": "Vielen Dank für Ihre Anfrage.",
                      "closingText": "Wir freuen uns auf Ihren Auftrag.",
                      "notes": null,
                      "active": true,
                      "archived": false,
                      "archivedAt": null,
                      "archivePath": null,
                      "items": [
                        {
                          "id": 1,
                          "positionNumber": 1,
                          "productId": 4,
                          "productNumber": "P1003",
                          "productName": "Projektleitung",
                          "description": "Projektmanagement",
                          "quantity": 5.0000,
                          "unit": "STD",
                          "unitPriceNet": 150.0000,
                          "discountPercent": 0.0000,
                          "vatRate": 19.0000,
                          "lineNet": 750.00,
                          "taxAmount": 142.50,
                          "lineGross": 892.50,
                          "notes": null
                        }
                      ]
                    }
                    """
                )
            )
        ),

        @ApiResponse(
            responseCode = "404",
            description = "Angebot wurde nicht gefunden",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                examples = @ExampleObject(
                    name = "Angebot nicht gefunden",
                    value = """
                    {
                      "status": 404,
                      "error": "Not Found",
                      "message": "Angebot mit ID 999 wurde nicht gefunden.",
                      "timestamp": "2026-10-21T20:00:00"
                    }
                    """
                )
            )
        )

    })
    public ResponseEntity<OfferDto> findById(
        @PathVariable Long id
    ) {

        return ResponseEntity.ok(
            offerService.findById(id)
        );
    }

    // =========================================================
    // CREATE
    // =========================================================

    @PostMapping
    @Operation(
        summary = "Neues Angebot erstellen",
        description =
            "Erstellt ein neues Angebot und berechnet "
                + "Positions- und Angebotssummen."
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        required = true,
        content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(
                implementation = OfferRequest.class
            ),
            examples = @ExampleObject(
                name = "Neues Angebot",
                value = """
                {
                  "offerNumber": "AN-2026-000001",
                  "offerDate": "2026-10-21",
                  "validUntil": "2026-11-20",
                  "status": "CREATED",
                  "companyId": 1,
                  "customerId": 1,
                  "customerName": null,
                  "customerContactPerson": null,
                  "customerDepartment": null,
                  "customerStreet": null,
                  "customerZipCode": null,
                  "customerCity": null,
                  "customerCountry": null,
                  "customerVatId": null,
                  "currencyCode": "EUR",
                  "paymentTermDays": 30,
                  "introductionText": "Vielen Dank für Ihre Anfrage.",
                  "closingText": "Wir freuen uns auf Ihren Auftrag.",
                  "notes": null,
                  "active": true,
                  "items": [
                    {
                      "positionNumber": 1,
                      "productId": 4,
                      "productNumber": null,
                      "productName": null,
                      "description": "Projektmanagement",
                      "quantity": 5.0000,
                      "unit": null,
                      "unitPriceNet": null,
                      "discountPercent": 0.0000,
                      "vatRate": 19.0000,
                      "notes": null
                    }
                  ]
                }
                """
            )
        )
    )
    @ApiResponses({

        @ApiResponse(
            responseCode = "201",
            description = "Angebot erfolgreich erstellt",
            content = @Content(
                schema = @Schema(
                    implementation = OfferDto.class
                )
            )
        ),

        @ApiResponse(
            responseCode = "400",
            description = "Ungültige Angebotsdaten",
            content = @Content(
                examples = @ExampleObject(
                    name = "Ungültiges Angebotsdatum",
                    value = """
                    {
                      "status": 400,
                      "error": "Bad Request",
                      "message": "Das Angebotsdatum darf nicht null sein.",
                      "timestamp": "2026-10-21T20:00:00"
                    }
                    """
                )
            )
        ),

        @ApiResponse(
            responseCode = "404",
            description =
                "Firma, Kunde oder Produkt wurde nicht gefunden",
            content = @Content(
                examples = @ExampleObject(
                    name = "Produkt nicht gefunden",
                    value = """
                    {
                      "status": 404,
                      "error": "Not Found",
                      "message": "Produkt mit ID 999 wurde nicht gefunden.",
                      "timestamp": "2026-10-21T20:00:00"
                    }
                    """
                )
            )
        )

    })
    public ResponseEntity<OfferDto> create(
        @Valid
        @RequestBody
        OfferRequest request
    ) {

        OfferDto created =
            offerService.create(
                request
            );

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(created);
    }

    // =========================================================
    // UPDATE
    // =========================================================

    @PutMapping("/{id}")
    @Operation(
        summary = "Angebot ändern",
        description =
            "Ändert ein noch nicht archiviertes Angebot "
                + "und berechnet alle Summen neu."
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        required = true,
        content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(
                implementation = OfferRequest.class
            ),
            examples = @ExampleObject(
                name = "Angebot ändern",
                value = """
                {
                  "offerNumber": "AN-2026-000001",
                  "offerDate": "2026-10-21",
                  "validUntil": "2026-11-30",
                  "status": "SENT",
                  "companyId": 1,
                  "customerId": 1,
                  "currencyCode": "EUR",
                  "paymentTermDays": 30,
                  "introductionText": "Vielen Dank für Ihre Anfrage.",
                  "closingText": "Dieses Angebot ist bis zum angegebenen Datum gültig.",
                  "notes": "Überarbeitete Version",
                  "active": true,
                  "items": [
                    {
                      "positionNumber": 1,
                      "productId": 4,
                      "description": "Projektmanagement",
                      "quantity": 8.0000,
                      "unitPriceNet": 150.0000,
                      "discountPercent": 5.0000,
                      "vatRate": 19.0000
                    }
                  ]
                }
                """
            )
        )
    )
    @ApiResponses({

        @ApiResponse(
            responseCode = "200",
            description = "Angebot erfolgreich geändert",
            content = @Content(
                schema = @Schema(
                    implementation = OfferDto.class
                )
            )
        ),

        @ApiResponse(
            responseCode = "400",
            description = "Ungültige Angebotsdaten"
        ),

        @ApiResponse(
            responseCode = "404",
            description =
                "Angebot, Firma, Kunde oder Produkt wurde nicht gefunden"
        ),

        @ApiResponse(
            responseCode = "409",
            description =
                "Archiviertes Angebot darf nicht geändert werden",
            content = @Content(
                examples = @ExampleObject(
                    name = "Archiviertes Angebot",
                    value = """
                    {
                      "status": 409,
                      "error": "Conflict",
                      "message": "Das Angebot AN-2026-000001 ist bereits archiviert und darf nicht mehr geändert oder gelöscht werden.",
                      "timestamp": "2026-10-21T20:00:00"
                    }
                    """
                )
            )
        )

    })
    public ResponseEntity<OfferDto> update(
        @PathVariable Long id,
        @Valid
        @RequestBody
        OfferRequest request
    ) {

        return ResponseEntity.ok(
            offerService.update(
                id,
                request
            )
        );
    }

    // =========================================================
    // DELETE
    // =========================================================

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Angebot löschen",
        description =
            "Löscht ein Angebot, sofern es noch nicht archiviert wurde."
    )
    @ApiResponses({

        @ApiResponse(
            responseCode = "204",
            description = "Angebot erfolgreich gelöscht"
        ),

        @ApiResponse(
            responseCode = "404",
            description = "Angebot wurde nicht gefunden"
        ),

        @ApiResponse(
            responseCode = "409",
            description =
                "Archiviertes Angebot darf nicht gelöscht werden",
            content = @Content(
                examples = @ExampleObject(
                    name = "Archiviertes Angebot",
                    value = """
                    {
                      "status": 409,
                      "error": "Conflict",
                      "message": "Das Angebot AN-2026-000001 ist bereits archiviert und darf nicht mehr geändert oder gelöscht werden.",
                      "timestamp": "2026-10-21T20:00:00"
                    }
                    """
                )
            )
        )

    })
    public ResponseEntity<Void> delete(
        @PathVariable Long id
    ) {

        offerService.delete(id);

        return ResponseEntity
            .noContent()
            .build();
    }

    // =========================================================
    // PDF
    // =========================================================

    @GetMapping(
        value = "/{id}/pdf",
        produces = MediaType.APPLICATION_PDF_VALUE
    )
    @Operation(
        summary = "Angebot als PDF herunterladen",
        description =
            "Erzeugt das PDF aus dem aktuellen Stand "
                + "des Angebots. Das Angebot wird dabei nicht archiviert."
    )
    @ApiResponses({

        @ApiResponse(
            responseCode = "200",
            description = "Angebots-PDF erfolgreich erzeugt",
            content = @Content(
                mediaType = MediaType.APPLICATION_PDF_VALUE
            )
        ),

        @ApiResponse(
            responseCode = "404",
            description = "Angebot wurde nicht gefunden"
        ),

        @ApiResponse(
            responseCode = "500",
            description = "PDF konnte nicht erzeugt werden",
            content = @Content(
                examples = @ExampleObject(
                    name = "PDF-Fehler",
                    value = """
                    {
                      "status": 500,
                      "error": "Internal Server Error",
                      "message": "Das Angebots-PDF konnte nicht erzeugt werden.",
                      "timestamp": "2026-10-21T20:00:00"
                    }
                    """
                )
            )
        )

    })
    public ResponseEntity<byte[]> downloadPdf(
        @PathVariable Long id
    ) {

        var generated =
            offerPdfService
                .generateOfferPdf(id);

        String fileName =
            generated.offerNumber()
                + ".pdf";

        return ResponseEntity
            .ok()
            .header(
                HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\""
                    + fileName
                    + "\""
            )
            .contentType(
                MediaType.APPLICATION_PDF
            )
            .contentLength(
                generated.content().length
            )
            .body(
                generated.content()
            );
    }

    // =========================================================
    // ARCHIVIEREN
    // =========================================================

    @PostMapping("/{id}/archive")
    @Operation(
        summary = "Angebot archivieren",
        description = """
            Erzeugt das Angebots-PDF und archiviert es.

            Zielstruktur:
            archive/{Jahr}/Angebote/{Angebotsnummer}/{Angebotsnummer}.pdf

            Nach erfolgreicher Archivierung werden archived,
            archivedAt und archivePath gesetzt.
            """
    )
    @ApiResponses({

        @ApiResponse(
            responseCode = "200",
            description = "Angebot erfolgreich archiviert",
            content = @Content(
                schema = @Schema(
                    implementation = OfferDto.class
                ),
                examples = @ExampleObject(
                    name = "Archiviertes Angebot",
                    value = """
                    {
                      "id": 1,
                      "offerNumber": "AN-2026-000001",
                      "offerDate": "2026-10-21",
                      "validUntil": "2026-11-20",
                      "status": "CREATED",
                      "companyId": 1,
                      "customerId": 1,
                      "customerNumber": "K1145",
                      "customerName": "ProTip AG",
                      "currencyCode": "EUR",
                      "paymentTermDays": 30,
                      "subtotalNet": 750.00,
                      "taxAmount": 142.50,
                      "totalGross": 892.50,
                      "active": true,
                      "archived": true,
                      "archivedAt": "2026-10-21T20:30:00",
                      "archivePath": "C:\\\\PolyRackDummy\\\\Rechnung-Enterprise\\\\archive\\\\2026\\\\Angebote\\\\AN-2026-000001",
                      "items": []
                    }
                    """
                )
            )
        ),

        @ApiResponse(
            responseCode = "404",
            description = "Angebot wurde nicht gefunden"
        ),

        @ApiResponse(
            responseCode = "409",
            description = "Archivierung nicht möglich"
        )

    })
    public ResponseEntity<OfferDto> archive(
        @PathVariable Long id
    ) {

        offerArchiveService.archive(id);

        return ResponseEntity.ok(
            offerService.findById(id)
        );
    }

    // =========================================================
    // ARCHIV-PDF
    // =========================================================

    @GetMapping(
        value = "/{id}/archive",
        produces = MediaType.APPLICATION_PDF_VALUE
    )
    @Operation(
        summary = "Archiviertes Angebots-PDF herunterladen",
        description =
            "Liefert die unveränderte, bereits archivierte PDF-Datei."
    )
    @ApiResponses({

        @ApiResponse(
            responseCode = "200",
            description =
                "Archiviertes Angebots-PDF erfolgreich geladen",
            content = @Content(
                mediaType = MediaType.APPLICATION_PDF_VALUE
            )
        ),

        @ApiResponse(
            responseCode = "404",
            description =
                "Angebot oder Archivdatei wurde nicht gefunden"
        ),

        @ApiResponse(
            responseCode = "409",
            description = "Angebot wurde noch nicht archiviert",
            content = @Content(
                examples = @ExampleObject(
                    name = "Nicht archiviert",
                    value = """
                    {
                      "status": 409,
                      "error": "Conflict",
                      "message": "Das Angebot AN-2026-000001 wurde noch nicht archiviert.",
                      "timestamp": "2026-10-21T20:00:00"
                    }
                    """
                )
            )
        )

    })
    public ResponseEntity<Resource> downloadArchive(
        @PathVariable Long id
    ) throws IOException {

        Path archiveFile =
            offerArchiveService
                .getArchivedPdf(id);

        FileSystemResource resource =
            new FileSystemResource(
                archiveFile
            );

        return ResponseEntity
            .ok()
            .header(
                HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\""
                    + archiveFile
                    .getFileName()
                    .toString()
                    + "\""
            )
            .contentType(
                MediaType.APPLICATION_PDF
            )
            .contentLength(
                resource.contentLength()
            )
            .body(resource);
    }

    // =========================================================
    // ARCHIVSTATUS
    // =========================================================

    @GetMapping("/{id}/archive/status")
    @Operation(
        summary = "Archivstatus des Angebots abrufen",
        description =
            "Prüft den Archivstatus und das Vorhandensein "
                + "der archivierten PDF-Datei."
    )
    @ApiResponses({

        @ApiResponse(
            responseCode = "200",
            description = "Archivstatus erfolgreich ermittelt",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                examples = {
                    @ExampleObject(
                        name = "Archiviert",
                        value = "true"
                    ),
                    @ExampleObject(
                        name = "Nicht archiviert",
                        value = "false"
                    )
                }
            )
        ),

        @ApiResponse(
            responseCode = "404",
            description = "Angebot wurde nicht gefunden"
        )

    })
    public ResponseEntity<Boolean> archiveStatus(
        @PathVariable Long id
    ) {

        return ResponseEntity.ok(
            offerArchiveService
                .isArchived(id)
        );
    }

    // =========================================================
    // ARCHIVIERUNG ZURÜCKSETZEN
    // =========================================================

    @DeleteMapping("/{id}/archive")
    @Operation(
        summary = "Archivierung des Angebots zurücksetzen",
        description = """
            Setzt den Archivstatus zurück.

            Dabei werden:
            - archived auf false gesetzt
            - archivedAt auf null gesetzt
            - archivePath auf null gesetzt

            Die physische PDF-Datei wird nicht gelöscht.
            """
    )
    @ApiResponses({

        @ApiResponse(
            responseCode = "204",
            description =
                "Archivierung erfolgreich zurückgesetzt"
        ),

        @ApiResponse(
            responseCode = "404",
            description = "Angebot wurde nicht gefunden"
        ),

        @ApiResponse(
            responseCode = "409",
            description = "Angebot ist nicht archiviert",
            content = @Content(
                examples = @ExampleObject(
                    name = "Nicht archiviert",
                    value = """
                    {
                      "status": 409,
                      "error": "Conflict",
                      "message": "Das Angebot AN-2026-000001 ist nicht archiviert.",
                      "timestamp": "2026-10-21T20:00:00"
                    }
                    """
                )
            )
        )

    })
    public ResponseEntity<Void> undoArchive(
        @PathVariable Long id
    ) {

        offerArchiveService
            .undoArchive(id);

        return ResponseEntity
            .noContent()
            .build();
    }
}
