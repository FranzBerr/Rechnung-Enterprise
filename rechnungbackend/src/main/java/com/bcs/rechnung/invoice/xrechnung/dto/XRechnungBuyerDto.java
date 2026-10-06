package com.bcs.rechnung.invoice.xrechnung.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class XRechnungBuyerDto {

    private String name;

    private String street;

    private String zipCode;

    private String city;

    private String country;

    private String leitwegId;

    private String electronicAddress;

    private String electronicAddressScheme;
}
