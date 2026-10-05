package com.bcs.rechnung.bank_account;

import com.bcs.rechnung.bank.BankEntity;
import com.bcs.rechnung.company.CompanyEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "bank_account")
public class BankAccountEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "company_id",
        nullable = false
    )
    private CompanyEntity company;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "bank_id",
        nullable = false
    )
    private BankEntity bank;

    @Column(
        name = "account_holder",
        nullable = false,
        length = 200
    )
    private String accountHolder;

    @Column(
        nullable = false,
        length = 34,
        unique = true
    )
    private String iban;

    @Column(
        name = "default_account",
        nullable = false
    )
    private boolean defaultAccount;

    @Column(nullable = false)
    private boolean active = true;
}
