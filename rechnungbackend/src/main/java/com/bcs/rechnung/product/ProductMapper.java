package com.bcs.rechnung.product;

import com.bcs.rechnung.company.CompanyEntity;
import com.bcs.rechnung.product.dto.ProductDto;
import com.bcs.rechnung.product.dto.ProductRequest;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public ProductDto toDto(
        ProductEntity entity
    ) {

        return new ProductDto(
            entity.getId(),
            entity.getCompany().getId(),
            entity.getProductNumber(),
            entity.getName(),
            entity.getDescription(),
            entity.getUnit(),
            entity.getNetPrice(),
            entity.getVatRate(),
            entity.isActive()
        );
    }

    public ProductEntity toEntity(
        ProductRequest request,
        CompanyEntity company
    ) {

        ProductEntity entity =
            new ProductEntity();

        entity.setCompany(company);
        entity.setProductNumber(request.productNumber());
        entity.setName(request.name());
        entity.setDescription(request.description());
        entity.setUnit(request.unit());
        entity.setNetPrice(request.netPrice());
        entity.setVatRate(request.vatRate());
        entity.setActive(request.active());

        return entity;
    }

    public void updateEntity(
        ProductEntity entity,
        ProductRequest request,
        CompanyEntity company
    ) {

        entity.setCompany(company);
        entity.setProductNumber(request.productNumber());
        entity.setName(request.name());
        entity.setDescription(request.description());
        entity.setUnit(request.unit());
        entity.setNetPrice(request.netPrice());
        entity.setVatRate(request.vatRate());
        entity.setActive(request.active());
    }
}
