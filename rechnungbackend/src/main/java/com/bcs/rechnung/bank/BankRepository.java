package com.bcs.rechnung.bank;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BankRepository
    extends JpaRepository<BankEntity, Long> {

    Optional<BankEntity> findByBicIgnoreCase(
        String bic
    );

    Optional<BankEntity> findByBankNameIgnoreCase(
        String bankName
    );

    List<BankEntity> findAllByActiveTrue();

    boolean existsByBicIgnoreCase(
        String bic
    );

    boolean existsByBankNameIgnoreCase(
        String bankName
    );

    List<BankEntity> findByBankNameContainingIgnoreCaseOrderByBankNameAsc(
        String bankName
    );

    List<BankEntity> findByCountryIgnoreCaseOrderByBankNameAsc(
        String country
    );
}
