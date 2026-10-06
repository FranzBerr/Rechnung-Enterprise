package com.bcs.rechnung.invoice.xrechnung;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
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
        byte[] xml =
            invoiceXmlService.generateXml(invoiceId);

        String fileName =
            "invoice-" + invoiceId + ".xml";

        return ResponseEntity.ok()
            .header(
                HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\""
                    + fileName
                    + "\""
            )
            .contentType(MediaType.APPLICATION_XML)
            .contentLength(xml.length)
            .body(xml);
    }

    @PostMapping("/{invoiceId}/xrechnung/save")
    public ResponseEntity<String> saveXRechnung(
        @PathVariable Long invoiceId
    ) throws IOException {
        validateOutputDirectory();

        byte[] xml =
            invoiceXmlService.generateXml(invoiceId);

        Path outputDirectory =
            Path.of(properties.getOutputDirectory())
                .toAbsolutePath()
                .normalize();

        Files.createDirectories(outputDirectory);

        String fileName =
            "invoice-" + invoiceId + ".xml";

        Path outputFile =
            outputDirectory.resolve(fileName)
                .normalize();

        /*
         * Verhindert, dass der erzeugte Dateipfad außerhalb
         * des konfigurierten Verzeichnisses liegt.
         */
        if (!outputFile.startsWith(outputDirectory)) {
            throw new IllegalStateException(
                "Der ermittelte Ausgabepfad ist ungültig."
            );
        }

        Files.write(
            outputFile,
            xml,
            StandardOpenOption.CREATE,
            StandardOpenOption.TRUNCATE_EXISTING,
            StandardOpenOption.WRITE
        );

        return ResponseEntity.ok(
            "XRechnung gespeichert: "
                + outputFile
        );
    }

    private void validateOutputDirectory() {
        if (!StringUtils.hasText(
            properties.getOutputDirectory()
        )) {
            throw new IllegalStateException(
                "Die Konfiguration "
                    + "'xrechnung.output-directory' "
                    + "fehlt oder ist leer."
            );
        }
    }
}
