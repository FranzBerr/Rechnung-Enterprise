package com.bcs.rechnung.customer;

import com.bcs.rechnung.company.CompanyEntity;
import com.bcs.rechnung.customer.dto.CustomerDto;
import com.bcs.rechnung.customer.dto.CustomerRequest;
import org.springframework.stereotype.Component;

@Component
public class CustomerMapper {

    public CustomerDto toDto(
        CustomerEntity entity
    ) {

        return new CustomerDto(
            entity.getId(),
            entity.getCompany().getId(),
            entity.getCustomerNumber(),
            entity.getName(),
            entity.getContactPerson(),
            entity.getStreet(),
            entity.getZipCode(),
            entity.getCity(),
            entity.getCountry(),
            entity.getPhone(),
            entity.getEmail(),
            entity.getVatId(),
            entity.isActive()
        );
    }

    public CustomerEntity toEntity(
        CustomerRequest request,
        CompanyEntity company
    ) {

        CustomerEntity entity =
            new CustomerEntity();

        entity.setCompany(
            company
        );

        entity.setCustomerNumber(
            request.customerNumber()
        );

        entity.setName(
            request.name()
        );

        entity.setContactPerson(
            request.contactPerson()
        );

        entity.setStreet(
            request.street()
        );

        entity.setZipCode(
            request.zipCode()
        );

        entity.setCity(
            request.city()
        );

        entity.setCountry(
            request.country()
        );

        entity.setPhone(
            request.phone()
        );

        entity.setEmail(
            request.email()
        );

        entity.setVatId(
            request.vatId()
        );

        entity.setActive(
            request.active()
        );

        return entity;
    }

    public void updateEntity(
        CustomerEntity entity,
        CustomerRequest request,
        CompanyEntity company
    ) {

        entity.setCompany(
            company
        );

        entity.setCustomerNumber(
            request.customerNumber()
        );

        entity.setName(
            request.name()
        );

        entity.setContactPerson(
            request.contactPerson()
        );

        entity.setStreet(
            request.street()
        );

        entity.setZipCode(
            request.zipCode()
        );

        entity.setCity(
            request.city()
        );

        entity.setCountry(
            request.country()
        );

        entity.setPhone(
            request.phone()
        );

        entity.setEmail(
            request.email()
        );

        entity.setVatId(
            request.vatId()
        );

        entity.setActive(
            request.active()
        );
    }
}
