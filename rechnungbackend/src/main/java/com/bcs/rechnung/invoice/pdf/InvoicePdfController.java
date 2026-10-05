package com.bcs.rechnung.invoice.pdf;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
@Tag(
    name = "PDF",
    description = "PDF-Erzeugung"
)
public class InvoicePdfController {

    private final InvoicePdfService service;

    @GetMapping(
        value = "/{id}/pdf",
        produces = MediaType.APPLICATION_PDF_VALUE
    )
    public byte[] createPdf(
        @PathVariable Long id
    ) {
        return service.generateInvoicePdf(id);
    }
}
