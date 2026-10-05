package com.bcs.rechnung.customer;

import com.bcs.rechnung.customer.dto.CustomerDto;
import com.bcs.rechnung.customer.dto.CustomerRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//Unterstützte Endpunkte
//GET    /api/customers
//
//GET    /api/customers/active
//
//GET    /api/customers/{id}
//
//GET    /api/customers/company/{companyId}
//
//POST   /api/customers
//
//PUT    /api/customers/{id}
//
//PUT    /api/customers/{id}/activate
//
//PUT    /api/customers/{id}/deactivate
//
//DELETE /api/customers/{id}

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
@Tag(
    name = "Customer",
    description = "Verwaltung von Kunden"
)
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping
    @Operation(summary = "Alle Kunden lesen")
    public List<CustomerDto> findAll() {
        return customerService.findAll();
    }

    @GetMapping("/active")
    @Operation(summary = "Alle aktiven Kunden lesen")
    public List<CustomerDto> findActive() {
        return customerService.findActive();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Kunde über ID lesen")
    public CustomerDto findById(
        @PathVariable Long id
    ) {
        return customerService.findById(id);
    }

    @GetMapping("/company/{companyId}")
    @Operation(summary = "Kunden einer Firma lesen")
    public List<CustomerDto> findByCompanyId(
        @PathVariable Long companyId
    ) {
        return customerService.findByCompanyId(
            companyId
        );
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Neuen Kunden anlegen")
    public CustomerDto create(
        @Valid
        @RequestBody CustomerRequest request
    ) {
        return customerService.create(request);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Kunden aktualisieren")
    public CustomerDto update(
        @PathVariable Long id,
        @Valid
        @RequestBody CustomerRequest request
    ) {
        return customerService.update(
            id,
            request
        );
    }

    @PutMapping("/{id}/activate")
    @Operation(summary = "Kunden aktivieren")
    public CustomerDto activate(
        @PathVariable Long id
    ) {
        return customerService.activate(id);
    }

    @PutMapping("/{id}/deactivate")
    @Operation(summary = "Kunden deaktivieren")
    public CustomerDto deactivate(
        @PathVariable Long id
    ) {
        return customerService.deactivate(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Kunden löschen")
    public void delete(
        @PathVariable Long id
    ) {
        customerService.delete(id);
    }
}
