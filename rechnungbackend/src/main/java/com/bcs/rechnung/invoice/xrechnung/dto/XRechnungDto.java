package com.bcs.rechnung.invoice.xrechnung.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


//---------------------------------------------------------------------
//            Haupt-DTO
//---------------------------------------------------------------------
@Getter
@Setter
public class XRechnungDto {

    private String invoiceNumber;

    private LocalDate invoiceDate;

    private LocalDate serviceDate;

    private LocalDate dueDate;

    private String buyerReference;

    private String orderReference;

    private String invoiceTypeCode;

    private String paymentMeansCode;

    private String currencyCode;

    private String paymentReference;

    private XRechnungSellerDto seller;

    private XRechnungBuyerDto buyer;

    private XRechnungTotalsDto totals;

    private String bankName;

    private String iban;

    private String bic;

    private String accountHolder;

    private List<XRechnungItemDto> items =
        new ArrayList<>();
}
