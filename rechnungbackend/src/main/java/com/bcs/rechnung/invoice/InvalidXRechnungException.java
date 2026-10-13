package com.bcs.rechnung.invoice;

import java.nio.file.Path;
import java.util.List;

public class InvalidXRechnungException
    extends RuntimeException {

    private final String invoiceNumber;

    private final List<String> validationErrors;

    private final List<String> validationWarnings;

    private final Path reportFile;

    public InvalidXRechnungException(
        String invoiceNumber,
        List<String> validationErrors,
        List<String> validationWarnings,
        Path reportFile
    ) {
        super(
            createMessage(
                invoiceNumber,
                validationErrors,
                reportFile
            )
        );

        this.invoiceNumber =
            invoiceNumber;

        this.validationErrors =
            validationErrors == null
                ? List.of()
                : List.copyOf(validationErrors);

        this.validationWarnings =
            validationWarnings == null
                ? List.of()
                : List.copyOf(validationWarnings);

        this.reportFile =
            reportFile;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public List<String> getValidationErrors() {
        return validationErrors;
    }

    public List<String> getValidationWarnings() {
        return validationWarnings;
    }

    public Path getReportFile() {
        return reportFile;
    }

    private static String createMessage(
        String invoiceNumber,
        List<String> errors,
        Path reportFile
    ) {
        return "Die XRechnung "
            + invoiceNumber
            + " ist nicht KoSIT-konform. Fehler: "
            + (errors == null ? List.of() : errors)
            + ". Prüfbericht: "
            + reportFile;
    }
}
