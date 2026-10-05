package com.bcs.rechnung.customer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(
    name = "CustomerRequest",
    description = "Anlage oder Änderung eines Kunden"
)
public record CustomerRequest(

    @NotNull
    @Schema(
        description = "ID der Firma",
        example = "1",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    Long companyId,

    @NotBlank
    @Size(max = 30)
    @Schema(
        description = "Kundennummer",
        example = "K100000",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    String customerNumber,

    @NotBlank
    @Size(max = 200)
    @Schema(
        description = "Name des Kunden",
        example = "Muster GmbH",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    String name,

    @Size(max = 200)
    @Schema(
        description = "Ansprechpartner",
        example = "Max Mustermann"
    )
    String contactPerson,

    @Size(max = 200)
    @Schema(
        description = "Straße",
        example = "Hauptstraße 1"
    )
    String street,

    @Size(max = 20)
    @Schema(
        description = "Postleitzahl",
        example = "97616"
    )
    String zipCode,

    @Size(max = 100)
    @Schema(
        description = "Ort",
        example = "Bad Neustadt"
    )
    String city,

    @Size(max = 100)
    @Schema(
        description = "Land",
        example = "DE"
    )
    String country,

    @Size(max = 100)
    @Schema(
        description = "Telefonnummer",
        example = "+49 9771 123456"
    )
    String phone,

    @Size(max = 200)
    @Schema(
        description = "E-Mail-Adresse",
        example = "info@muster.de"
    )
    String email,

    @Size(max = 50)
    @Schema(
        description = "Umsatzsteuer-ID",
        example = "DE123456789"
    )
    String vatId,

    @Schema(
        description = "Aktiv",
        example = "true"
    )
    boolean active
) {
}
