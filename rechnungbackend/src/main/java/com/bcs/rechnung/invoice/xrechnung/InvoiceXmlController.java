package com.bcs.rechnung.invoice.xrechnung;

import com.bcs.rechnung.util.FilenameUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
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
public class InvoiceXmlController {

    private final InvoiceXmlService invoiceXmlService;
    private final XRechnungProperties properties;

    @GetMapping(
        value = "/{invoiceId}/xrechnung",
        produces = MediaType.APPLICATION_XML_VALUE
    )
    public ResponseEntity<byte[]> downloadXRechnung(
        @PathVariable Long invoiceId
    ) {
        GeneratedXRechnung generated =
            invoiceXmlService.generateXml(invoiceId);


//        String invoiceNumber =
//            invoiceXmlService.getInvoiceNumber(invoiceId);

//        String fileName =
//            createSafeFileName(invoiceNumber);
        String fileName =
            FilenameUtils.xmlFileName(
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
            .contentType(MediaType.APPLICATION_XML)
            .contentLength(
                generated.content().length
            )
            .body(
                generated.content()
            );
    }

    @PostMapping("/{invoiceId}/xrechnung/save")
    public ResponseEntity<String> saveXRechnung(
        @PathVariable Long invoiceId
    ) throws IOException {
        validateOutputDirectory();

        GeneratedXRechnung generated =
            invoiceXmlService.generateXml(invoiceId);

//        String invoiceNumber =
//            invoiceXmlService.getInvoiceNumber(invoiceId);

        String fileName =
            FilenameUtils.xmlFileName(
                generated.invoiceNumber()
            );

        Path outputDirectory =
            Path.of(properties.getOutputDirectory())
                .toAbsolutePath()
                .normalize();

        Files.createDirectories(outputDirectory);

        Path outputFile =
            outputDirectory.resolve(fileName)
                .normalize();

        /*
         * Verhindert, dass der erzeugte Dateipfad außerhalb
         * des konfigurierten Ausgabeverzeichnisses liegt.
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
            "XRechnung gespeichert: " + outputFile
        );
    }

    private String createSafeFileName(
        String invoiceNumber
    ) {
        if (invoiceNumber == null
            || invoiceNumber.isBlank()) {

            throw new IllegalStateException(
                "Die Rechnungsnummer fehlt."
            );
        }

        String safeInvoiceNumber =
            invoiceNumber.trim()
                .replaceAll(
                    "[\\\\/:*?\"<>|]",
                    "-"
                );

        return safeInvoiceNumber + ".xml";
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
