package com.bcs.rechnung.company;

import com.bcs.rechnung.company.dto.CompanyDto;
import com.bcs.rechnung.company.dto.CompanyRequest;
import org.springframework.stereotype.Component;

@Component
public class CompanyMapper {

    public CompanyDto toDto(
        CompanyEntity entity
    ) {

        return new CompanyDto(
            entity.getId(),
            entity.getName(),
            entity.getLegalForm(),
            entity.getTaxNumber(),
            entity.getVatId(),
            entity.getStreet(),
            entity.getZipCode(),
            entity.getCity(),
            entity.getCountry(),
            entity.getPhone(),
            entity.getEmail(),
            entity.getWebsite(),
            entity.isActive()
        );
    }

    public CompanyEntity toEntity(
        CompanyRequest request
    ) {

        CompanyEntity entity =
            new CompanyEntity();

        entity.setName(
            request.name()
        );

        entity.setLegalForm(
            request.legalForm()
        );

        entity.setTaxNumber(
            request.taxNumber()
        );

        entity.setVatId(
            request.vatId()
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

        entity.setWebsite(
            request.website()
        );

        entity.setActive(
            request.active()
        );

        return entity;
    }

    public void updateEntity(
        CompanyEntity entity,
        CompanyRequest request
    ) {

        entity.setName(
            request.name()
        );

        entity.setLegalForm(
            request.legalForm()
        );

        entity.setTaxNumber(
            request.taxNumber()
        );

        entity.setVatId(
            request.vatId()
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

        entity.setWebsite(
            request.website()
        );

        entity.setActive(
            request.active()
        );
    }
}
