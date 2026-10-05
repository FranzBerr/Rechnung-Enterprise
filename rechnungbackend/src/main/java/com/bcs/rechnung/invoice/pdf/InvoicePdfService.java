package com.bcs.rechnung.invoice.pdf;

public interface InvoicePdfService {

    byte[] generateInvoicePdf(
        Long invoiceId
    );
}
