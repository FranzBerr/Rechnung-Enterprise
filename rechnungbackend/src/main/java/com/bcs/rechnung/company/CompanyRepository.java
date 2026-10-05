package com.bcs.rechnung.company;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CompanyRepository
    extends JpaRepository<CompanyEntity, Long> {

    List<CompanyEntity> findByActiveTrue();

    List<CompanyEntity> findByActiveFalse();

    Optional<CompanyEntity> findByNameIgnoreCase(
        String name
    );

    boolean existsByNameIgnoreCase(
        String name
    );

    List<CompanyEntity>
    findByNameContainingIgnoreCaseOrderByNameAsc(
        String name
    );

    List<CompanyEntity>
    findByCountryIgnoreCaseOrderByNameAsc(
        String country
    );

    List<CompanyEntity>
    findByCityIgnoreCaseOrderByNameAsc(
        String city
    );

    Optional<CompanyEntity>
    findFirstByActiveTrueOrderByIdAsc();
}
