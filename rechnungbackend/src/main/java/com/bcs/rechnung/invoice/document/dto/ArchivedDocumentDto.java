package com.bcs.rechnung.invoice.document.dto;

import java.time.LocalDateTime;

public record ArchivedDocumentDto(

    Long id,

    Long invoiceId,

    String invoiceNumber,

    String archiveDirectory,

    String pdfFile,

    String xmlFile,

    String validationReport,

    String validatorLog,

    Boolean valid,

    LocalDateTime archivedAt

) {
}
