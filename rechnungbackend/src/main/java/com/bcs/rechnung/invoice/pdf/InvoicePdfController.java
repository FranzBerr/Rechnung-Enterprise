package com.bcs.rechnung.invoice.pdf;

import com.bcs.rechnung.invoice.xrechnung.XRechnungProperties;
import com.bcs.rechnung.util.FilenameUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
@Tag(
    name = "PDF",
    description = "PDF-Erzeugung"
)
public class InvoicePdfController {

    private final InvoicePdfService service;
    private final PdfProperties properties;

    @GetMapping(
        value = "/{id}/pdf",
        produces = MediaType.APPLICATION_PDF_VALUE
    )
    @Operation(
        summary = "Rechnung als PDF herunterladen"
    )
    public ResponseEntity<byte[]> createPdf(
        @PathVariable Long id
    ) {

        GeneratedInvoicePdf generated =
            service.generateInvoicePdf(id);

        String fileName =
            FilenameUtils.pdfFileName(
                generated.invoiceNumber()
            );

        ContentDisposition contentDisposition =
            ContentDisposition.attachment()
                .filename(
                    fileName,
                    StandardCharsets.UTF_8
                )
                .build();

        return ResponseEntity
            .ok()
            .header(
                HttpHeaders.CONTENT_DISPOSITION,
                contentDisposition.toString()
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

    @PostMapping("/{id}/pdf/save")
    @Operation(
        summary = "Rechnung als PDF speichern"
    )
    public ResponseEntity<String> savePdf(
        @PathVariable Long id
    ) throws IOException {

        validateOutputDirectory();

        GeneratedInvoicePdf generated =
            service.generateInvoicePdf(id);

        String fileName =
            FilenameUtils.pdfFileName(
                generated.invoiceNumber()
            );

        Path outputDirectory =
            Path.of(properties.getOutputDirectory())
                .toAbsolutePath()
                .normalize();

        Files.createDirectories(
            outputDirectory
        );

        Path outputFile =
            outputDirectory.resolve(fileName)
                .normalize();

        /*
         * Schutz gegen Path Traversal.
         */
        if (!outputFile.startsWith(outputDirectory)) {
            throw new IllegalStateException(
                "Der ermittelte Ausgabepfad ist ungültig."
            );
        }

        Files.write(
            outputFile,
            generated.content(),
            StandardOpenOption.CREATE,
            StandardOpenOption.TRUNCATE_EXISTING,
            StandardOpenOption.WRITE
        );

        return ResponseEntity.ok(
            "PDF gespeichert: "
                + outputFile
        );
    }

    private void validateOutputDirectory() {

        String outputDirectory =
            properties.getOutputDirectory();

        if (outputDirectory == null
            || outputDirectory.isBlank()) {

            throw new IllegalStateException(
                "Die Konfiguration "
                    + "'xrechnung.output-directory' "
                    + "fehlt oder ist leer."
            );
        }
    }
}
