package com.bcs.rechnung.invoice.document;

import lombok.Getter;

import java.nio.file.Path;
import java.util.List;

@Getter
public class InvalidXRechnungException
    extends RuntimeException {

    private final String invoiceNumber;

    private final List<String> errors;

    private final List<String> warnings;

    private final Path reportFile;

    public InvalidXRechnungException(
        String invoiceNumber,
        List<String> errors,
        List<String> warnings,
        Path reportFile
    ) {

        super(
            "Die XRechnung "
                + invoiceNumber
                + " ist nicht gültig."
        );

        this.invoiceNumber =
            invoiceNumber;

        this.errors =
            errors;

        this.warnings =
            warnings;

        this.reportFile =
            reportFile;
    }
}
