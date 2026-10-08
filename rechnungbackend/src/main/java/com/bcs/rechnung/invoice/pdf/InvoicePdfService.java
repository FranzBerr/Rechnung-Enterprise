package com.bcs.rechnung.invoice.pdf;

public interface InvoicePdfService {

    GeneratedInvoicePdf generateInvoicePdf(
        Long invoiceId
    );
}
