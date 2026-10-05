package com.bcs.rechnung.product;

import com.bcs.rechnung.product.dto.ProductDto;
import com.bcs.rechnung.product.dto.ProductRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//REST-Endpunkte
//    HTTP
//GET /api/products
//GET /api/products/active
//GET /api/products/{id}
//GET /api/products/company/{companyId}
//POST /api/products
//PUT /api/products/{id}
//PUT /api/products/{id}/activate
//PUT /api/products/{id}/deactivate

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(
    name = "Product",
    description = "Verwaltung von Produkten"
)
public class ProductController {

    private final ProductService productService;

    @GetMapping
    @Operation(summary = "Alle Produkte lesen")
    public List<ProductDto> findAll() {
        return productService.findAll();
    }

    @GetMapping("/active")
    @Operation(summary = "Alle aktiven Produkte lesen")
    public List<ProductDto> findActive() {
        return productService.findActive();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Produkt über ID lesen")
    public ProductDto findById(
        @PathVariable Long id
    ) {
        return productService.findById(id);
    }

    @GetMapping("/company/{companyId}")
    @Operation(summary = "Produkte einer Firma lesen")
    public List<ProductDto> findByCompanyId(
        @PathVariable Long companyId
    ) {
        return productService.findByCompanyId(
            companyId
        );
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Neues Produkt anlegen")
    public ProductDto create(
        @Valid
        @RequestBody ProductRequest request
    ) {
        return productService.create(request);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Produkt aktualisieren")
    public ProductDto update(
        @PathVariable Long id,
        @Valid
        @RequestBody ProductRequest request
    ) {
        return productService.update(
            id,
            request
        );
    }

    @PutMapping("/{id}/activate")
    @Operation(summary = "Produkt aktivieren")
    public ProductDto activate(
        @PathVariable Long id
    ) {
        return productService.activate(id);
    }

    @PutMapping("/{id}/deactivate")
    @Operation(summary = "Produkt deaktivieren")
    public ProductDto deactivate(
        @PathVariable Long id
    ) {
        return productService.deactivate(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Produkt löschen")
    public void delete(
        @PathVariable Long id
    ) {
        productService.delete(id);
    }
}
