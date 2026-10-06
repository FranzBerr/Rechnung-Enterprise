package com.bcs.rechnung.invoice.xrechnung.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class XRechnungSellerDto {

    /**
     * BT-27
     * Verkäufername
     */
    private String name;

    /**
     * BT-35
     * Straße
     */
    private String street;

    /**
     * BT-38
     * PLZ
     */
    private String zipCode;

    /**
     * BT-37
     * Ort
     */
    private String city;

    /**
     * BT-40
     * Land
     */
    private String country;

    /**
     * BT-31
     * Umsatzsteuer-ID
     */
    private String vatId;

    /**
     * BT-32
     * Steuernummer
     */
    private String taxNumber;

    /**
     * BT-34
     * Elektronische Adresse
     */
    private String electronicAddress;

    /**
     * BT-34-1
     * Schema der elektronischen Adresse
     * z.B. EM
     */
    private String electronicAddressScheme;

    /**
     * BT-41
     * Ansprechpartner
     */
    private String contactPerson;

    /**
     * BT-42
     * Telefon
     */
    private String phone;

    /**
     * BT-43
     * E-Mail
     */
    private String email;
}
