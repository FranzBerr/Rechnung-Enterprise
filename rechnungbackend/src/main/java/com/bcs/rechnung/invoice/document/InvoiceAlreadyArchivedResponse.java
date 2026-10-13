package com.bcs.rechnung.invoice.document;

import java.time.LocalDateTime;

public record InvoiceAlreadyArchivedResponse(

    int status,

    String error,

    String message,

    Long invoiceId,

    String invoiceNumber,

    LocalDateTime archivedAt,

    LocalDateTime timestamp

) {
}
