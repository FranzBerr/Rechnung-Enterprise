package com.bcs.rechnung.customer;

import com.bcs.rechnung.company.CompanyEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
    name = "customer",
    uniqueConstraints = @UniqueConstraint(
        name = "UK_CUSTOMER_CUSTOMER_NUMBER",
        columnNames = "customer_number"
    )
)
public class CustomerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "company_id", nullable = false)
    private CompanyEntity company;

    @Column(
        name = "customer_number",
        nullable = false,
        length = 30
    )
    private String customerNumber;

    @Column(
        name = "name",
        nullable = false,
        length = 200
    )
    private String name;

    @Column(name = "contact_person", length = 200)
    private String contactPerson;

    @Column(name = "street", length = 200)
    private String street;

    @Column(name = "zip_code", length = 20)
    private String zipCode;

    @Column(name = "city", length = 100)
    private String city;

    @Column(name = "country", length = 100)
    private String country;

    @Column(name = "phone", length = 100)
    private String phone;

    @Column(name = "email", length = 200)
    private String email;

    @Column(name = "vat_id", length = 50)
    private String vatId;

    @Column(name = "leitweg_id", length = 100)
    private String leitwegId;

    @Column(name = "electronic_address", length = 255)
    private String electronicAddress;

    @Column(name = "electronic_address_scheme", length = 20)
    private String electronicAddressScheme;

    @Column(name = "business_process_id", length = 255)
    private String businessProcessId;

    @Column(name = "buyer_reference", length = 100)
    private String buyerReference;

    @Column(name = "department", length = 200)
    private String department;

    @Column(name = "active", nullable = false)
    private boolean active = true;
}
