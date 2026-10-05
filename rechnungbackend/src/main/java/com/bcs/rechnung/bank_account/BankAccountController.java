package com.bcs.rechnung.bank_account;

import com.bcs.rechnung.bank_account.dto.BankAccountDto;
import com.bcs.rechnung.bank_account.dto.BankAccountRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bank-accounts")
@RequiredArgsConstructor
public class BankAccountController {

    private final BankAccountService bankAccountService;

    @GetMapping
    public List<BankAccountDto> findAll() {
        return bankAccountService.findAll();
    }

    @GetMapping("/active")
    public List<BankAccountDto> findActive() {
        return bankAccountService.findActive();
    }

    @GetMapping("/{id}")
    public BankAccountDto findById(
        @PathVariable Long id
    ) {
        return bankAccountService.findById(id);
    }

    @GetMapping("/company/{companyId}")
    public List<BankAccountDto> findByCompanyId(
        @PathVariable Long companyId
    ) {
        return bankAccountService.findByCompanyId(companyId);
    }

    @GetMapping("/company/{companyId}/default")
    public BankAccountDto findDefaultByCompanyId(
        @PathVariable Long companyId
    ) {
        return bankAccountService.findDefaultByCompanyId(companyId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BankAccountDto create(
        @Valid @RequestBody BankAccountRequest request
    ) {
        return bankAccountService.create(request);
    }

    @PutMapping("/{id}")
    public BankAccountDto update(
        @PathVariable Long id,
        @Valid @RequestBody BankAccountRequest request
    ) {
        return bankAccountService.update(
            id,
            request
        );
    }

    @PutMapping("/{id}/activate")
    public BankAccountDto activate(
        @PathVariable Long id
    ) {
        return bankAccountService.activate(id);
    }

    @PutMapping("/{id}/deactivate")
    public BankAccountDto deactivate(
        @PathVariable Long id
    ) {
        return bankAccountService.deactivate(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
        @PathVariable Long id
    ) {
        bankAccountService.delete(id);
    }
}
