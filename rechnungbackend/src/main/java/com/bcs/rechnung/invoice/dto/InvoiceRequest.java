package com.bcs.rechnung.invoice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Schema(
    name = "InvoiceRequest",
    description = "Anlage einer Rechnung"
)
public record InvoiceRequest(

    @NotNull
    @Schema(
        description = "ID der eigenen Firma",
        example = "1",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    Long companyId,

    @NotNull
    @Schema(
        description = "ID des Kunden",
        example = "1",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    Long customerId,

    @NotNull
    @Schema(
        description = "ID des Bankkontos",
        example = "1",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    Long bankAccountId,

    @NotNull
    @Schema(
        description = "Rechnungsdatum",
        example = "2026-09-29",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    LocalDate invoiceDate,

    @Schema(
        description = "Leistungsdatum",
        example = "2026-09-29"
    )
    LocalDate serviceDate,

    @NotNull
    @PositiveOrZero
    @Schema(
        description = "Zahlungsziel in Tagen",
        example = "14",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    Integer paymentTermDays,

    @DecimalMin(value = "0.0000")
    @DecimalMax(value = "100.0000")
    @Schema(
        description = "Skonto in Prozent",
        example = "2.0000"
    )
    BigDecimal discountPercent,

    @PositiveOrZero
    @Schema(
        description = "Skontofrist in Tagen",
        example = "7"
    )
    Integer discountDays,

    @Size(max = 2000)
    @Schema(
        description = "Anmerkungen zur Rechnung",
        example = "Vielen Dank für Ihren Auftrag."
    )
    String notes,

    @NotEmpty
    @Valid
    @Schema(
        description = "Rechnungspositionen",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    List<InvoiceItemRequest> items
) {
}
