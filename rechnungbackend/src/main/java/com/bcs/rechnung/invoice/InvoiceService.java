package com.bcs.rechnung.invoice;

import com.bcs.rechnung.invoice.dto.InvoiceDto;
import com.bcs.rechnung.invoice.dto.InvoiceRequest;

import java.util.List;

public interface InvoiceService {

    List<InvoiceDto> findAll();

    List<InvoiceDto> findByStatus(
        InvoiceStatus status
    );

    List<InvoiceDto> findByCompanyId(
        Long companyId
    );

    List<InvoiceDto> findByCustomerId(
        Long customerId
    );

    InvoiceDto findById(
        Long id
    );

    InvoiceDto create(
        InvoiceRequest request
    );

    InvoiceDto complete(
        Long id
    );

    InvoiceDto markAsSent(
        Long id
    );

    InvoiceDto markAsPaid(
        Long id
    );

    InvoiceDto cancel(
        Long id
    );

    void deleteDraft(
        Long id
    );
}
