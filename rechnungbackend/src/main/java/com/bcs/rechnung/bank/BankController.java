package com.bcs.rechnung.bank;

import com.bcs.rechnung.bank.dto.BankDto;
import com.bcs.rechnung.bank.dto.BankRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//Passende URLs
//GET /api/banks
//GET /api/banks/1
//POST /api/banks
//PUT /api/banks/1
//DELETE /api/banks/1
//GET /api/banks/active
//``
@RestController
@RequestMapping("/api/banks")
@RequiredArgsConstructor
public class BankController {

    private final BankService bankService;

    @GetMapping
    public List<BankDto> findAll() {

        return bankService.findAll();
    }

    @GetMapping("/{id}")
    public BankDto findById(
        @PathVariable Long id
    ) {

        return bankService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BankDto create(
        @Valid
        @RequestBody BankRequest request
    ) {

        return bankService.create(request);
    }

    @PutMapping("/{id}")
    public BankDto update(
        @PathVariable Long id,
        @Valid
        @RequestBody BankRequest request
    ) {

        return bankService.update(
            id,
            request
        );
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
        @PathVariable Long id
    ) {

        bankService.delete(id);
    }

    @GetMapping("/active")
    public List<BankDto> findActive() {

        return bankService.findAll()
            .stream()
            .filter(BankDto::active)
            .toList();
    }
}
