package com.bcs.rechnung.customer;

import com.bcs.rechnung.customer.dto.CustomerDto;
import com.bcs.rechnung.customer.dto.CustomerRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST-Controller für die Verwaltung von Kunden.
 *
 * <p>
 * Stellt die REST-Endpunkte für das Lesen, Anlegen,
 * Ändern, Aktivieren, Deaktivieren und Löschen
 * von Kunden bereit.
 * </p>
 *
 * <p>Basis-URL:</p>
 *
 * <pre>
 * /api/customers
 * </pre>
 *
 * <p>Unterstützte Endpunkte:</p>
 *
 * <pre>
 * GET    /api/customers
 * GET    /api/customers/active
 * GET    /api/customers/{id}
 * GET    /api/customers/company/{companyId}
 *
 * POST   /api/customers
 *
 * PUT    /api/customers/{id}
 * PUT    /api/customers/{id}/activate
 * PUT    /api/customers/{id}/deactivate
 *
 * DELETE /api/customers/{id}
 * </pre>
 *
 * <p>
 * Fehlerantworten wie Validierungsfehler, nicht gefundene
 * Ressourcen, fachliche Konflikte und interne Fehler werden
 * zentral über den GlobalExceptionHandler verarbeitet.
 * </p>
 */
@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
@Tag(
    name = "Customer",
    description = "Verwaltung von Kunden"
)
public class CustomerController {

    private final CustomerService customerService;


    /**
     * Liefert alle vorhandenen Kunden.
     *
     * GET /api/customers
     *
     * @return Liste aller Kunden
     */
    @GetMapping
    @Operation(
        summary = "Alle Kunden lesen",
        description = "Liefert alle im System vorhandenen Kunden."
    )
    @ApiResponse(
        responseCode = "200",
        description = "Kunden wurden erfolgreich gelesen"
    )
    public List<CustomerDto> findAll() {

        return customerService.findAll();
    }


    /**
     * Liefert alle aktiven Kunden.
     *
     * GET /api/customers/active
     *
     * @return Liste aller aktiven Kunden
     */
    @GetMapping("/active")
    @Operation(
        summary = "Alle aktiven Kunden lesen",
        description = "Liefert alle Kunden mit dem Status aktiv."
    )
    @ApiResponse(
        responseCode = "200",
        description = "Aktive Kunden wurden erfolgreich gelesen"
    )
    public List<CustomerDto> findActive() {

        return customerService.findActive();
    }


    /**
     * Liefert einen Kunden anhand seiner ID.
     *
     * GET /api/customers/{id}
     *
     * @param id eindeutige ID des Kunden
     * @return gefundener Kunde
     */
    @GetMapping("/{id}")
    @Operation(
        summary = "Kunde über ID lesen",
        description = "Liefert einen Kunden anhand seiner eindeutigen Kunden-ID."
    )
    @ApiResponse(
        responseCode = "200",
        description = "Kunde wurde erfolgreich gelesen"
    )
    public CustomerDto findById(

        @Parameter(
            description = "Eindeutige ID des Kunden",
            example = "1",
            required = true
        )
        @PathVariable Long id

    ) {

        return customerService.findById(id);
    }


    /**
     * Liefert alle Kunden einer Firma.
     *
     * GET /api/customers/company/{companyId}
     *
     * @param companyId eindeutige ID der Firma
     * @return Liste der Kunden dieser Firma
     */
    @GetMapping("/company/{companyId}")
    @Operation(
        summary = "Kunden einer Firma lesen",
        description = "Liefert alle Kunden, die einer bestimmten Firma zugeordnet sind."
    )
    @ApiResponse(
        responseCode = "200",
        description = "Kunden der Firma wurden erfolgreich gelesen"
    )
    public List<CustomerDto> findByCompanyId(

        @Parameter(
            description = "Eindeutige ID der Firma",
            example = "1",
            required = true
        )
        @PathVariable Long companyId

    ) {

        return customerService.findByCompanyId(companyId);
    }


    /**
     * Legt einen neuen Kunden an.
     *
     * POST /api/customers
     *
     * <p>
     * Die Daten werden entsprechend der Validierungsregeln
     * des CustomerRequest geprüft.
     * </p>
     *
     * @param request Daten des neuen Kunden
     * @return neu angelegter Kunde
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
        summary = "Neuen Kunden anlegen",
        description = "Legt einen neuen Kunden anhand der übergebenen Kundendaten an."
    )
    @ApiResponse(
        responseCode = "201",
        description = "Kunde wurde erfolgreich angelegt"
    )
    public CustomerDto create(

        @Valid
        @RequestBody CustomerRequest request

    ) {

        return customerService.create(request);
    }


    /**
     * Aktualisiert einen bestehenden Kunden.
     *
     * PUT /api/customers/{id}
     *
     * @param id ID des zu aktualisierenden Kunden
     * @param request neue Kundendaten
     * @return aktualisierter Kunde
     */
    @PutMapping("/{id}")
    @Operation(
        summary = "Kunden aktualisieren",
        description = "Aktualisiert einen vorhandenen Kunden anhand seiner ID."
    )
    @ApiResponse(
        responseCode = "200",
        description = "Kunde wurde erfolgreich aktualisiert"
    )
    public CustomerDto update(

        @Parameter(
            description = "Eindeutige ID des Kunden",
            example = "1",
            required = true
        )
        @PathVariable Long id,

        @Valid
        @RequestBody CustomerRequest request

    ) {

        return customerService.update(
            id,
            request
        );
    }


    /**
     * Aktiviert einen Kunden.
     *
     * PUT /api/customers/{id}/activate
     *
     * @param id ID des zu aktivierenden Kunden
     * @return aktualisierter Kunde
     */
    @PutMapping("/{id}/activate")
    @Operation(
        summary = "Kunden aktivieren",
        description = "Setzt den Status eines Kunden auf aktiv."
    )
    @ApiResponse(
        responseCode = "200",
        description = "Kunde wurde erfolgreich aktiviert"
    )
    public CustomerDto activate(

        @Parameter(
            description = "Eindeutige ID des Kunden",
            example = "1",
            required = true
        )
        @PathVariable Long id

    ) {

        return customerService.activate(id);
    }


    /**
     * Deaktiviert einen Kunden.
     *
     * PUT /api/customers/{id}/deactivate
     *
     * @param id ID des zu deaktivierenden Kunden
     * @return aktualisierter Kunde
     */
    @PutMapping("/{id}/deactivate")
    @Operation(
        summary = "Kunden deaktivieren",
        description = "Setzt den Status eines Kunden auf inaktiv."
    )
    @ApiResponse(
        responseCode = "200",
        description = "Kunde wurde erfolgreich deaktiviert"
    )
    public CustomerDto deactivate(

        @Parameter(
            description = "Eindeutige ID des Kunden",
            example = "1",
            required = true
        )
        @PathVariable Long id

    ) {

        return customerService.deactivate(id);
    }


    /**
     * Löscht einen Kunden.
     *
     * DELETE /api/customers/{id}
     *
     * @param id ID des zu löschenden Kunden
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
        summary = "Kunden löschen",
        description = "Löscht einen vorhandenen Kunden anhand seiner ID."
    )
    @ApiResponse(
        responseCode = "204",
        description = "Kunde wurde erfolgreich gelöscht"
    )
    public void delete(

        @Parameter(
            description = "Eindeutige ID des Kunden",
            example = "1",
            required = true
        )
        @PathVariable Long id

    ) {

        customerService.delete(id);
    }
}
