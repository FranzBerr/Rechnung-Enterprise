package com.bcs.rechnung.invoice.xrechnung.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class XRechnungItemDto {

    private Integer positionNumber;

    private String productNumber;

    private String description;

    private BigDecimal quantity;

    private String unit;

    private BigDecimal unitPriceNet;

    private BigDecimal vatRate;

    private String taxCategoryCode;

    private BigDecimal lineNet;

    private BigDecimal taxAmount;

    private BigDecimal lineGross;

    private String name;

}
