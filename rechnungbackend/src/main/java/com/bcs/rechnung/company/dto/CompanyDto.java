package com.bcs.rechnung.company.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
    name = "CompanyDto",
    description = "Firma"
)
public record CompanyDto(

    @Schema(
        description = "Eindeutige ID",
        example = "1"
    )
    Long id,

    @Schema(
        description = "Firmenname",
        example = "BCS GmbH"
    )
    String name,

    @Schema(
        description = "Rechtsform",
        example = "GmbH"
    )
    String legalForm,

    @Schema(
        description = "Steuernummer",
        example = "123/456/789"
    )
    String taxNumber,

    @Schema(
        description = "Umsatzsteuer-ID",
        example = "DE123456789"
    )
    String vatId,

    @Schema(
        description = "Straße",
        example = "Musterstraße 1"
    )
    String street,

    @Schema(
        description = "Postleitzahl",
        example = "97616"
    )
    String zipCode,

    @Schema(
        description = "Ort",
        example = "Bad Neustadt"
    )
    String city,

    @Schema(
        description = "Land",
        example = "DE"
    )
    String country,

    @Schema(
        description = "Telefonnummer",
        example = "+49 9771 12345"
    )
    String phone,

    @Schema(
        description = "E-Mail-Adresse",
        example = "info@bcs.de"
    )
    String email,

    @Schema(
        description = "Webseite",
        example = "https://www.bcs.de"
    )
    String website,

    @Schema(
        description = "Aktiv"
    )
    boolean active
) {
}
