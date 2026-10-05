package com.bcs.rechnung.bank_account.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
    name = "BankAccountDto",
    description = "Bankkonto"
)
public record BankAccountDto(

    @Schema(
        description = "Eindeutige ID",
        example = "1"
    )
    Long id,

    @Schema(
        description = "ID der Firma",
        example = "1"
    )
    Long companyId,

    @Schema(
        description = "ID der Bank",
        example = "2"
    )
    Long bankId,

    @Schema(
        description = "Name der Bank",
        example = "HypoVereinsbank"
    )
    String bankName,

    @Schema(
        description = "BIC der Bank",
        example = "HYVEDEMMXXX"
    )
    String bic,

    @Schema(
        description = "Kontoinhaber",
        example = "BCS GmbH"
    )
    String accountHolder,

    @Schema(
        description = "IBAN",
        example = "DE02120300000000202051"
    )
    String iban,

    @Schema(
        description = "Standardkonto"
    )
    boolean defaultAccount,

    @Schema(
        description = "Aktiv"
    )
    boolean active
) {
}
