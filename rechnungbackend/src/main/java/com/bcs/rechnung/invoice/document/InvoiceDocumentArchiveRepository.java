package com.bcs.rechnung.invoice.document;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InvoiceDocumentArchiveRepository
    extends JpaRepository<InvoiceDocumentArchiveEntity, Long> {

    Optional<InvoiceDocumentArchiveEntity> findByInvoiceId(
        Long invoiceId
    );

    Optional<InvoiceDocumentArchiveEntity> findByInvoiceNumber(
        String invoiceNumber
    );

    List<InvoiceDocumentArchiveEntity>
    findAllByOrderByArchivedAtDesc();

    boolean existsByInvoiceId(
        Long invoiceId
    );
}
