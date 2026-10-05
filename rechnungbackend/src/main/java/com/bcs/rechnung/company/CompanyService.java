package com.bcs.rechnung.company;

import com.bcs.rechnung.company.dto.CompanyDto;
import com.bcs.rechnung.company.dto.CompanyRequest;

import java.util.List;

public interface CompanyService {

    List<CompanyDto> findAll();

    List<CompanyDto> findActive();

    CompanyDto findById(
        Long id
    );

    CompanyDto findDefaultCompany();

    CompanyDto create(
        CompanyRequest request
    );

    CompanyDto update(
        Long id,
        CompanyRequest request
    );

    CompanyDto activate(
        Long id
    );

    CompanyDto deactivate(
        Long id
    );

    void delete(
        Long id
    );
}
