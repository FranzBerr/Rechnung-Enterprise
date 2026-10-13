package com.bcs.rechnung.invoice.document;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Getter
@Setter
@Component
@ConfigurationProperties(
    prefix = "invoice-documents"
)
public class InvoiceDocumentProperties {

    /**
     * Wurzelverzeichnis des Rechnungsarchivs.
     */
    private String archiveDirectory;

    /**
     * Temporäres Arbeitsverzeichnis.
     */
    private String tempDirectory;

    /**
     * Vollständiger Pfad zur KoSIT-Standalone-JAR.
     */
    private String validatorJar;

    /**
     * Vollständiger Pfad zur scenarios.xml.
     */
    private String validatorScenario;

    /**
     * Verzeichnis der entpackten
     * XRechnung-Validator-Konfiguration.
     */
    private String validatorRepository;

    /**
     * Maximale Laufzeit des KoSIT-Validators.
     */
    private long validatorTimeoutSeconds = 60;

    @PostConstruct
    public void validate() {

        requireText(
            archiveDirectory,
            "invoice-documents.archive-directory"
        );

        requireText(
            tempDirectory,
            "invoice-documents.temp-directory"
        );

        requireText(
            validatorJar,
            "invoice-documents.validator-jar"
        );

        requireText(
            validatorScenario,
            "invoice-documents.validator-scenario"
        );

        requireText(
            validatorRepository,
            "invoice-documents.validator-repository"
        );

        if (validatorTimeoutSeconds <= 0) {
            throw new IllegalStateException(
                "Die Konfiguration "
                    + "'invoice-documents.validator-timeout-seconds' "
                    + "muss größer als 0 sein."
            );
        }
    }

    private void requireText(
        String value,
        String propertyName
    ) {
        if (!StringUtils.hasText(value)) {
            throw new IllegalStateException(
                "Die Konfiguration '"
                    + propertyName
                    + "' fehlt oder ist leer."
            );
        }
    }
}
