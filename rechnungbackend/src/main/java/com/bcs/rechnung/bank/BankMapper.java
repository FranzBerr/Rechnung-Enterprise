package com.bcs.rechnung.bank;

import com.bcs.rechnung.bank.dto.BankDto;
import com.bcs.rechnung.bank.dto.BankRequest;
import org.springframework.stereotype.Component;

@Component
public class BankMapper {

    public BankDto toDto(
        BankEntity entity
    ) {
        return new BankDto(
            entity.getId(),
            entity.getBankCode(),
            entity.getBankName(),
            entity.getBic(),
            entity.getCountry(),
            entity.isActive()
        );
    }

    public BankEntity toEntity(
        BankRequest request
    ) {
        BankEntity entity =
            new BankEntity();

        entity.setBankCode(
            request.bankCode()
        );

        entity.setBankName(
            request.bankName()
        );

        entity.setBic(
            request.bic()
        );

        entity.setCountry(
            request.country()
        );

        entity.setActive(
            request.active()
        );

        return entity;
    }
}
