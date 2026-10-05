package com.bcs.rechnung.bank_account;

import com.bcs.rechnung.bank.BankEntity;
import com.bcs.rechnung.bank.BankRepository;
import com.bcs.rechnung.bank_account.dto.BankAccountDto;
import com.bcs.rechnung.bank_account.dto.BankAccountRequest;
import com.bcs.rechnung.company.CompanyEntity;
import com.bcs.rechnung.company.CompanyRepository;
import com.bcs.rechnung.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class BankAccountServiceImpl
    implements BankAccountService {

    private final BankAccountRepository bankAccountRepository;
    private final CompanyRepository companyRepository;
    private final BankRepository bankRepository;
    private final BankAccountMapper bankAccountMapper;

    @Override
    @Transactional(readOnly = true)
    public List<BankAccountDto> findAll() {
        return bankAccountRepository.findAll()
            .stream()
            .map(bankAccountMapper::toDto)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BankAccountDto> findActive() {
        return bankAccountRepository.findByActiveTrue()
            .stream()
            .map(bankAccountMapper::toDto)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BankAccountDto> findByCompanyId(
        Long companyId
    ) {
        validateId(
            companyId,
            "Firmen-ID"
        );

        ensureCompanyExists(companyId);

        return bankAccountRepository
            .findByCompany_Id(companyId)
            .stream()
            .map(bankAccountMapper::toDto)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public BankAccountDto findById(
        Long id
    ) {
        BankAccountEntity entity =
            loadBankAccount(id);

        return bankAccountMapper.toDto(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public BankAccountDto findDefaultByCompanyId(
        Long companyId
    ) {
        validateId(
            companyId,
            "Firmen-ID"
        );

        BankAccountEntity entity =
            bankAccountRepository
                .findByCompany_IdAndDefaultAccountTrue(
                    companyId
                )
                .orElseThrow(() ->
                    new ResourceNotFoundException(
                        (
                            "Für die Firma mit ID %d "
                                + "wurde kein Standardkonto gefunden."
                        ).formatted(companyId)
                    )
                );

        return bankAccountMapper.toDto(entity);
    }

    @Override
    public BankAccountDto create(
        BankAccountRequest request
    ) {
        validateRequest(request);

        CompanyEntity company =
            loadCompany(request.companyId());

        BankEntity bank =
            loadBank(request.bankId());

        String normalizedIban =
            normalizeIban(request.iban());

        validateUniqueIban(
            normalizedIban,
            null
        );

        BankAccountEntity entity =
            bankAccountMapper.toEntity(
                request,
                company,
                bank
            );

        entity.setIban(normalizedIban);

        if (entity.isDefaultAccount()) {
            clearExistingDefaultAccount(
                company.getId(),
                null
            );
        }

        BankAccountEntity savedEntity =
            bankAccountRepository.save(entity);

        log.info(
            "Bankkonto mit ID {} für Firma {} und Bank {} erstellt.",
            savedEntity.getId(),
            company.getId(),
            bank.getId()
        );

        return bankAccountMapper.toDto(savedEntity);
    }

    @Override
    public BankAccountDto update(
        Long id,
        BankAccountRequest request
    ) {
        validateId(
            id,
            "Bankkonto-ID"
        );

        validateRequest(request);

        BankAccountEntity entity =
            loadBankAccount(id);

        CompanyEntity company =
            loadCompany(request.companyId());

        BankEntity bank =
            loadBank(request.bankId());

        String normalizedIban =
            normalizeIban(request.iban());

        validateUniqueIban(
            normalizedIban,
            id
        );

        bankAccountMapper.updateEntity(
            entity,
            request,
            company,
            bank
        );

        entity.setIban(normalizedIban);

        if (entity.isDefaultAccount()) {
            clearExistingDefaultAccount(
                company.getId(),
                entity.getId()
            );
        }

        BankAccountEntity savedEntity =
            bankAccountRepository.save(entity);

        log.info(
            "Bankkonto mit ID {} aktualisiert.",
            savedEntity.getId()
        );

        return bankAccountMapper.toDto(savedEntity);
    }

    @Override
    public BankAccountDto activate(
        Long id
    ) {
        BankAccountEntity entity =
            loadBankAccount(id);

        if (!entity.getBank().isActive()) {
            throw new IllegalStateException(
                "Das Bankkonto kann nicht aktiviert werden, "
                    + "weil die zugeordnete Bank inaktiv ist."
            );
        }

        entity.setActive(true);

        BankAccountEntity savedEntity =
            bankAccountRepository.save(entity);

        log.info(
            "Bankkonto mit ID {} aktiviert.",
            savedEntity.getId()
        );

        return bankAccountMapper.toDto(savedEntity);
    }

    @Override
    public BankAccountDto deactivate(
        Long id
    ) {
        BankAccountEntity entity =
            loadBankAccount(id);

        entity.setActive(false);

        if (entity.isDefaultAccount()) {
            entity.setDefaultAccount(false);
        }

        BankAccountEntity savedEntity =
            bankAccountRepository.save(entity);

        log.info(
            "Bankkonto mit ID {} deaktiviert.",
            savedEntity.getId()
        );

        return bankAccountMapper.toDto(savedEntity);
    }

    @Override
    public void delete(
        Long id
    ) {
        BankAccountEntity entity =
            loadBankAccount(id);

        try {
            bankAccountRepository.delete(entity);
            bankAccountRepository.flush();

            log.info(
                "Bankkonto mit ID {} gelöscht.",
                id
            );
        } catch (DataIntegrityViolationException exception) {
            throw new IllegalStateException(
                "Das Bankkonto mit ID "
                    + id
                    + " kann nicht gelöscht werden, "
                    + "weil es noch verwendet wird.",
                exception
            );
        }
    }

    private BankAccountEntity loadBankAccount(
        Long id
    ) {
        validateId(
            id,
            "Bankkonto-ID"
        );

        return bankAccountRepository.findById(id)
            .orElseThrow(() ->
                new ResourceNotFoundException(
                    "Bankkonto mit ID %d wurde nicht gefunden."
                        .formatted(id)
                )
            );
    }

    private CompanyEntity loadCompany(
        Long companyId
    ) {
        validateId(
            companyId,
            "Firmen-ID"
        );

        return companyRepository.findById(companyId)
            .orElseThrow(() ->
                new ResourceNotFoundException(
                    "Firma mit ID %d wurde nicht gefunden."
                        .formatted(companyId)
                )
            );
    }

    private BankEntity loadBank(
        Long bankId
    ) {
        validateId(
            bankId,
            "Bank-ID"
        );

        BankEntity bank =
            bankRepository.findById(bankId)
                .orElseThrow(() ->
                    new ResourceNotFoundException(
                        "Bank mit ID %d wurde nicht gefunden."
                            .formatted(bankId)
                    )
                );

        if (!bank.isActive()) {
            throw new IllegalStateException(
                "Die Bank mit ID "
                    + bankId
                    + " ist nicht aktiv."
            );
        }

        return bank;
    }

    private void ensureCompanyExists(
        Long companyId
    ) {
        if (!companyRepository.existsById(companyId)) {
            throw new ResourceNotFoundException(
                "Firma mit ID %d wurde nicht gefunden."
                    .formatted(companyId)
            );
        }
    }

    private void validateRequest(
        BankAccountRequest request
    ) {
        if (request == null) {
            throw new IllegalArgumentException(
                "Der Bankkonto-Request darf nicht null sein."
            );
        }

        validateId(
            request.companyId(),
            "Firmen-ID"
        );

        validateId(
            request.bankId(),
            "Bank-ID"
        );

        if (request.accountHolder() == null
            || request.accountHolder().isBlank()) {

            throw new IllegalArgumentException(
                "Der Kontoinhaber muss angegeben werden."
            );
        }

        if (request.accountHolder().trim().length() > 200) {
            throw new IllegalArgumentException(
                "Der Kontoinhaber darf höchstens 200 Zeichen enthalten."
            );
        }

        if (request.iban() == null
            || request.iban().isBlank()) {

            throw new IllegalArgumentException(
                "Die IBAN muss angegeben werden."
            );
        }

        String normalizedIban =
            normalizeIban(request.iban());

        if (normalizedIban.length() > 34) {
            throw new IllegalArgumentException(
                "Die IBAN darf höchstens 34 Zeichen enthalten."
            );
        }

        if (!normalizedIban.matches("[A-Z0-9]+")) {
            throw new IllegalArgumentException(
                "Die IBAN darf nur Buchstaben und Ziffern enthalten."
            );
        }

        if (request.defaultAccount()
            && !request.active()) {

            throw new IllegalArgumentException(
                "Ein inaktives Bankkonto kann nicht "
                    + "als Standardkonto festgelegt werden."
            );
        }
    }

    private void validateUniqueIban(
        String iban,
        Long currentBankAccountId
    ) {
        bankAccountRepository
            .findByIbanIgnoreCase(iban)
            .filter(existing ->
                currentBankAccountId == null
                    || !existing.getId()
                    .equals(currentBankAccountId)
            )
            .ifPresent(existing -> {
                throw new IllegalArgumentException(
                    "Ein Bankkonto mit der IBAN "
                        + iban
                        + " existiert bereits."
                );
            });
    }

    private void clearExistingDefaultAccount(
        Long companyId,
        Long excludedBankAccountId
    ) {
        bankAccountRepository
            .findByCompany_IdAndDefaultAccountTrue(
                companyId
            )
            .filter(existing ->
                excludedBankAccountId == null
                    || !existing.getId()
                    .equals(excludedBankAccountId)
            )
            .ifPresent(existing -> {
                existing.setDefaultAccount(false);
                bankAccountRepository.save(existing);
            });
    }

    private String normalizeIban(
        String iban
    ) {
        return iban
            .replaceAll("\\s+", "")
            .toUpperCase(Locale.ROOT);
    }

    private void validateId(
        Long id,
        String fieldName
    ) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException(
                fieldName
                    + " muss größer als 0 sein."
            );
        }
    }
}
