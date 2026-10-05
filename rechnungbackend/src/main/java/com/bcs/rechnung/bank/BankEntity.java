package com.bcs.rechnung.bank;

import com.bcs.rechnung.bank_account.BankAccountEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "bank")
public class BankEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
        name = "bank_code",
        length = 20
    )
    private String bankCode;

    @Column(
        name = "bank_name",
        nullable = false,
        length = 200
    )
    private String bankName;

    @Column(
        nullable = false,
        length = 11
    )
    private String bic;

    @Column(
        length = 10
    )
    private String country;

    @Column(
        nullable = false
    )
    private boolean active = true;

    @OneToMany(
        mappedBy = "bank",
        fetch = FetchType.LAZY
    )
    private List<BankAccountEntity> bankAccounts =
        new ArrayList<>();
}
