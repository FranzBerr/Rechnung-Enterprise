package com.bcs.rechnung.invoice.pdf;

import com.bcs.rechnung.exception.ResourceNotFoundException;
import com.bcs.rechnung.invoice.InvoiceEntity;
import com.bcs.rechnung.invoice.InvoiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InvoicePdfServiceImpl
    implements InvoicePdfService {

    private final InvoiceRepository invoiceRepository;
    private final InvoicePdfGenerator invoicePdfGenerator;

    @Override
    public byte[] generateInvoicePdf(
        Long invoiceId
    ) {

        InvoiceEntity invoice =
            invoiceRepository.findById(invoiceId)
                .orElseThrow(() ->
                    new ResourceNotFoundException(
                        "Rechnung nicht gefunden"
                    )
                );

        try {

            return invoicePdfGenerator.generate(
                invoice
            );

        } catch (IOException e) {

            throw new IllegalStateException(
                "PDF konnte nicht erzeugt werden",
                e
            );
        }
    }

    private void validateId(
        Long invoiceId
    ) {

        if (invoiceId == null
            || invoiceId <= 0) {

            throw new IllegalArgumentException(
                "Die Rechnungs-ID muss größer als 0 sein."
            );
        }
    }
}
