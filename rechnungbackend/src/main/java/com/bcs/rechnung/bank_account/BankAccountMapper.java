package com.bcs.rechnung.bank_account;

import com.bcs.rechnung.bank.BankEntity;
import com.bcs.rechnung.bank_account.dto.BankAccountDto;
import com.bcs.rechnung.bank_account.dto.BankAccountRequest;
import com.bcs.rechnung.company.CompanyEntity;
import org.springframework.stereotype.Component;

@Component
public class BankAccountMapper {

    public BankAccountDto toDto(
        BankAccountEntity entity
    ) {

        return new BankAccountDto(
            entity.getId(),
            entity.getCompany().getId(),
            entity.getBank().getId(),
            entity.getBank().getBankName(),
            entity.getBank().getBic(),
            entity.getAccountHolder(),
            entity.getIban(),
            entity.isDefaultAccount(),
            entity.isActive()
        );
    }

    public BankAccountEntity toEntity(
        BankAccountRequest request,
        CompanyEntity company,
        BankEntity bank
    ) {

        BankAccountEntity entity =
            new BankAccountEntity();

        entity.setCompany(
            company
        );

        entity.setBank(
            bank
        );

        entity.setAccountHolder(
            request.accountHolder()
        );

        entity.setIban(
            request.iban()
        );

        entity.setDefaultAccount(
            request.defaultAccount()
        );

        entity.setActive(
            request.active()
        );

        return entity;
    }

    public void updateEntity(
        BankAccountEntity entity,
        BankAccountRequest request,
        CompanyEntity company,
        BankEntity bank
    ) {

        entity.setCompany(
            company
        );

        entity.setBank(
            bank
        );

        entity.setAccountHolder(
            request.accountHolder()
        );

        entity.setIban(
            request.iban()
        );

        entity.setDefaultAccount(
            request.defaultAccount()
        );

        entity.setActive(
            request.active()
        );
    }
}
