package com.bcs.rechnung.invoice.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Target(
    ElementType.TYPE
)
@Retention(
    RetentionPolicy.RUNTIME
)
@Constraint(
    validatedBy =
        InvoicePaymentTermsValidator.class
)
public @interface ValidInvoicePaymentTerms {

    String message() default
        "Die Zahlungsbedingungen der Rechnung sind ungültig.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
