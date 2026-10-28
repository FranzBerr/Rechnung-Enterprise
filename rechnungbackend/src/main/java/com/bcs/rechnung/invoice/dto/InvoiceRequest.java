package com.bcs.rechnung.invoice.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record InvoiceRequest(

    /*
     * Firma, welche die Rechnung ausstellt.
     */
    @NotNull(
        message = "Die Firma muss angegeben werden."
    )
    @Positive(
        message = "Die Firmen-ID muss größer als 0 sein."
    )
    Long companyId,

    /*
     * Rechnungsempfänger.
     */
    @NotNull(
        message = "Der Kunde muss angegeben werden."
    )
    @Positive(
        message = "Die Kunden-ID muss größer als 0 sein."
    )
    Long customerId,

    /*
     * Rechnungsdatum.
     */
    @NotNull(
        message = "Das Rechnungsdatum muss angegeben werden."
    )
    LocalDate invoiceDate,

    /*
     * Leistungsdatum.
     *
     * Das Leistungsdatum ist optional.
     */
    LocalDate serviceDate,

    /*
     * Fälligkeitsdatum.
     */
    @NotNull(
        message = "Das Fälligkeitsdatum muss angegeben werden."
    )
    LocalDate dueDate,

    /*
     * Zahlungsziel in Tagen.
     *
     * Beispiel:
     * 30
     */
    @NotNull(
        message = "Das Zahlungsziel muss angegeben werden."
    )
    @PositiveOrZero(
        message = "Das Zahlungsziel darf nicht negativ sein."
    )
    Integer paymentTermDays,

    /*
     * Skontosatz in Prozent.
     *
     * Beispiel:
     * 2.00 = 2 %
     *
     * Kein Skonto:
     * 0.00
     */
    @NotNull(
        message = "Der Skontosatz muss angegeben werden."
    )
    @DecimalMin(
        value = "0.00",
        inclusive = true,
        message = "Der Skontosatz darf nicht negativ sein."
    )
    @DecimalMax(
        value = "100.00",
        inclusive = true,
        message = "Der Skontosatz darf maximal 100 % betragen."
    )
    @Digits(
        integer = 3,
        fraction = 2,
        message = "Der Skontosatz darf maximal 3 Ganzzahl- und 2 Nachkommastellen enthalten."
    )
    BigDecimal discountPercent,

    /*
     * Skontofrist in Tagen.
     *
     * Beispiel:
     * 10
     *
     * Kein Skonto:
     * 0
     */
    @NotNull(
        message = "Die Skontofrist muss angegeben werden."
    )
    @PositiveOrZero(
        message = "Die Skontofrist darf nicht negativ sein."
    )
    Integer discountDays,

    /*
     * Käuferreferenz.
     *
     * Bei einer XRechnung kann hier beispielsweise
     * eine Leitweg- oder Auftragsreferenz stehen.
     */
    @Size(
        max = 200,
        message = "Die Käuferreferenz darf maximal 200 Zeichen enthalten."
    )
    String buyerReference,

    /*
     * Auftragsreferenz.
     */
    @Size(
        max = 200,
        message = "Die Auftragsreferenz darf maximal 200 Zeichen enthalten."
    )
    String orderReference,

    /*
     * Projektreferenz.
     */
    @Size(
        max = 200,
        message = "Die Projektreferenz darf maximal 200 Zeichen enthalten."
    )
    String projectReference,

    /*
     * Vertragsreferenz.
     */
    @Size(
        max = 200,
        message = "Die Vertragsreferenz darf maximal 200 Zeichen enthalten."
    )
    String contractReference,

    /*
     * Zahlungsreferenz beziehungsweise
     * Verwendungszweck.
     */
    @Size(
        max = 200,
        message = "Die Zahlungsreferenz darf maximal 200 Zeichen enthalten."
    )
    String paymentReference,

    @Positive(
        message = "Die Bankkonto-ID muss größer als 0 sein."
    )
    Long bankAccountId,
    /*
     * Optionale Bemerkungen zur Rechnung.
     */
    @Size(
        max = 4000,
        message = "Die Bemerkungen dürfen maximal 4000 Zeichen enthalten."
    )
    String notes,

    /*
     * Rechnungspositionen.
     *
     * @Valid sorgt dafür, dass auch die
     * Bean-Validation-Regeln in
     * InvoiceItemRequest ausgeführt werden.
     */
    @NotEmpty(
        message = "Die Rechnung muss mindestens eine Position enthalten."
    )
    @Size(
        max = 1000,
        message = "Eine Rechnung darf maximal 1000 Positionen enthalten."
    )
    @Valid
    List<InvoiceItemRequest> items

) {

    /*
     * Das Fälligkeitsdatum darf nicht
     * vor dem Rechnungsdatum liegen.
     *
     * Null-Werte werden bereits durch
     * @NotNull behandelt.
     */
    @AssertTrue(
        message = "Das Fälligkeitsdatum darf nicht vor dem Rechnungsdatum liegen."
    )
    public boolean isDueDateValid() {

        if (invoiceDate == null
            || dueDate == null) {

            return true;
        }

        return !dueDate.isBefore(
            invoiceDate
        );
    }

    /*
     * Wenn ein positiver Skontosatz angegeben
     * wurde, muss auch eine positive
     * Skontofrist vorhanden sein.
     */
    @AssertTrue(
        message = "Bei einem Skontosatz größer als 0 muss die Skontofrist größer als 0 sein."
    )
    public boolean isDiscountDaysRequired() {

        if (discountPercent == null
            || discountDays == null) {

            return true;
        }

        if (discountPercent.compareTo(
            BigDecimal.ZERO
        ) <= 0) {

            return true;
        }

        return discountDays > 0;
    }

    /*
     * Wenn kein Skonto vorgesehen ist,
     * muss auch die Skontofrist 0 sein.
     *
     * Dadurch werden widersprüchliche
     * Request-Daten vermieden.
     */
    @AssertTrue(
        message = "Wenn kein Skonto gewährt wird, muss die Skontofrist 0 sein."
    )
    public boolean isDiscountConfigurationConsistent() {

        if (discountPercent == null
            || discountDays == null) {

            return true;
        }

        if (discountPercent.compareTo(
            BigDecimal.ZERO
        ) == 0) {

            return discountDays == 0;
        }

        return true;
    }

    /*
     * Die Skontofrist darf nicht länger
     * als das reguläre Zahlungsziel sein.
     *
     * Beispiel:
     *
     * Zahlungsziel: 30 Tage
     * Skontofrist:   10 Tage
     *
     * ist gültig.
     *
     * Zahlungsziel: 10 Tage
     * Skontofrist:   30 Tage
     *
     * ist ungültig.
     */
    @AssertTrue(
        message = "Die Skontofrist darf das Zahlungsziel nicht überschreiten."
    )
    public boolean isDiscountPeriodValid() {

        if (paymentTermDays == null
            || discountDays == null) {

            return true;
        }

        if (discountDays == 0) {
            return true;
        }

        return discountDays
            <= paymentTermDays;
    }
    @AssertTrue(
        message = "Das Leistungsdatum darf nicht nach dem Rechnungsdatum liegen."
    )
    public boolean isServiceDateValid() {

        if (invoiceDate == null
            || serviceDate == null) {

            return true;
        }

        return !serviceDate.isAfter(
            invoiceDate
        );
    }
    @AssertTrue(
        message = "Das Fälligkeitsdatum muss dem Rechnungsdatum zuzüglich Zahlungsziel entsprechen."
    )
    public boolean isDueDateConsistentWithPaymentTerm() {

        if (invoiceDate == null
            || dueDate == null
            || paymentTermDays == null) {

            return true;
        }

        LocalDate expectedDueDate =
            invoiceDate.plusDays(
                paymentTermDays
            );

        return dueDate.equals(
            expectedDueDate
        );
    }

}
