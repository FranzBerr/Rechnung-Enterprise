package com.bcs.rechnung.deliverynote.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Schema(
    name = "DeliveryNoteDto",
    description = "Lieferschein inklusive Lieferscheinpositionen"
)
public record DeliveryNoteDto(

    @Schema(
        description = "Eindeutige ID",
        example = "1"
    )
    Long id,

    @Schema(
        description = "Lieferscheinnummer",
        example = "LS-2026-000073"
    )
    String deliveryNoteNumber,

    @Schema(
        description = "ID des Kunden",
        example = "1"
    )
    Long customerId,

    @Schema(
        description = "Kundennummer",
        example = "K1145"
    )
    String customerNumber,

    @Schema(
        description = "Kundenname",
        example = "ProTip AG"
    )
    String customerName,

    @Schema(
        description = "Lieferdatum",
        example = "2026-10-13"
    )
    LocalDate deliveryDate,

    @Schema(
        description = "Name des Lieferempfängers",
        example = "ProTip AG"
    )
    String deliveryName,

    @Schema(
        description = "Ansprechpartner beim Lieferempfänger"
    )
    String deliveryContactPerson,

    @Schema(
        description = "Abteilung beim Lieferempfänger"
    )
    String deliveryDepartment,

    @Schema(
        description = "Straße der Lieferanschrift",
        example = "Moitzfeld 96"
    )
    String deliveryStreet,

    @Schema(
        description = "Postleitzahl der Lieferanschrift",
        example = "51429"
    )
    String deliveryZipCode,

    @Schema(
        description = "Ort der Lieferanschrift",
        example = "Bergisch Gladbach"
    )
    String deliveryCity,

    @Schema(
        description = "Land der Lieferanschrift",
        example = "DE"
    )
    String deliveryCountry,

    @Schema(
        description = "Auftragsreferenz"
    )
    String orderReference,

    @Schema(
        description = "Versandart"
    )
    String shippingMethod,

    @Schema(
        description = "Trackingnummer"
    )
    String trackingNumber,

    @Schema(
        description = "Anmerkungen zum Lieferschein"
    )
    String notes,

    @Schema(
        description = "Lieferschein ist aktiv",
        example = "true"
    )
    boolean active,

    @Schema(
        description = "Lieferschein wurde archiviert",
        example = "false"
    )
    boolean archived,

    @Schema(
        description = "Zeitpunkt der Archivierung",
        example = "2026-10-21T16:30:00"
    )
    LocalDateTime archivedAt,

    @Schema(
        description = "Pfad der archivierten PDF-Datei"
    )
    String archivePath,

    @Schema(
        description = "Datum der Auslieferung",
        example = "2026-10-21"
    )
    LocalDate deliveryDateSigned,

    @Schema(
        description = "Name der ausliefernden Person",
        example = "Max Mustermann"
    )
    String deliveryPerson,

    @Schema(
        description = "Digitale Unterschrift der ausliefernden Person"
    )
    byte[] deliverySignature,

    @Schema(
        description = "Datum des Warenempfangs",
        example = "2026-10-21"
    )
    LocalDate receiptDateSigned,

    @Schema(
        description = "Name der Person beim Warenempfang",
        example = "Erika Musterfrau"
    )
    String receiptPerson,

    @Schema(
        description = "Digitale Unterschrift beim Warenempfang"
    )
    byte[] receiptSignature,

    @Schema(
        description = "Positionen des Lieferscheins"
    )
    List<DeliveryNoteItemDto> items

) {
}
