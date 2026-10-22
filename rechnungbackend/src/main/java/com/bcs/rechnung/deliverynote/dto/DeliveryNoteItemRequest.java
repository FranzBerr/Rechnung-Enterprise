package com.bcs.rechnung.deliverynote.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

@Schema(
    name = "DeliveryNoteItemRequest",
    description = "Position für Anlage oder Änderung eines Lieferscheins"
)
public record DeliveryNoteItemRequest(

    @NotNull
    @Positive
    @Schema(
        description = "Positionsnummer",
        example = "1",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    Integer positionNumber,

    @Schema(
        description = "ID des Produkts aus dem Produktstamm",
        example = "4"
    )
    Long productId,

    @Size(max = 30)
    @Schema(
        description = "Produktnummer",
        example = "P1003"
    )
    String productNumber,

    @NotBlank
    @Size(max = 200)
    @Schema(
        description = "Produktbezeichnung",
        example = "Projektleitung",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    String name,

    @Size(max = 1000)
    @Schema(
        description = "Beschreibung der Position",
        example = "Projektmanagement"
    )
    String description,

    @NotNull
    @DecimalMin("0.0001")
    @Schema(
        description = "Menge",
        example = "8.0000",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    BigDecimal quantity,

    @NotBlank
    @Size(max = 30)
    @Schema(
        description = "Mengeneinheit",
        example = "STD",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    String unit,

    @Size(max = 1000)
    @Schema(
        description = "Anmerkungen zur Position",
        example = "Projektleitung vor Ort"
    )
    String notes

) {
}
