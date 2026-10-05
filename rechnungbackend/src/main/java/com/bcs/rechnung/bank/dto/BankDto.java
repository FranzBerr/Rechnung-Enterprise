package com.bcs.rechnung.bank.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
    name = "BankDto",
    description = "Bank"
)
public record BankDto(

    @Schema(
        description = "Eindeutige ID",
        example = "1"
    )
    Long id,

    @Schema(
        description = "Bankleitzahl",
        example = "70020270"
    )
    String bankCode,

    @Schema(
        description = "Bankname",
        example = "HypoVereinsbank"
    )
    String bankName,

    @Schema(
        description = "BIC",
        example = "HYVEDEMMXXX"
    )
    String bic,

    @Schema(
        description = "Ländercode",
        example = "DE"
    )
    String country,

    @Schema(
        description = "Aktiv"
    )
    boolean active
) {
}
