package com.bcs.rechnung.invoice.document;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class InvoiceAlreadyArchivedException
    extends RuntimeException {

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
        DateTimeFormatter.ofPattern(
            "dd.MM.yyyy HH:mm:ss"
        );

    private final Long invoiceId;

    private final String invoiceNumber;

    private final LocalDateTime archivedAt;

    public InvoiceAlreadyArchivedException(
        Long invoiceId,
        String invoiceNumber,
        LocalDateTime archivedAt
    ) {
        super(
            createMessage(
                invoiceNumber,
                archivedAt
            )
        );

        this.invoiceId =
            invoiceId;

        this.invoiceNumber =
            invoiceNumber;

        this.archivedAt =
            archivedAt;
    }

    public Long getInvoiceId() {
        return invoiceId;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public LocalDateTime getArchivedAt() {
        return archivedAt;
    }

    private static String createMessage(
        String invoiceNumber,
        LocalDateTime archivedAt
    ) {
        if (archivedAt == null) {
            return "Die Rechnung "
                + invoiceNumber
                + " wurde bereits archiviert.";
        }

        return "Die Rechnung "
            + invoiceNumber
            + " wurde bereits am "
            + archivedAt.format(
            DATE_TIME_FORMATTER
        )
            + " archiviert.";
    }
}
