package com.bcs.rechnung.offer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

@Schema(
    name = "OfferItemRequest",
    description = "Position für Anlage oder Änderung eines Angebots"
)
public record OfferItemRequest(

    @NotNull
    @Positive
    @Schema(
        description = "Positionsnummer",
        example = "1",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    Integer positionNumber,

    @Schema(
        description = "ID des Produkts",
        example = "4"
    )
    Long productId,

    @Size(max = 30)
    @Schema(
        description = "Produktnummer",
        example = "P1003"
    )
    String productNumber,

    @Size(max = 200)
    @Schema(
        description = "Produktbezeichnung",
        example = "Projektleitung"
    )
    String productName,

    @Size(max = 1000)
    @Schema(
        description = "Beschreibung",
        example = "Projektmanagement"
    )
    String description,

    @NotNull
    @DecimalMin("0.0001")
    @Schema(
        description = "Menge",
        example = "5.0000",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    BigDecimal quantity,

    @Size(max = 30)
    @Schema(
        description = "Mengeneinheit",
        example = "STD"
    )
    String unit,

    @Schema(
        description = "Netto-Einzelpreis. Bei einer Produktposition kann bei null der Produktpreis übernommen werden.",
        example = "150.0000"
    )
    BigDecimal unitPriceNet,

    @DecimalMin("0.0000")
    @Schema(
        description = "Rabatt in Prozent",
        example = "0.0000"
    )
    BigDecimal discountPercent,

    @NotNull
    @DecimalMin("0.0000")
    @Schema(
        description = "Umsatzsteuersatz",
        example = "19.0000",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    BigDecimal vatRate,

    @Schema(
        description = "Optionale Position. Der Betrag wird nicht in die Angebotssumme eingerechnet.",
        example = "false"
    )
    boolean optional,

    @Size(max = 1000)
    @Schema(
        description = "Anmerkungen zur Position",
        example = "Optional bei Beauftragung"
    )
    String notes

) {
}
