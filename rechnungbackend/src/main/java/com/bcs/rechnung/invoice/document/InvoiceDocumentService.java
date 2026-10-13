package com.bcs.rechnung.invoice.document;

public interface InvoiceDocumentService {

    ArchivedInvoiceDocuments generateValidateAndArchive(Long invoiceId);

    ArchivedInvoiceDocuments archiveExistingDocuments(Long invoiceId);
}
