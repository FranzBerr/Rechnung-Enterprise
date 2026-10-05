package com.bcs.rechnung.invoice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

@Schema(
    name = "InvoiceItemRequest",
    description = "Anlage einer Rechnungsposition"
)
public record InvoiceItemRequest(

    @NotNull
    @Schema(
        description = "ID des Produkts",
        example = "1",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    Long productId,

    @Size(max = 1000)
    @Schema(
        description = "Individuelle Positionsbeschreibung",
        example = "Softwareentwicklung",
        nullable = true
    )
    String description,

    @NotNull
    @DecimalMin(value = "0.0001")
    @Schema(
        description = "Menge",
        example = "2.0000",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    BigDecimal quantity,

    @DecimalMin(value = "0.0000")
    @Schema(
        description = "Abweichender Nettopreis",
        example = "120.0000",
        nullable = true
    )
    BigDecimal unitPriceNet,

    @DecimalMin(value = "0.0000")
    @DecimalMax(value = "100.0000")
    @Schema(
        description = "Positionsrabatt in Prozent",
        example = "0.0000"
    )
    BigDecimal discountPercent,

    @NotNull
    @DecimalMin(value = "0.0000")
    @DecimalMax(value = "100.0000")
    @Schema(
        description = "Umsatzsteuersatz",
        example = "19.0000",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    BigDecimal vatRate
) {
}
