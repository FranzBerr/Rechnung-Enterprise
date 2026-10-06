package com.bcs.rechnung.invoice.xrechnung.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class XRechnungTotalsDto {

    private BigDecimal subtotalNet;

    private BigDecimal taxAmount;

    private BigDecimal totalGross;
}
