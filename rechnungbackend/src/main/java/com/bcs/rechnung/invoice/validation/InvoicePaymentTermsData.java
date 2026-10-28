package com.bcs.rechnung.invoice.validation;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface InvoicePaymentTermsData {

    LocalDate getInvoiceDate();

    LocalDate getDueDate();

    Integer getPaymentTermDays();

    Integer getDiscountDays();

    BigDecimal getDiscountPercent();
}
