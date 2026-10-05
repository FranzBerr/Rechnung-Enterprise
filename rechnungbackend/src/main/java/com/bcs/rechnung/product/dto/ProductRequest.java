package com.bcs.rechnung.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

@Schema(
    name = "ProductRequest",
    description = "Anlage oder Änderung eines Produkts"
)
public record ProductRequest(

    @NotNull
    Long companyId,

    @NotBlank
    @Size(max = 30)
    String productNumber,

    @NotBlank
    @Size(max = 200)
    String name,

    @Size(max = 1000)
    String description,

    @NotBlank
    @Size(max = 30)
    String unit,

    @NotNull
    @DecimalMin("0.00")
    BigDecimal netPrice,

    boolean active
) {
}
