package com.bcs.rechnung.invoice.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record InvoiceItemRequest(

    @NotNull(
        message = "Das Produkt muss angegeben werden."
    )
    @Positive(
        message = "Die Produkt-ID muss größer als 0 sein."
    )
    Long productId,

    @Size(
        max = 4000,
        message = "Die Positionsbeschreibung darf maximal 4000 Zeichen enthalten."
    )
    String description,

    @NotNull(
        message = "Die Menge muss angegeben werden."
    )
    @DecimalMin(
        value = "0.0001",
        inclusive = true,
        message = "Die Menge muss größer als 0 sein."
    )
    @Digits(
        integer = 15,
        fraction = 4,
        message = "Die Menge darf maximal 15 Ganzzahl- und 4 Nachkommastellen enthalten."
    )
    BigDecimal quantity,

    @Size(
        max = 10,
        message = "Die Mengeneinheit darf maximal 10 Zeichen enthalten."
    )
    String unit,

    @NotNull(
        message = "Der Netto-Einzelpreis muss angegeben werden."
    )
    @DecimalMin(
        value = "0.00",
        inclusive = true,
        message = "Der Netto-Einzelpreis darf nicht negativ sein."
    )
    @Digits(
        integer = 15,
        fraction = 4,
        message = "Der Netto-Einzelpreis darf maximal 15 Ganzzahl- und 4 Nachkommastellen enthalten."
    )
    BigDecimal unitPriceNet,

    @NotNull(
        message = "Der Mehrwertsteuersatz muss angegeben werden."
    )
    @DecimalMin(
        value = "0.00",
        inclusive = true,
        message = "Der Mehrwertsteuersatz darf nicht negativ sein."
    )
    @DecimalMax(
        value = "100.00",
        inclusive = true,
        message = "Der Mehrwertsteuersatz darf maximal 100 % betragen."
    )
    @Digits(
        integer = 3,
        fraction = 2,
        message = "Der Mehrwertsteuersatz darf maximal 2 Nachkommastellen enthalten."
    )
    BigDecimal vatRate,

    @DecimalMin(
        value = "0.00",
        inclusive = true,
        message = "Der Positionsrabatt darf nicht negativ sein."
    )
    @DecimalMax(
        value = "100.00",
        inclusive = true,
        message = "Der Positionsrabatt darf maximal 100 % betragen."
    )
    @Digits(
        integer = 3,
        fraction = 2,
        message = "Der Positionsrabatt darf maximal 2 Nachkommastellen enthalten."
    )
    BigDecimal discountPercent

) {
}
