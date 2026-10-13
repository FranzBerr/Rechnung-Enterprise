package com.bcs.rechnung.invoice.document;

import java.util.List;

public record ArchivedInvoiceDocuments(

    String invoiceNumber,

    String archiveDirectory,

    String pdfFile,

    String xmlFile,

    String validationReport,

    boolean valid,

    List<String> warnings

) {}
