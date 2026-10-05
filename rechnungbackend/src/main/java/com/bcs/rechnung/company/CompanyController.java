package com.bcs.rechnung.company;

import com.bcs.rechnung.company.dto.CompanyDto;
import com.bcs.rechnung.company.dto.CompanyRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/companies")
@RequiredArgsConstructor
@Tag(
    name = "Company",
    description = "Verwaltung von Firmen"
)
public class CompanyController {

    private final CompanyService companyService;

    @GetMapping
    @Operation(summary = "Alle Firmen lesen")
    public List<CompanyDto> findAll() {
        return companyService.findAll();
    }

    @GetMapping("/active")
    @Operation(summary = "Alle aktiven Firmen lesen")
    public List<CompanyDto> findActive() {
        return companyService.findActive();
    }

    @GetMapping("/default")
    @Operation(summary = "Standardfirma lesen")
    public CompanyDto findDefaultCompany() {
        return companyService.findDefaultCompany();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Firma über ID lesen")
    public CompanyDto findById(
        @PathVariable Long id
    ) {
        return companyService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Neue Firma anlegen")
    public CompanyDto create(
        @Valid
        @RequestBody CompanyRequest request
    ) {
        return companyService.create(request);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Firma aktualisieren")
    public CompanyDto update(
        @PathVariable Long id,
        @Valid
        @RequestBody CompanyRequest request
    ) {
        return companyService.update(
            id,
            request
        );
    }

    @PutMapping("/{id}/activate")
    @Operation(summary = "Firma aktivieren")
    public CompanyDto activate(
        @PathVariable Long id
    ) {
        return companyService.activate(id);
    }

    @PutMapping("/{id}/deactivate")
    @Operation(summary = "Firma deaktivieren")
    public CompanyDto deactivate(
        @PathVariable Long id
    ) {
        return companyService.deactivate(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Firma löschen")
    public void delete(
        @PathVariable Long id
    ) {
        companyService.delete(id);
    }
}
