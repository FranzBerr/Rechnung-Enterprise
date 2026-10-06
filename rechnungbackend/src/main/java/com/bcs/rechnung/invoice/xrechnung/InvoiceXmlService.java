package com.bcs.rechnung.invoice.xrechnung;

public interface InvoiceXmlService {

    byte[] generateXml(
        Long invoiceId
    );
}
