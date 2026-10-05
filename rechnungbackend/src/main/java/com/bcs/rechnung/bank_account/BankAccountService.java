package com.bcs.rechnung.bank_account;

import com.bcs.rechnung.bank_account.dto.BankAccountDto;
import com.bcs.rechnung.bank_account.dto.BankAccountRequest;

import java.util.List;

public interface BankAccountService {

    List<BankAccountDto> findAll();

    List<BankAccountDto> findActive();

    List<BankAccountDto> findByCompanyId(
        Long companyId
    );

    BankAccountDto findById(
        Long id
    );

    BankAccountDto findDefaultByCompanyId(
        Long companyId
    );

    BankAccountDto create(
        BankAccountRequest request
    );

    BankAccountDto update(
        Long id,
        BankAccountRequest request
    );

    BankAccountDto activate(
        Long id
    );

    BankAccountDto deactivate(
        Long id
    );

    void delete(
        Long id
    );
}
