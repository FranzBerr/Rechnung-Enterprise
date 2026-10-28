package com.bcs.rechnung.invoice.pdf.dto;

public record InvoicePdfSellerDto(

    String name,

    String street,

    String zipCode,

    String city,

    String country,

    String phone,

    String email,

    String vatId,

    String taxNumber

) {
}
