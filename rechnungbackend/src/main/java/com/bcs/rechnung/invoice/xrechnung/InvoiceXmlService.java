package com.bcs.rechnung.invoice.xrechnung;

public interface InvoiceXmlService {

    GeneratedXRechnung generateXml(
        Long invoiceId
    );
//    String getInvoiceNumber(Long invoiceId);
}
