package com.bcs.rechnung.invoice.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.math.BigDecimal;
import java.time.LocalDate;

public class InvoicePaymentTermsValidator
    implements ConstraintValidator<
    ValidInvoicePaymentTerms,
    InvoicePaymentTermsData
    > {

    private static final BigDecimal ZERO =
        BigDecimal.ZERO;

    private static final BigDecimal MAX_DISCOUNT_PERCENT =
        BigDecimal.valueOf(
            100
        );

    @Override
    public boolean isValid(
        InvoicePaymentTermsData invoice,
        ConstraintValidatorContext context
    ) {
        /*
         * null wird nicht von dieser Cross-Field-Validation
         * geprüft. Dafür ist @NotNull am DTO oder Controller
         * zuständig.
         */
        if (invoice == null) {
            return true;
        }

        ValidationResult result =
            new ValidationResult(
                context
            );

        validateDueDate(
            invoice,
            result
        );

        validatePaymentTermDays(
            invoice,
            result
        );

        validateDiscount(
            invoice,
            result
        );

        return result.isValid();
    }

    private void validateDueDate(
        InvoicePaymentTermsData invoice,
        ValidationResult result
    ) {
        LocalDate invoiceDate =
            invoice.getInvoiceDate();

        LocalDate dueDate =
            invoice.getDueDate();

        if (invoiceDate == null
            || dueDate == null) {

            return;
        }

        /*
         * Das Fälligkeitsdatum darf nicht vor
         * dem Rechnungsdatum liegen.
         */
        if (dueDate.isBefore(
            invoiceDate
        )) {
            result.addViolation(
                "dueDate",
                "Das Fälligkeitsdatum darf nicht "
                    + "vor dem Rechnungsdatum liegen."
            );
        }
    }

    private void validatePaymentTermDays(
        InvoicePaymentTermsData invoice,
        ValidationResult result
    ) {
        Integer paymentTermDays =
            invoice.getPaymentTermDays();

        if (paymentTermDays != null
            && paymentTermDays < 0) {

            result.addViolation(
                "paymentTermDays",
                "Das Zahlungsziel darf nicht negativ sein."
            );
        }

        LocalDate invoiceDate =
            invoice.getInvoiceDate();

        LocalDate dueDate =
            invoice.getDueDate();

        if (invoiceDate == null
            || dueDate == null
            || paymentTermDays == null
            || paymentTermDays < 0) {

            return;
        }

        LocalDate expectedDueDate =
            invoiceDate.plusDays(
                paymentTermDays
            );

        /*
         * Sind Rechnungsdatum, Zahlungsziel und
         * Fälligkeitsdatum vorhanden, müssen diese
         * Werte zueinander passen.
         */
        if (!dueDate.equals(
            expectedDueDate
        )) {
            String message =
                "Das Fälligkeitsdatum muss bei einem "
                    + "Zahlungsziel von "
                    + paymentTermDays
                    + " Tagen dem Datum "
                    + expectedDueDate
                    + " entsprechen.";

            result.addViolation(
                "dueDate",
                message
            );

            result.addViolation(
                "paymentTermDays",
                message
            );
        }
    }

    private void validateDiscount(
        InvoicePaymentTermsData invoice,
        ValidationResult result
    ) {
        Integer discountDays =
            invoice.getDiscountDays();

        BigDecimal discountPercent =
            invoice.getDiscountPercent();

        validateDiscountDays(
            discountDays,
            result
        );

        validateDiscountPercent(
            discountPercent,
            result
        );

        boolean hasDiscountDays =
            discountDays != null
                && discountDays > 0;

        boolean hasDiscountPercent =
            discountPercent != null
                && discountPercent.compareTo(
                ZERO
            ) > 0;

        /*
         * Skontofrist und Skontosatz müssen immer
         * gemeinsam vorhanden sein.
         */
        if (hasDiscountDays
            && !hasDiscountPercent) {

            result.addViolation(
                "discountPercent",
                "Wenn eine Skontofrist angegeben ist, "
                    + "muss auch ein Skontosatz größer "
                    + "als 0 angegeben werden."
            );
        }

        if (hasDiscountPercent
            && !hasDiscountDays) {

            result.addViolation(
                "discountDays",
                "Wenn ein Skontosatz angegeben ist, "
                    + "muss auch eine Skontofrist größer "
                    + "als 0 angegeben werden."
            );
        }

        validateDiscountPeriod(
            invoice,
            hasDiscountDays,
            result
        );

        validateDiscountDueDate(
            invoice,
            hasDiscountDays,
            result
        );
    }

    private void validateDiscountDays(
        Integer discountDays,
        ValidationResult result
    ) {
        if (discountDays != null
            && discountDays < 0) {

            result.addViolation(
                "discountDays",
                "Die Skontofrist darf nicht negativ sein."
            );
        }
    }

    private void validateDiscountPercent(
        BigDecimal discountPercent,
        ValidationResult result
    ) {
        if (discountPercent == null) {
            return;
        }

        if (discountPercent.compareTo(
            ZERO
        ) < 0) {

            result.addViolation(
                "discountPercent",
                "Der Skontosatz darf nicht negativ sein."
            );
        }

        if (discountPercent.compareTo(
            MAX_DISCOUNT_PERCENT
        ) > 0) {

            result.addViolation(
                "discountPercent",
                "Der Skontosatz darf maximal "
                    + "100 % betragen."
            );
        }
    }

    private void validateDiscountPeriod(
        InvoicePaymentTermsData invoice,
        boolean hasDiscountDays,
        ValidationResult result
    ) {
        if (!hasDiscountDays) {
            return;
        }

        Integer discountDays =
            invoice.getDiscountDays();

        Integer paymentTermDays =
            invoice.getPaymentTermDays();

        if (paymentTermDays == null
            || paymentTermDays <= 0) {

            return;
        }

        /*
         * Die Skontofrist muss kürzer als das
         * reguläre Zahlungsziel sein.
         */
        if (discountDays >= paymentTermDays) {

            result.addViolation(
                "discountDays",
                "Die Skontofrist muss kürzer als "
                    + "das reguläre Zahlungsziel sein."
            );

            result.addViolation(
                "paymentTermDays",
                "Das Zahlungsziel muss länger als "
                    + "die Skontofrist sein."
            );
        }
    }

    private void validateDiscountDueDate(
        InvoicePaymentTermsData invoice,
        boolean hasDiscountDays,
        ValidationResult result
    ) {
        if (!hasDiscountDays) {
            return;
        }

        LocalDate invoiceDate =
            invoice.getInvoiceDate();

        LocalDate dueDate =
            invoice.getDueDate();

        Integer discountDays =
            invoice.getDiscountDays();

        if (invoiceDate == null
            || dueDate == null
            || discountDays == null) {

            return;
        }

        LocalDate discountDueDate =
            invoiceDate.plusDays(
                discountDays
            );

        /*
         * Das errechnete Skontodatum muss vor
         * dem regulären Fälligkeitsdatum liegen.
         */
        if (!discountDueDate.isBefore(
            dueDate
        )) {
            result.addViolation(
                "discountDays",
                "Das Skontodatum "
                    + discountDueDate
                    + " muss vor dem Fälligkeitsdatum "
                    + dueDate
                    + " liegen."
            );
        }
    }

    private static final class ValidationResult {

        private final ConstraintValidatorContext context;

        private boolean valid =
            true;

        private boolean defaultViolationDisabled =
            false;

        private ValidationResult(
            ConstraintValidatorContext context
        ) {
            this.context =
                context;
        }

        private void addViolation(
            String property,
            String message
        ) {
            disableDefaultViolation();

            context
                .buildConstraintViolationWithTemplate(
                    message
                )
                .addPropertyNode(
                    property
                )
                .addConstraintViolation();

            valid =
                false;
        }

        private void disableDefaultViolation() {
            if (defaultViolationDisabled) {
                return;
            }

            context.disableDefaultConstraintViolation();

            defaultViolationDisabled =
                true;
        }

        private boolean isValid() {
            return valid;
        }
    }
}
