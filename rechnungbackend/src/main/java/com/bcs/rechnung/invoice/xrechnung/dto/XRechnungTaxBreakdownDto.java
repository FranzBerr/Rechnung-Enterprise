package com.bcs.rechnung.invoice.xrechnung.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class XRechnungTaxBreakdownDto {

    private String taxCategoryCode;

    private BigDecimal vatRate;

    private BigDecimal taxableAmount;

    private BigDecimal taxAmount;
}
