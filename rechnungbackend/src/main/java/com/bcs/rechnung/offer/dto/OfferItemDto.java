package com.bcs.rechnung.offer.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(
    name = "OfferItemDto",
    description = "Position eines Angebots"
)
public record OfferItemDto(

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
        example = "4"
    )
    Long productId,

    @Schema(
        description = "Produktnummer",
        example = "P1003"
    )
    String productNumber,

    @Schema(
        description = "Produktbezeichnung",
        example = "Projektleitung"
    )
    String productName,

    @Schema(
        description = "Beschreibung",
        example = "Projektmanagement"
    )
    String description,

    @Schema(
        description = "Menge",
        example = "5.0000"
    )
    BigDecimal quantity,

    @Schema(
        description = "Einheit",
        example = "STD"
    )
    String unit,

    @Schema(
        description = "Netto-Einzelpreis",
        example = "150.0000"
    )
    BigDecimal unitPriceNet,

    @Schema(
        description = "Rabatt in Prozent",
        example = "0.0000"
    )
    BigDecimal discountPercent,

    @Schema(
        description = "Umsatzsteuersatz",
        example = "19.0000"
    )
    BigDecimal vatRate,

    @Schema(
        description = "Netto-Positionssumme",
        example = "750.00"
    )
    BigDecimal lineNet,

    @Schema(
        description = "Steuerbetrag",
        example = "142.50"
    )
    BigDecimal taxAmount,

    @Schema(
        description = "Brutto-Positionssumme",
        example = "892.50"
    )
    BigDecimal lineGross,

    @Schema(
        description = "Optionale Position. Der Betrag wird nicht in die Gesamtsumme des Angebots eingerechnet.",
        example = "false"
    )
    boolean optional,

    @Schema(
        description = "Anmerkungen zur Position",
        example = "Optional bei Beauftragung"
    )
    String notes

) {
}
