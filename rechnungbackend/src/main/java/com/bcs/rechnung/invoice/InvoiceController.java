package com.bcs.rechnung.invoice;

import com.bcs.rechnung.invoice.dto.InvoiceDto;
import com.bcs.rechnung.invoice.dto.InvoiceRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

// pdf
import com.bcs.rechnung.invoice.pdf.InvoicePdfService;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;

import java.util.List;

@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
@Tag(
    name = "Invoice",
    description = "Verwaltung von Rechnungen"
)
public class InvoiceController {

    private final InvoiceService invoiceService;
    private final InvoicePdfService invoicePdfService;

    @GetMapping
    @Operation(summary = "Alle Rechnungen lesen")
    public List<InvoiceDto> findAll() {
        return invoiceService.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Rechnung über ID lesen")
    public InvoiceDto findById(
        @PathVariable Long id
    ) {
        return invoiceService.findById(id);
    }

    @GetMapping("/status")
    @Operation(summary = "Rechnungen nach Status lesen")
    public List<InvoiceDto> findByStatus(
        @RequestParam InvoiceStatus status
    ) {
        return invoiceService.findByStatus(status);
    }

    @GetMapping("/company/{companyId}")
    @Operation(summary = "Rechnungen einer Firma lesen")
    public List<InvoiceDto> findByCompanyId(
        @PathVariable Long companyId
    ) {
        return invoiceService.findByCompanyId(companyId);
    }

    @GetMapping("/customer/{customerId}")
    @Operation(summary = "Rechnungen eines Kunden lesen")
    public List<InvoiceDto> findByCustomerId(
        @PathVariable Long customerId
    ) {
        return invoiceService.findByCustomerId(customerId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Rechnungsentwurf anlegen")
    public InvoiceDto create(
        @Valid
        @RequestBody InvoiceRequest request
    ) {
        return invoiceService.create(request);
    }

    @PostMapping("/{id}/complete")
    @Operation(summary = "Rechnung abschließen")
    public InvoiceDto complete(
        @PathVariable Long id
    ) {
        return invoiceService.complete(id);
    }

    @PostMapping("/{id}/sent")
    @Operation(summary = "Rechnung als versendet markieren")
    public InvoiceDto markAsSent(
        @PathVariable Long id
    ) {
        return invoiceService.markAsSent(id);
    }

    @PostMapping("/{id}/paid")
    @Operation(summary = "Rechnung als bezahlt markieren")
    public InvoiceDto markAsPaid(
        @PathVariable Long id
    ) {
        return invoiceService.markAsPaid(id);
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "Rechnung stornieren")
    public InvoiceDto cancel(
        @PathVariable Long id
    ) {
        return invoiceService.cancel(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Rechnungsentwurf löschen")
    public void deleteDraft(
        @PathVariable Long id
    ) {
        invoiceService.deleteDraft(id);
    }

    @GetMapping("/{id}/pdf")
    @Operation(
        summary = "Rechnung als PDF herunterladen"
    )
    public ResponseEntity<byte[]> downloadPdf(
        @PathVariable Long id
    ) {

        byte[] pdf =
            invoicePdfService.generateInvoicePdf(id);

        return ResponseEntity.ok()
            .header(
                HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=rechnung-" + id + ".pdf"
            )
            .contentType(
                MediaType.APPLICATION_PDF
            )
            .body(pdf);
    }
}
