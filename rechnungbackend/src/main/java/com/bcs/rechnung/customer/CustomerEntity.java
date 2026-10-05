package com.bcs.rechnung.customer;

import com.bcs.rechnung.company.CompanyEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "customer")
public class CustomerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(
        fetch = FetchType.LAZY,
        optional = false
    )
    @JoinColumn(
        name = "company_id",
        nullable = false
    )
    private CompanyEntity company;

    @Column(
        name = "customer_number",
        nullable = false,
        length = 30
    )
    private String customerNumber;

    @Column(
        nullable = false,
        length = 200
    )
    private String name;

    @Column(
        name = "contact_person",
        length = 200
    )
    private String contactPerson;

    @Column(
        length = 200
    )
    private String street;

    @Column(
        name = "zip_code",
        length = 20
    )
    private String zipCode;

    @Column(
        length = 100
    )
    private String city;

    @Column(
        length = 100
    )
    private String country;

    @Column(
        length = 100
    )
    private String phone;

    @Column(
        length = 200
    )
    private String email;

    @Column(
        name = "vat_id",
        length = 50
    )
    private String vatId;

    @Column(
        nullable = false
    )
    private boolean active = true;
}
