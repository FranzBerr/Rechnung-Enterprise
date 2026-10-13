package com.bcs.rechnung.invoice.document;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "invoice_document_archive",
    schema = "dbo"
)
@Getter
@Setter
public class InvoiceDocumentArchiveEntity {

    @Id
    @GeneratedValue(
        strategy = GenerationType.IDENTITY
    )
    @Column(
        name = "id",
        nullable = false
    )
    private Long id;

    @Column(
        name = "invoice_id",
        nullable = false
    )
    private Long invoiceId;

    @Column(
        name = "invoice_number",
        nullable = false,
        length = 50
    )
    private String invoiceNumber;

    @Column(
        name = "archive_directory",
        nullable = false,
        length = 1000
    )
    private String archiveDirectory;

    @Column(
        name = "pdf_file",
        nullable = false,
        length = 1000
    )
    private String pdfFile;

    @Column(
        name = "xml_file",
        nullable = false,
        length = 1000
    )
    private String xmlFile;

    @Column(
        name = "validation_report",
        length = 1000
    )
    private String validationReport;

    @Column(
        name = "validator_log",
        length = 1000
    )
    private String validatorLog;

    @Column(
        name = "valid",
        nullable = false
    )
    private Boolean valid;

    @Column(
        name = "archived_at",
        nullable = false
    )
    private LocalDateTime archivedAt;
}
