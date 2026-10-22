package com.bcs.rechnung.deliverynote.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(
    name = "DeliveryNoteItemDto",
    description = "Position eines Lieferscheins"
)
public record DeliveryNoteItemDto(

    @Schema(
        description = "Eindeutige ID der Position",
        example = "1"
    )
    Long id,

    @Schema(
        description = "Positionsnummer",
        example = "1"
    )
    Integer positionNumber,

    @Schema(
        description = "ID des Produkts",
        example = "1"
    )
    Long productId,

    @Schema(
        description = "Produktnummer",
        example = "P1000"
    )
    String productNumber,

    @Schema(
        description = "Produktbezeichnung",
        example = "IT Beratung WinDev"
    )
    String name,

    @Schema(
        description = "Beschreibung",
        example = "Projektvertrag vor Ort in Belgien"
    )
    String description,

    @Schema(
        description = "Menge",
        example = "16.0000"
    )
    BigDecimal quantity,

    @Schema(
        description = "Mengeneinheit",
        example = "STD"
    )
    String unit,

    @Schema(
        description = "Anmerkung zur Position"
    )
    String notes

) {
}
