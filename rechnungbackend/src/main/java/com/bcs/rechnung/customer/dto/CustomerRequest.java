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
    boolean active,

    /*
     * ---------------------------------------------------------
     * XRechnung / elektronische Rechnungsstellung
     * ---------------------------------------------------------
     */

    @Size(max = 100)
    @Schema(
        description = "Leitweg-ID des Rechnungsempfängers",
        example = "09184-12345-67"
    )
    String leitwegId,

    @Size(max = 200)
    @Schema(
        description = "Elektronische Adresse des Rechnungsempfängers",
        example = "09184-12345-67"
    )
    String electronicAddress,

    @Size(max = 20)
    @Schema(
        description = "Schema der elektronischen Adresse",
        example = "0204"
    )
    String electronicAddressScheme,

    @Size(max = 250)
    @Schema(
        description = "Geschäftsprozess-ID für die elektronische Rechnung",
        example = "urn:fdc:peppol.eu:2017:poacc:billing:01:1.0"
    )
    String businessProcessId,

    @Size(max = 200)
    @Schema(
        description = "Käuferreferenz für die elektronische Rechnung",
        example = "09184-12345-67"
    )
    String buyerReference,

    @Size(max = 200)
    @Schema(
        description = "Abteilung beziehungsweise Organisationseinheit des Kunden",
        example = "Zentrale Rechnungsstelle"
    )
    String department

) {
}
