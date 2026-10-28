package com.bcs.rechnung.invoice.pdf.dto;

import java.math.BigDecimal;

public record InvoicePdfTotalsDto(

    BigDecimal subtotalNet,

    BigDecimal taxRate,

    BigDecimal taxAmount,

    BigDecimal totalGross

) {
}
