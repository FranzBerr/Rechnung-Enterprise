package com.bcs.rechnung.bank_account.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(
    name = "BankAccountRequest",
    description = "Anlage oder Änderung eines Bankkontos"
)
public record BankAccountRequest(

    @NotNull
    @Schema(
        description = "ID der Firma",
        example = "1",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    Long companyId,

    @NotNull
    @Schema(
        description = "ID der Bank",
        example = "2",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    Long bankId,

    @NotNull
    @Size(max = 200)
    @Schema(
        description = "Kontoinhaber",
        example = "BCS GmbH",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    String accountHolder,

    @NotNull
    @Size(max = 34)
    @Schema(
        description = "IBAN",
        example = "DE02120300000000202051",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    String iban,

    @Schema(
        description = "Kennzeichen für Standardkonto",
        example = "true"
    )
    boolean defaultAccount,

    @Schema(
        description = "Aktiv",
        example = "true"
    )
    boolean active
) {
}
