package com.bcs.rechnung.bank.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(
    name = "BankRequest",
    description = "Anlage oder Änderung einer Bank"
)
public record BankRequest(

    @Size(max = 20)
    @Schema(
        description = "Bankleitzahl",
        example = "70020270"
    )
    String bankCode,

    @NotBlank
    @Size(max = 200)
    @Schema(
        description = "Name der Bank",
        example = "HypoVereinsbank",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    String bankName,

    @NotBlank
    @Size(max = 11)
    @Schema(
        description = "Business Identifier Code",
        example = "HYVEDEMMXXX",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    String bic,

    @Size(max = 10)
    @Schema(
        description = "Ländercode",
        example = "DE"
    )
    String country,

    @Schema(
        description = "Aktiv",
        example = "true"
    )
    boolean active
) {
}
