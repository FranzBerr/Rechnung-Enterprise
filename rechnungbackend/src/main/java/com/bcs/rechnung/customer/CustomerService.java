package com.bcs.rechnung.customer;

import com.bcs.rechnung.customer.dto.CustomerDto;
import com.bcs.rechnung.customer.dto.CustomerRequest;

import java.util.List;

public interface CustomerService {

    List<CustomerDto> findAll();

    List<CustomerDto> findActive();

    List<CustomerDto> findByCompanyId(
        Long companyId
    );

    CustomerDto findById(
        Long id
    );

    CustomerDto create(
        CustomerRequest request
    );

    CustomerDto update(
        Long id,
        CustomerRequest request
    );

    CustomerDto activate(
        Long id
    );

    CustomerDto deactivate(
        Long id
    );

    void delete(
        Long id
    );
}
