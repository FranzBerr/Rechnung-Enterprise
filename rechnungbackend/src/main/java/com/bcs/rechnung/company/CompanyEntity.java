package com.bcs.rechnung.company;

import com.bcs.rechnung.bank_account.BankAccountEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "company")
public class CompanyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
        name = "name",
        nullable = false,
        length = 200
    )
    private String name;

    @Column(
        name = "legal_form",
        length = 200
    )
    private String legalForm;

    @Column(
        name = "tax_number",
        length = 100
    )
    private String taxNumber;

    @Column(
        name = "vat_id",
        length = 50
    )
    private String vatId;

    @Column(
        name = "street",
        length = 200
    )
    private String street;

    @Column(
        name = "zip_code",
        length = 20
    )
    private String zipCode;

    @Column(
        name = "city",
        length = 100
    )
    private String city;

    @Column(
        name = "country",
        length = 100
    )
    private String country;

    @Column(
        name = "phone",
        length = 100
    )
    private String phone;

    @Column(
        name = "email",
        length = 200
    )
    private String email;

    @Column(
        name = "website",
        length = 500
    )
    private String website;

    @Column(
        name = "active",
        nullable = false
    )
    private boolean active = true;

    @OneToMany(
        mappedBy = "company",
        fetch = FetchType.LAZY
    )
    private List<BankAccountEntity> bankAccounts =
        new ArrayList<>();
}
