package com.bcs.rechnung.bank;

import com.bcs.rechnung.bank.dto.BankDto;
import com.bcs.rechnung.bank.dto.BankRequest;

import java.util.List;

public interface BankService {

    List<BankDto> findAll();

    BankDto findById(Long id);

    BankDto create(BankRequest request);

    BankDto update(
        Long id,
        BankRequest request
    );

    void delete(Long id);
}
