package com.bcs.rechnung.invoice.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(
    name = "InvoiceItemDto",
    description = "Rechnungsposition"
)
public record InvoiceItemDto(

    Long id,

    Integer positionNumber,

    Long productId,

    String productNumber,

    String description,

    BigDecimal quantity,

    String unit,

    BigDecimal unitPriceNet,

    BigDecimal discountPercent,

    BigDecimal vatRate,

    BigDecimal lineNet,

    BigDecimal taxAmount,

    BigDecimal lineGross
) {
}
