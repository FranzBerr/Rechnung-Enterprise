package com.bcs.rechnung.invoice.pdf.dto;

import java.math.BigDecimal;

public record InvoicePdfItemDto(

    Integer positionNumber,

    String productNumber,

    String description,

    BigDecimal quantity,

    String unit,

    BigDecimal unitPriceNet,

    BigDecimal vatRate,

    BigDecimal lineNet,

    BigDecimal taxAmount,

    BigDecimal lineGross

) {
}
