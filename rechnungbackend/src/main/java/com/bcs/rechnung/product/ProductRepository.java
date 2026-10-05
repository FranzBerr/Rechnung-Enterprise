package com.bcs.rechnung.product;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository
    extends JpaRepository<ProductEntity, Long> {

    List<ProductEntity> findByActiveTrue();

    List<ProductEntity> findByCompany_Id(
        Long companyId
    );

    List<ProductEntity> findByCompany_IdAndActiveTrue(
        Long companyId
    );

    Optional<ProductEntity> findByProductNumberIgnoreCase(
        String productNumber
    );

    boolean existsByProductNumberIgnoreCase(
        String productNumber
    );

    List<ProductEntity>
    findByNameContainingIgnoreCaseOrderByNameAsc(
        String name
    );
}
