package com.bcs.rechnung.customer.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
    name = "CustomerDto",
    description = "Kunde einschließlich optionaler XRechnungsdaten"
)
public record CustomerDto(

    @Schema(
        description = "Eindeutige ID",
        example = "1"
    )
    Long id,

    @Schema(
        description = "ID der zugeordneten Firma",
        example = "1"
    )
    Long companyId,

    @Schema(
        description = "Kundennummer",
        example = "K100000"
    )
    String customerNumber,

    @Schema(
        description = "Name des Kunden",
        example = "Muster GmbH"
    )
    String name,

    @Schema(
        description = "Ansprechpartner",
        example = "Max Mustermann"
    )
    String contactPerson,

    @Schema(
        description = "Straße",
        example = "Hauptstraße 1"
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
        example = "+49 9771 123456"
    )
    String phone,

    @Schema(
        description = "E-Mail-Adresse",
        example = "info@muster.de"
    )
    String email,

    @Schema(
        description = "Umsatzsteuer-ID",
        example = "DE123456789"
    )
    String vatId,

    @Schema(
        description = "Aktiv",
        example = "true"
    )
    boolean active,

    /*
     * ---------------------------------------------------------
     * XRechnung / elektronische Rechnungsstellung
     * ---------------------------------------------------------
     */

    @Schema(
        description = "Leitweg-ID des Rechnungsempfängers",
        example = "09184-12345-67"
    )
    String leitwegId,

    @Schema(
        description = "Elektronische Adresse des Rechnungsempfängers",
        example = "09184-12345-67"
    )
    String electronicAddress,

    @Schema(
        description = "Schema der elektronischen Adresse",
        example = "0204"
    )
    String electronicAddressScheme,

    @Schema(
        description = "Geschäftsprozess-ID für die elektronische Rechnung",
        example = "urn:fdc:peppol.eu:2017:poacc:billing:01:1.0"
    )
    String businessProcessId,

    @Schema(
        description = "Käuferreferenz für die elektronische Rechnung",
        example = "09184-12345-67"
    )
    String buyerReference,

    @Schema(
        description = "Abteilung beziehungsweise Organisationseinheit des Kunden",
        example = "Zentrale Rechnungsstelle"
    )
    String department

) {
}
