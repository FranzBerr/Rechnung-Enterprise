package com.bcs.rechnung.customer;

import com.bcs.rechnung.company.CompanyEntity;
import com.bcs.rechnung.company.CompanyRepository;
import com.bcs.rechnung.customer.dto.CustomerDto;
import com.bcs.rechnung.customer.dto.CustomerRequest;
import com.bcs.rechnung.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomerServiceImpl
    implements CustomerService {

    private final CustomerRepository customerRepository;

    private final CompanyRepository companyRepository;

    private final CustomerMapper customerMapper;

    @Override
    @Transactional(readOnly = true)
    public List<CustomerDto> findAll() {

        return customerRepository.findAll()
            .stream()
            .map(customerMapper::toDto)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerDto> findActive() {

        return customerRepository.findByActiveTrue()
            .stream()
            .map(customerMapper::toDto)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerDto> findByCompanyId(
        Long companyId
    ) {

        return customerRepository
            .findByCompany_Id(companyId)
            .stream()
            .map(customerMapper::toDto)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerDto findById(
        Long id
    ) {

        CustomerEntity entity =
            loadCustomer(id);

        return customerMapper.toDto(entity);
    }

    @Override
    public CustomerDto create(
        CustomerRequest request
    ) {

        validateCustomerNumber(
            request.customerNumber(),
            null
        );

        CompanyEntity company =
            loadCompany(
                request.companyId()
            );

        CustomerEntity entity =
            customerMapper.toEntity(
                request,
                company
            );

        entity =
            customerRepository.save(entity);

        return customerMapper.toDto(entity);
    }

    @Override
    public CustomerDto update(
        Long id,
        CustomerRequest request
    ) {

        CustomerEntity entity =
            loadCustomer(id);

        validateCustomerNumber(
            request.customerNumber(),
            id
        );

        CompanyEntity company =
            loadCompany(
                request.companyId()
            );

        customerMapper.updateEntity(
            entity,
            request,
            company
        );

        entity =
            customerRepository.save(entity);

        return customerMapper.toDto(entity);
    }

    @Override
    public CustomerDto activate(
        Long id
    ) {

        CustomerEntity entity =
            loadCustomer(id);

        entity.setActive(true);

        entity =
            customerRepository.save(entity);

        return customerMapper.toDto(entity);
    }

    @Override
    public CustomerDto deactivate(
        Long id
    ) {

        CustomerEntity entity =
            loadCustomer(id);

        entity.setActive(false);

        entity =
            customerRepository.save(entity);

        return customerMapper.toDto(entity);
    }

    @Override
    public void delete(
        Long id
    ) {

        CustomerEntity entity =
            loadCustomer(id);

        customerRepository.delete(entity);
    }

    private CustomerEntity loadCustomer(
        Long id
    ) {

        return customerRepository
            .findById(id)
            .orElseThrow(() ->
                new ResourceNotFoundException(
                    "Kunde mit ID "
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

    private void validateCustomerNumber(
        String customerNumber,
        Long currentId
    ) {

        customerRepository
            .findByCustomerNumberIgnoreCase(
                customerNumber
            )
            .ifPresent(existing -> {

                if (currentId == null
                    || !existing.getId().equals(currentId)) {

                    throw new IllegalArgumentException(
                        "Die Kundennummer "
                            + customerNumber
                            + " existiert bereits."
                    );
                }
            });
    }
}
