package com.bcs.rechnung.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(
    name = "ProductDto",
    description = "Produkt"
)
public record ProductDto(

    Long id,

    Long companyId,

    String productNumber,

    String name,

    String description,

    String unit,

    BigDecimal netPrice,

    BigDecimal vatRate,

    boolean active
) {
}
