package com.bcs.rechnung.offer.dto;

import com.bcs.rechnung.offer.OfferStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;

@Schema(
    name = "OfferRequest",
    description = "Anlage oder Änderung eines Angebots"
)
public record OfferRequest(

    @Size(max = 30)
    @Schema(
        description = "Angebotsnummer",
        example = "AN-2026-000001"
    )
    String offerNumber,

    @NotNull
    @Schema(
        description = "Angebotsdatum",
        example = "2026-10-21",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    LocalDate offerDate,

    @Schema(
        description = "Gültigkeitsdatum des Angebots",
        example = "2026-11-20"
    )
    LocalDate validUntil,

    @Schema(
        description = "Status des Angebots",
        example = "CREATED"
    )
    OfferStatus status,

    @NotNull
    @Schema(
        description = "ID der Firma",
        example = "1",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    Long companyId,

    @NotNull
    @Schema(
        description = "ID des Kunden",
        example = "6",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    Long customerId,

    @Size(max = 200)
    String customerName,

    @Size(max = 200)
    String customerContactPerson,

    @Size(max = 200)
    String customerDepartment,

    @Size(max = 200)
    String customerStreet,

    @Size(max = 20)
    String customerZipCode,

    @Size(max = 100)
    String customerCity,

    @Size(max = 100)
    String customerCountry,

    @Size(max = 50)
    String customerVatId,

    @Size(max = 3)
    @Schema(
        description = "Währung",
        example = "EUR"
    )
    String currencyCode,

    @PositiveOrZero
    @Schema(
        description = "Zahlungsziel in Tagen",
        example = "30"
    )
    Integer paymentTermDays,

    @Size(max = 2000)
    String introductionText,

    @Size(max = 2000)
    String closingText,

    @Size(max = 2000)
    String notes,

    @Schema(
        description = "Angebot ist aktiv",
        example = "true"
    )
    boolean active,

    @Valid
    @Schema(
        description = "Angebotspositionen"
    )
    List<OfferItemRequest> items

) {
}
