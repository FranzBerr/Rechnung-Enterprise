package com.bcs.rechnung.bank;

import com.bcs.rechnung.bank.dto.BankDto;
import com.bcs.rechnung.bank.dto.BankRequest;
import com.bcs.rechnung.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class BankServiceImpl implements BankService {

    private final BankRepository bankRepository;

    private final BankMapper bankMapper;

    @Override
    @Transactional(readOnly = true)
    public List<BankDto> findAll() {

        return bankRepository.findAll()
            .stream()
            .map(bankMapper::toDto)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public BankDto findById(Long id) {

        BankEntity bank =
            bankRepository.findById(id)
                .orElseThrow(() ->
                    new ResourceNotFoundException(
                        "Bank mit ID %d wurde nicht gefunden."
                            .formatted(id)
                    )
                );

        return bankMapper.toDto(bank);
    }

    @Override
    public BankDto create(
        BankRequest request
    ) {
        validateBankRequest(request);

        if (bankRepository.existsByBicIgnoreCase(
            request.bic()
        )) {

            throw new IllegalArgumentException(
                "Eine Bank mit der BIC "
                    + request.bic()
                    + " existiert bereits."
            );
        }

        BankEntity bank =
            bankMapper.toEntity(request);

        bank =
            bankRepository.save(bank);

        return bankMapper.toDto(bank);
    }

    @Override
    public BankDto update(
        Long id,
        BankRequest request
    ) {
        validateBankRequest(request);

        BankEntity bank =
            bankRepository.findById(id)
                .orElseThrow(() ->
                    new ResourceNotFoundException(
                        "Bank mit ID %d wurde nicht gefunden."
                            .formatted(id)
                    )
                );

        boolean bicChanged =
            !bank.getBic()
                .equalsIgnoreCase(
                    request.bic()
                );

        if (bicChanged
            && bankRepository.existsByBicIgnoreCase(
            request.bic()
        )) {

            throw new IllegalArgumentException(
                "Eine Bank mit der BIC "
                    + request.bic()
                    + " existiert bereits."
            );
        }

        bank.setBankCode(
            request.bankCode()
        );

        bank.setBankName(
            request.bankName()
        );

        bank.setBic(
            request.bic()
        );

        bank.setCountry(
            request.country()
        );

        bank.setActive(
            request.active()
        );

        bank =
            bankRepository.save(bank);

        return bankMapper.toDto(bank);
    }

    @Override
    public void delete(
        Long id
    ) {
        BankEntity bank =
            bankRepository.findById(id)
                .orElseThrow(() ->
                    new ResourceNotFoundException(
                        "Bank mit ID %d wurde nicht gefunden."
                            .formatted(id)
                    )
                );

        bankRepository.delete(bank);
    }

    private void validateBankRequest(
        BankRequest request
    ) {
        if (request == null) {
            throw new IllegalArgumentException(
                "BankRequest darf nicht null sein."
            );
        }

        if (request.bankName() == null
            || request.bankName().isBlank()) {

            throw new IllegalArgumentException(
                "Bankname darf nicht leer sein."
            );
        }

        if (request.bic() == null
            || request.bic().isBlank()) {

            throw new IllegalArgumentException(
                "BIC darf nicht leer sein."
            );
        }
    }
}
