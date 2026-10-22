package com.bcs.rechnung.deliverynote.controller;

import com.bcs.rechnung.deliverynote.archive.DeliveryNoteArchiveService;
import com.bcs.rechnung.deliverynote.dto.DeliveryNoteDto;
import com.bcs.rechnung.deliverynote.dto.DeliveryNoteRequest;
import com.bcs.rechnung.deliverynote.pdf.DeliveryNotePdfService;
import com.bcs.rechnung.deliverynote.service.DeliveryNoteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/delivery-notes")
@RequiredArgsConstructor
@Tag(
    name = "Delivery Notes",
    description = "Verwaltung von Lieferscheinen"
)
public class DeliveryNoteController {

    private final DeliveryNoteService deliveryNoteService;
    private final DeliveryNotePdfService deliveryNotePdfService;
    private final DeliveryNoteArchiveService deliveryNoteArchiveService;

    @GetMapping
    @Operation(
        summary = "Alle Lieferscheine abrufen"
    )
    public ResponseEntity<List<DeliveryNoteDto>> findAll() {
        return ResponseEntity.ok(
            deliveryNoteService.findAll()
        );
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Lieferschein anhand der ID abrufen"
    )
    public ResponseEntity<DeliveryNoteDto> findById(
        @PathVariable Long id
    ) {
        return ResponseEntity.ok(
            deliveryNoteService.findById(id)
        );
    }

    @PostMapping
    @Operation(
        summary = "Neuen Lieferschein anlegen"
    )
    public ResponseEntity<DeliveryNoteDto> create(
        @Valid @RequestBody DeliveryNoteRequest request
    ) {
        val created =
            deliveryNoteService.create(request);

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(created);
    }

    @PutMapping("/{id}")
    @Operation(
        summary = "Lieferschein ändern"
    )
    public ResponseEntity<DeliveryNoteDto> update(
        @PathVariable Long id,
        @Valid @RequestBody DeliveryNoteRequest request
    ) {
        return ResponseEntity.ok(
            deliveryNoteService.update(
                id,
                request
            )
        );
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Lieferschein löschen"
    )
    public ResponseEntity<Void> delete(
        @PathVariable Long id
    ) {
        deliveryNoteService.delete(id);

        return ResponseEntity
            .noContent()
            .build();
    }

    @GetMapping(
        value = "/{id}/pdf",
        produces = MediaType.APPLICATION_PDF_VALUE
    )
    @Operation(
        summary = "Lieferschein als PDF erzeugen"
    )
    public ResponseEntity<byte[]> downloadPdf(
        @PathVariable Long id
    ) {
        val generated =
            deliveryNotePdfService
                .generateDeliveryNotePdf(id);

        val fileName =
            generated.deliveryNoteNumber()
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

    @PostMapping("/{id}/archive")
    @Operation(
        summary = "Lieferschein archivieren"
    )
    public ResponseEntity<DeliveryNoteDto> archive(
        @PathVariable Long id
    ) {
        deliveryNoteArchiveService.archive(id);

        return ResponseEntity.ok(
            deliveryNoteService.findById(id)
        );
    }

    @GetMapping(
        value = "/{id}/archive",
        produces = MediaType.APPLICATION_PDF_VALUE
    )
    @Operation(
        summary = "Archivierten Lieferschein herunterladen"
    )
    public ResponseEntity<Resource> downloadArchive(
        @PathVariable Long id
    ) throws IOException {

        val path =
            deliveryNoteArchiveService
                .getArchivedPdf(id);

        val resource =
            new FileSystemResource(path);

        return ResponseEntity
            .ok()
            .header(
                HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\""
                    + path.getFileName()
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

    @GetMapping("/{id}/archive/status")
    @Operation(
        summary = "Archivstatus des Lieferscheins abrufen"
    )
    public ResponseEntity<DeliveryNoteDto> archiveStatus(
        @PathVariable Long id
    ) {
        return ResponseEntity.ok(
            deliveryNoteService.findById(id)
        );
    }
}
