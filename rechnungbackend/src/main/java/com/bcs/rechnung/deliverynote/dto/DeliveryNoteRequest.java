package com.bcs.rechnung.deliverynote.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;

@Schema(
    name = "DeliveryNoteRequest",
    description = "Anlage oder Änderung eines Lieferscheins"
)
public record DeliveryNoteRequest(

    @Size(max = 30)
    @Schema(
        description = "Lieferscheinnummer",
        example = "LS-2026-000073"
    )
    String deliveryNoteNumber,

    @NotNull
    @Schema(
        description = "ID des Kunden",
        example = "1",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    Long customerId,

    @NotNull
    @Schema(
        description = "Lieferdatum",
        example = "2026-10-21",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    LocalDate deliveryDate,

    @Size(max = 200)
    @Schema(
        description = "Name des Lieferempfängers",
        example = "POLYRACK TECH-GROUP"
    )
    String deliveryName,

    @Size(max = 200)
    @Schema(
        description = "Ansprechpartner beim Lieferempfänger",
        example = "Frau Giebelhäuser"
    )
    String deliveryContactPerson,

    @Size(max = 200)
    @Schema(
        description = "Abteilung beim Lieferempfänger"
    )
    String deliveryDepartment,

    @Size(max = 200)
    @Schema(
        description = "Straße der Lieferanschrift",
        example = "Heinrich-Hertz-Str. 26"
    )
    String deliveryStreet,

    @Size(max = 20)
    @Schema(
        description = "Postleitzahl der Lieferanschrift",
        example = "75334"
    )
    String deliveryZipCode,

    @Size(max = 100)
    @Schema(
        description = "Ort der Lieferanschrift",
        example = "Straubenhardt"
    )
    String deliveryCity,

    @Size(max = 100)
    @Schema(
        description = "Land der Lieferanschrift",
        example = "DE"
    )
    String deliveryCountry,

    @Size(max = 100)
    @Schema(
        description = "Auftragsreferenz",
        example = "47352-30490"
    )
    String orderReference,

    @Size(max = 100)
    @Schema(
        description = "Versandart",
        example = "Persönliche Übergabe"
    )
    String shippingMethod,

    @Size(max = 100)
    @Schema(
        description = "Trackingnummer",
        example = "TRACK-2026-00001"
    )
    String trackingNumber,

    @Size(max = 2000)
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
        description = "Bestätigtes Datum der Auslieferung",
        example = "2026-10-21"
    )
    LocalDate deliveryDateSigned,

    @Size(max = 200)
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
        description = "Bestätigtes Datum des Warenempfangs",
        example = "2026-10-21"
    )
    LocalDate receiptDateSigned,

    @Size(max = 200)
    @Schema(
        description = "Name der Person beim Warenempfang",
        example = "Frau Giebelhäuser"
    )
    String receiptPerson,

    @Schema(
        description = "Digitale Unterschrift beim Warenempfang"
    )
    byte[] receiptSignature,

    @Valid
    @Schema(
        description = "Positionen des Lieferscheins"
    )
    List<DeliveryNoteItemRequest> items

) {
}
