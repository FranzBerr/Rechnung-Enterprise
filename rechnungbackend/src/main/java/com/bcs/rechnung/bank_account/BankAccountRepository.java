package com.bcs.rechnung.bank_account;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BankAccountRepository
    extends JpaRepository<BankAccountEntity, Long> {

    List<BankAccountEntity> findByCompany_Id(
        Long companyId
    );

    List<BankAccountEntity> findByCompany_IdAndActiveTrue(
        Long companyId
    );

    List<BankAccountEntity> findByBank_Id(
        Long bankId
    );

    Optional<BankAccountEntity> findByIbanIgnoreCase(
        String iban
    );

    boolean existsByIbanIgnoreCase(
        String iban
    );

    Optional<BankAccountEntity>
    findByCompany_IdAndDefaultAccountTrue(
        Long companyId
    );

    List<BankAccountEntity> findByActiveTrue();

    List<BankAccountEntity> findByActiveFalse();
}
