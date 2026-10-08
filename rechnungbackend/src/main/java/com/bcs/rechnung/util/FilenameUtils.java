package com.bcs.rechnung.util;

public final class FilenameUtils {

    private FilenameUtils() {
    }

    public static String createInvoiceFileName(
        String invoiceNumber,
        String extension
    ) {

        if (invoiceNumber == null
            || invoiceNumber.isBlank()) {

            throw new IllegalArgumentException(
                "Die Rechnungsnummer fehlt."
            );
        }

        String safeInvoiceNumber =
            invoiceNumber.trim()
                .replaceAll(
                    "[\\\\/:*?\"<>|]",
                    "-"
                );

        return safeInvoiceNumber + extension;
    }

    public static String pdfFileName(
        String invoiceNumber
    ) {
        return createInvoiceFileName(
            invoiceNumber,
            ".pdf"
        );
    }

    public static String xmlFileName(
        String invoiceNumber
    ) {
        return createInvoiceFileName(
            invoiceNumber,
            ".xml"
        );
    }
}
