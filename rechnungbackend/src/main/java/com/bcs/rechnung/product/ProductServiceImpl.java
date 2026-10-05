package com.bcs.rechnung.product;

import com.bcs.rechnung.company.CompanyEntity;
import com.bcs.rechnung.company.CompanyRepository;
import com.bcs.rechnung.exception.ResourceNotFoundException;
import com.bcs.rechnung.product.dto.ProductDto;
import com.bcs.rechnung.product.dto.ProductRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductServiceImpl
    implements ProductService {

    private final ProductRepository productRepository;

    private final CompanyRepository companyRepository;

    private final ProductMapper productMapper;

    @Override
    @Transactional(readOnly = true)
    public List<ProductDto> findAll() {

        return productRepository.findAll()
            .stream()
            .map(productMapper::toDto)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDto> findActive() {

        return productRepository.findByActiveTrue()
            .stream()
            .map(productMapper::toDto)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDto> findByCompanyId(
        Long companyId
    ) {

        return productRepository
            .findByCompany_Id(companyId)
            .stream()
            .map(productMapper::toDto)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ProductDto findById(
        Long id
    ) {

        ProductEntity entity =
            loadProduct(id);

        return productMapper.toDto(entity);
    }

    @Override
    public ProductDto create(
        ProductRequest request
    ) {

        validateProductNumber(
            request.productNumber(),
            null
        );

        CompanyEntity company =
            loadCompany(
                request.companyId()
            );

        ProductEntity entity =
            productMapper.toEntity(
                request,
                company
            );

        entity =
            productRepository.save(entity);

        return productMapper.toDto(entity);
    }

    @Override
    public ProductDto update(
        Long id,
        ProductRequest request
    ) {

        ProductEntity entity =
            loadProduct(id);

        validateProductNumber(
            request.productNumber(),
            id
        );

        CompanyEntity company =
            loadCompany(
                request.companyId()
            );

        productMapper.updateEntity(
            entity,
            request,
            company
        );

        entity =
            productRepository.save(entity);

        return productMapper.toDto(entity);
    }

    @Override
    public ProductDto activate(
        Long id
    ) {

        ProductEntity entity =
            loadProduct(id);

        entity.setActive(true);

        entity =
            productRepository.save(entity);

        return productMapper.toDto(entity);
    }

    @Override
    public ProductDto deactivate(
        Long id
    ) {

        ProductEntity entity =
            loadProduct(id);

        entity.setActive(false);

        entity =
            productRepository.save(entity);

        return productMapper.toDto(entity);
    }

    @Override
    public void delete(
        Long id
    ) {

        ProductEntity entity =
            loadProduct(id);

        productRepository.delete(entity);
    }

    private ProductEntity loadProduct(
        Long id
    ) {

        return productRepository
            .findById(id)
            .orElseThrow(() ->
                new ResourceNotFoundException(
                    "Produkt mit ID "
                        + id
                        + " wurde nicht gefunden."
                )
            );
    }

    private CompanyEntity loadCompany(
        Long companyId
    ) {

        return companyRepository
            .findById(companyId)
            .orElseThrow(() ->
                new ResourceNotFoundException(
                    "Firma mit ID "
                        + companyId
                        + " wurde nicht gefunden."
                )
            );
    }

    private void validateProductNumber(
        String productNumber,
        Long currentId
    ) {

        productRepository
            .findByProductNumberIgnoreCase(
                productNumber
            )
            .ifPresent(existing -> {

                if (currentId == null
                    || !existing.getId().equals(currentId)) {

                    throw new IllegalArgumentException(
                        "Die Produktnummer "
                            + productNumber
                            + " existiert bereits."
                    );
                }
            });
    }
}
