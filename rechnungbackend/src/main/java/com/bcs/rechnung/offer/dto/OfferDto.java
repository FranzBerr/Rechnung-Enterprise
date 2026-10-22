package com.bcs.rechnung.offer.dto;

import com.bcs.rechnung.offer.OfferStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Schema(
    name = "OfferDto",
    description = "Angebot inklusive Angebotspositionen"
)
public record OfferDto(

    @Schema(
        description = "Eindeutige ID",
        example = "1"
    )
    Long id,

    @Schema(
        description = "Angebotsnummer",
        example = "AN-2026-000001"
    )
    String offerNumber,

    @Schema(
        description = "Angebotsdatum",
        example = "2026-10-21"
    )
    LocalDate offerDate,

    @Schema(
        description = "Gültig bis",
        example = "2026-11-20"
    )
    LocalDate validUntil,

    @Schema(
        description = "Status des Angebots",
        example = "CREATED"
    )
    OfferStatus status,

    @Schema(
        description = "ID der Firma",
        example = "1"
    )
    Long companyId,

    @Schema(
        description = "ID des Kunden",
        example = "6"
    )
    Long customerId,

    @Schema(
        description = "Kundennummer",
        example = "K1146"
    )
    String customerNumber,

    @Schema(
        description = "Kundenname",
        example = "POLYRACK TECH-GROUP"
    )
    String customerName,

    @Schema(
        description = "Ansprechpartner",
        example = "Frau Giebelhäuser"
    )
    String customerContactPerson,

    @Schema(
        description = "Abteilung"
    )
    String customerDepartment,

    @Schema(
        description = "Straße",
        example = "Heinrich-Hertz-Str. 26"
    )
    String customerStreet,

    @Schema(
        description = "Postleitzahl",
        example = "75334"
    )
    String customerZipCode,

    @Schema(
        description = "Ort",
        example = "Straubenhardt"
    )
    String customerCity,

    @Schema(
        description = "Land",
        example = "DE"
    )
    String customerCountry,

    @Schema(
        description = "Umsatzsteuer-ID"
    )
    String customerVatId,

    @Schema(
        description = "Währung",
        example = "EUR"
    )
    String currencyCode,

    @Schema(
        description = "Zahlungsziel in Tagen",
        example = "30"
    )
    Integer paymentTermDays,

    @Schema(
        description = "Nettosumme",
        example = "950.00"
    )
    BigDecimal subtotalNet,

    @Schema(
        description = "Umsatzsteuer",
        example = "180.50"
    )
    BigDecimal taxAmount,

    @Schema(
        description = "Bruttosumme",
        example = "1130.50"
    )
    BigDecimal totalGross,

    @Schema(
        description = "Einleitungstext"
    )
    String introductionText,

    @Schema(
        description = "Schlusstext"
    )
    String closingText,

    @Schema(
        description = "Interne bzw. zusätzliche Anmerkungen"
    )
    String notes,

    @Schema(
        description = "Angebot ist aktiv",
        example = "true"
    )
    boolean active,

    @Schema(
        description = "Angebot wurde archiviert",
        example = "false"
    )
    boolean archived,

    @Schema(
        description = "Zeitpunkt der Archivierung",
        example = "2026-10-21T18:30:00"
    )
    LocalDateTime archivedAt,

    @Schema(
        description = "Archivverzeichnis des Angebots"
    )
    String archivePath,

    @Schema(
        description = "Positionen des Angebots"
    )
    List<OfferItemDto> items

) {
}
