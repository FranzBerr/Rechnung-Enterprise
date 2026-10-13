package com.bcs.rechnung.invoice.document.mapper;

import com.bcs.rechnung.invoice.document.InvoiceDocumentArchiveEntity;
import com.bcs.rechnung.invoice.document.dto.ArchivedDocumentDto;
import org.springframework.stereotype.Component;

@Component
public class InvoiceDocumentArchiveMapper {

    public ArchivedDocumentDto toDto(
        InvoiceDocumentArchiveEntity entity
    ) {
        return new ArchivedDocumentDto(
            entity.getId(),
            entity.getInvoiceId(),
            entity.getInvoiceNumber(),
            entity.getArchiveDirectory(),
            entity.getPdfFile(),
            entity.getXmlFile(),
            entity.getValidationReport(),
            entity.getValidatorLog(),
            entity.getValid(),
            entity.getArchivedAt()
        );
    }
}
