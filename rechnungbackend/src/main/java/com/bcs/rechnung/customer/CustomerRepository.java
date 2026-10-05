package com.bcs.rechnung.customer;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository
    extends JpaRepository<CustomerEntity, Long> {

    List<CustomerEntity> findByActiveTrue();

    List<CustomerEntity> findByActiveFalse();

    List<CustomerEntity> findByCompany_Id(
        Long companyId
    );

    List<CustomerEntity> findByCompany_IdAndActiveTrue(
        Long companyId
    );

    Optional<CustomerEntity> findByCustomerNumberIgnoreCase(
        String customerNumber
    );

    boolean existsByCustomerNumberIgnoreCase(
        String customerNumber
    );

    List<CustomerEntity>
    findByNameContainingIgnoreCaseOrderByNameAsc(
        String name
    );

    List<CustomerEntity>
    findByCityIgnoreCaseOrderByNameAsc(
        String city
    );

    List<CustomerEntity>
    findByCountryIgnoreCaseOrderByNameAsc(
        String country
    );

    Optional<CustomerEntity>
    findFirstByCompany_IdAndActiveTrueOrderByIdAsc(
        Long companyId
    );
}
