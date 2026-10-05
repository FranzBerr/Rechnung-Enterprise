package com.bcs.rechnung.invoice;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InvoiceRepository
    extends JpaRepository<InvoiceEntity, Long> {

    Optional<InvoiceEntity> findByInvoiceNumberIgnoreCase(
        String invoiceNumber
    );

    boolean existsByInvoiceNumberIgnoreCase(
        String invoiceNumber
    );

    List<InvoiceEntity> findByStatusOrderByInvoiceDateDesc(
        InvoiceStatus status
    );

    List<InvoiceEntity> findByCompany_IdOrderByInvoiceDateDesc(
        Long companyId
    );

    List<InvoiceEntity> findByCustomer_IdOrderByInvoiceDateDesc(
        Long customerId
    );
}
