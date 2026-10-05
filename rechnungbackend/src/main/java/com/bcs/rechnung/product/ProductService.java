package com.bcs.rechnung.product;

import com.bcs.rechnung.product.dto.ProductDto;
import com.bcs.rechnung.product.dto.ProductRequest;

import java.util.List;

public interface ProductService {

    List<ProductDto> findAll();

    List<ProductDto> findActive();

    List<ProductDto> findByCompanyId(
        Long companyId
    );

    ProductDto findById(
        Long id
    );

    ProductDto create(
        ProductRequest request
    );

    ProductDto update(
        Long id,
        ProductRequest request
    );

    ProductDto activate(
        Long id
    );

    ProductDto deactivate(
        Long id
    );

    void delete(
        Long id
    );
}
