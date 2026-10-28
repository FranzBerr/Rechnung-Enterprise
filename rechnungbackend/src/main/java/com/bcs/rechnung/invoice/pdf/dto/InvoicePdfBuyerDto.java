package com.bcs.rechnung.invoice.pdf.dto;

public record InvoicePdfBuyerDto(

    String customerNumber,

    String name,

    String street,

    String zipCode,

    String city,

    String country,

    String vatId,

    String buyerReference,

    String electronicAddress

) {
}
