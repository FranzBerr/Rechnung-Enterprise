package com.bcs.rechnung.invoice.xrechnung.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class XRechnungItemDto {

    @NotNull(
        message = "Die Positionsnummer muss angegeben werden."
    )
    @Positive(
        message = "Die Positionsnummer muss größer als 0 sein."
    )
    private Integer positionNumber;

    /*
     * Die Produkt-ID ist intern und für die eigentliche
     * XRechnung nicht zwingend erforderlich.
     */
    @Positive(
        message = "Die Produkt-ID muss größer als 0 sein."
    )
    private Long productId;

    @Size(
        max = 30,
        message = "Die Produktnummer darf maximal 30 Zeichen enthalten."
    )
    private String productNumber;

    /*
     * An InvoiceItemEntity.productName angeglichen.
     */
    @Size(
        max = 200,
        message = "Der Produktname darf maximal 200 Zeichen enthalten."
    )
    private String productName;

    @NotBlank(
        message = "Die Positionsbeschreibung darf nicht leer sein."
    )
    @Size(
        max = 1000,
        message = "Die Positionsbeschreibung darf maximal 1000 Zeichen enthalten."
    )
    private String description;

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
    private BigDecimal quantity;

    @NotBlank(
        message = "Die Mengeneinheit muss angegeben werden."
    )
    @Size(
        max = 20,
        message = "Die Mengeneinheit darf maximal 20 Zeichen enthalten."
    )
    private String unit;

    /*
     * Netto-Einzelpreis vor Positionsrabatt.
     */
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
    private BigDecimal unitPriceNet;

    /*
     * Positionsrabatt in Prozent.
     */
    @NotNull(
        message = "Der Positionsrabatt muss angegeben werden."
    )
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
        fraction = 4,
        message = "Der Positionsrabatt darf maximal 4 Nachkommastellen enthalten."
    )
    private BigDecimal discountPercent;

    /*
     * Umsatzsteuersatz.
     */
    @NotNull(
        message = "Der Umsatzsteuersatz muss angegeben werden."
    )
    @DecimalMin(
        value = "0.00",
        inclusive = true,
        message = "Der Umsatzsteuersatz darf nicht negativ sein."
    )
    @DecimalMax(
        value = "100.00",
        inclusive = true,
        message = "Der Umsatzsteuersatz darf maximal 100 % betragen."
    )
    @Digits(
        integer = 3,
        fraction = 4,
        message = "Der Umsatzsteuersatz darf maximal 4 Nachkommastellen enthalten."
    )
    private BigDecimal vatRate;

    @NotBlank(
        message = "Die Steuerkategorie muss angegeben werden."
    )
    @Size(
        max = 10,
        message = "Die Steuerkategorie darf maximal 10 Zeichen enthalten."
    )
    private String taxCategoryCode;

    @Size(
        max = 100,
        message = "Der Klassifikationscode darf maximal 100 Zeichen enthalten."
    )
    private String itemClassificationCode;

    /*
     * Positionsnettobetrag nach Rabatt.
     */
    @NotNull(
        message = "Der Positionsnettobetrag muss angegeben werden."
    )
    @DecimalMin(
        value = "0.00",
        inclusive = true,
        message = "Der Positionsnettobetrag darf nicht negativ sein."
    )
    @Digits(
        integer = 17,
        fraction = 2,
        message = "Der Positionsnettobetrag darf maximal 2 Nachkommastellen enthalten."
    )
    private BigDecimal lineNet;

    /*
     * Umsatzsteuerbetrag der Position.
     */
    @NotNull(
        message = "Der Umsatzsteuerbetrag muss angegeben werden."
    )
    @DecimalMin(
        value = "0.00",
        inclusive = true,
        message = "Der Umsatzsteuerbetrag darf nicht negativ sein."
    )
    @Digits(
        integer = 17,
        fraction = 2,
        message = "Der Umsatzsteuerbetrag darf maximal 2 Nachkommastellen enthalten."
    )
    private BigDecimal taxAmount;

    /*
     * Positionsbruttobetrag.
     */
    @NotNull(
        message = "Der Positionsbruttobetrag muss angegeben werden."
    )
    @DecimalMin(
        value = "0.00",
        inclusive = true,
        message = "Der Positionsbruttobetrag darf nicht negativ sein."
    )
    @Digits(
        integer = 17,
        fraction = 2,
        message = "Der Positionsbruttobetrag darf maximal 2 Nachkommastellen enthalten."
    )
    private BigDecimal lineGross;
}
