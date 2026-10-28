package com.bcs.rechnung.invoice.pdf.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record InvoicePdfDto(

    String invoiceNumber,

    LocalDate invoiceDate,

    LocalDate serviceDate,

    LocalDate dueDate,

    String customerNumber,

    String customerName,

    String customerStreet,

    String customerZipCode,

    String customerCity,

    String customerCountry,

    String companyName,

    String companyStreet,

    String companyZipCode,

    String companyCity,

    String companyCountry,

    String companyVatId,

    String companyTaxNumber,

    String bankName,

    String iban,

    String bic,

    String paymentReference,

    String orderReference,

    String notes,

    BigDecimal subtotalNet,

    BigDecimal taxAmount,

    BigDecimal totalGross,

    List<InvoicePdfItemDto> items

) {
}
