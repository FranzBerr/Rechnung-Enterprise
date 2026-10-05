package com.bcs.rechnung.company;

import com.bcs.rechnung.company.dto.CompanyDto;
import com.bcs.rechnung.company.dto.CompanyRequest;
import com.bcs.rechnung.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CompanyServiceImpl
    implements CompanyService {

    private final CompanyRepository companyRepository;

    private final CompanyMapper companyMapper;

    @Override
    @Transactional(readOnly = true)
    public List<CompanyDto> findAll() {

        return companyRepository.findAll()
            .stream()
            .map(companyMapper::toDto)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CompanyDto> findActive() {

        return companyRepository.findByActiveTrue()
            .stream()
            .map(companyMapper::toDto)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CompanyDto findById(
        Long id
    ) {

        CompanyEntity entity =
            companyRepository.findById(id)
                .orElseThrow(() ->
                    new ResourceNotFoundException(
                        "Firma mit ID "
                            + id
                            + " wurde nicht gefunden."
                    )
                );

        return companyMapper.toDto(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public CompanyDto findDefaultCompany() {

        CompanyEntity entity =
            companyRepository
                .findFirstByActiveTrueOrderByIdAsc()
                .orElseThrow(() ->
                    new ResourceNotFoundException(
                        "Keine aktive Firma vorhanden."
                    )
                );

        return companyMapper.toDto(entity);
    }

    @Override
    public CompanyDto create(
        CompanyRequest request
    ) {

        if (companyRepository.existsByNameIgnoreCase(
            request.name()
        )) {

            throw new IllegalArgumentException(
                "Eine Firma mit dem Namen '"
                    + request.name()
                    + "' existiert bereits."
            );
        }

        CompanyEntity entity =
            companyMapper.toEntity(request);

        entity =
            companyRepository.save(entity);

        return companyMapper.toDto(entity);
    }

    @Override
    public CompanyDto update(
        Long id,
        CompanyRequest request
    ) {

        CompanyEntity entity =
            companyRepository.findById(id)
                .orElseThrow(() ->
                    new ResourceNotFoundException(
                        "Firma mit ID "
                            + id
                            + " wurde nicht gefunden."
                    )
                );

        companyRepository.findByNameIgnoreCase(
                request.name()
            )
            .ifPresent(existing -> {

                if (!existing.getId().equals(id)) {

                    throw new IllegalArgumentException(
                        "Eine Firma mit dem Namen '"
                            + request.name()
                            + "' existiert bereits."
                    );
                }
            });

        companyMapper.updateEntity(
            entity,
            request
        );

        entity =
            companyRepository.save(entity);

        return companyMapper.toDto(entity);
    }

    @Override
    public CompanyDto activate(
        Long id
    ) {

        CompanyEntity entity =
            companyRepository.findById(id)
                .orElseThrow(() ->
                    new ResourceNotFoundException(
                        "Firma mit ID "
                            + id
                            + " wurde nicht gefunden."
                    )
                );

        entity.setActive(true);

        entity =
            companyRepository.save(entity);

        return companyMapper.toDto(entity);
    }

    @Override
    public CompanyDto deactivate(
        Long id
    ) {

        CompanyEntity entity =
            companyRepository.findById(id)
                .orElseThrow(() ->
                    new ResourceNotFoundException(
                        "Firma mit ID "
                            + id
                            + " wurde nicht gefunden."
                    )
                );

        entity.setActive(false);

        entity =
            companyRepository.save(entity);

        return companyMapper.toDto(entity);
    }

    @Override
    public void delete(
        Long id
    ) {

        CompanyEntity entity =
            companyRepository.findById(id)
                .orElseThrow(() ->
                    new ResourceNotFoundException(
                        "Firma mit ID "
                            + id
                            + " wurde nicht gefunden."
                    )
                );

        companyRepository.delete(entity);
    }
}
