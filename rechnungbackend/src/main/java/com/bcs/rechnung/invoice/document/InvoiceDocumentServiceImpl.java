package com.bcs.rechnung.invoice.document;

import com.bcs.rechnung.exception.ResourceNotFoundException;
import com.bcs.rechnung.invoice.InvalidXRechnungException;
import com.bcs.rechnung.invoice.InvoiceEntity;
import com.bcs.rechnung.invoice.InvoiceRepository;
import com.bcs.rechnung.invoice.pdf.GeneratedInvoicePdf;
import com.bcs.rechnung.invoice.pdf.InvoicePdfService;
import com.bcs.rechnung.invoice.pdf.PdfProperties;
import com.bcs.rechnung.invoice.xrechnung.GeneratedXRechnung;
import com.bcs.rechnung.invoice.xrechnung.InvoiceXmlService;
import com.bcs.rechnung.invoice.xrechnung.XRechnungProperties;
import com.bcs.rechnung.util.FilenameUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.Comparator;
import java.util.UUID;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class InvoiceDocumentServiceImpl
    implements InvoiceDocumentService {

    private static final String VALIDATION_DIRECTORY =
        "validation";

    private static final String VALIDATOR_LOG_FILE =
        "validator-console.log";

    private final InvoiceRepository invoiceRepository;

    private final InvoicePdfService invoicePdfService;

    private final InvoiceXmlService invoiceXmlService;

    private final KositValidationService validationService;

    private final InvoiceDocumentProperties properties;

    private final PdfProperties pdfProperties;

    private final XRechnungProperties xRechnungProperties;

    private final InvoiceDocumentArchiveRepository archiveRepository;

    @Override
    @Transactional
    public ArchivedInvoiceDocuments generateValidateAndArchive(
        Long invoiceId
    ) {
        validateInvoiceId(
            invoiceId
        );

        InvoiceEntity invoice =
            findInvoice(
                invoiceId
            );

        validateNotArchived(
            invoice
        );

        GeneratedXRechnung generatedXml =
            invoiceXmlService.generateXml(
                invoiceId
            );

        GeneratedInvoicePdf generatedPdf =
            invoicePdfService.generateInvoicePdf(
                invoiceId
            );

        verifyInvoiceNumbers(
            generatedXml.invoiceNumber(),
            generatedPdf.invoiceNumber()
        );

        String invoiceNumber =
            normalizeInvoiceNumber(
                generatedXml.invoiceNumber()
            );

        String xmlFileName =
            FilenameUtils.xmlFileName(
                invoiceNumber
            );

        String pdfFileName =
            FilenameUtils.pdfFileName(
                invoiceNumber
            );

        Path workingDirectory =
            createWorkingDirectory(
                invoiceNumber
            );

        boolean preserveWorkingDirectory =
            false;

        try {
            Path temporaryXml =
                workingDirectory.resolve(
                    xmlFileName
                );

            Path temporaryPdf =
                workingDirectory.resolve(
                    pdfFileName
                );

            Files.write(
                temporaryXml,
                generatedXml.content(),
                StandardOpenOption.CREATE_NEW,
                StandardOpenOption.WRITE
            );

            Files.write(
                temporaryPdf,
                generatedPdf.content(),
                StandardOpenOption.CREATE_NEW,
                StandardOpenOption.WRITE
            );

            return validateAndArchive(
                invoice,
                invoiceNumber,
                temporaryPdf,
                temporaryXml,
                workingDirectory
            );

        } catch (InvalidXRechnungException exception) {
            preserveWorkingDirectory =
                true;

            throw exception;

        } catch (IOException exception) {
            preserveWorkingDirectory =
                true;

            throw new IllegalStateException(
                "Die Rechnungsdokumente konnten nicht "
                    + "temporär gespeichert werden.",
                exception
            );

        } catch (RuntimeException exception) {
            preserveWorkingDirectory =
                true;

            throw exception;

        } finally {
            if (!preserveWorkingDirectory) {
                deleteRecursively(
                    workingDirectory
                );
            }
        }
    }

    @Override
    @Transactional
    public ArchivedInvoiceDocuments archiveExistingDocuments(
        Long invoiceId
    ) {
        validateInvoiceId(
            invoiceId
        );

        InvoiceEntity invoice =
            findInvoice(
                invoiceId
            );

        validateNotArchived(
            invoice
        );

        String invoiceNumber =
            normalizeInvoiceNumber(
                invoice.getInvoiceNumber()
            );

        String xmlFileName =
            FilenameUtils.xmlFileName(
                invoiceNumber
            );

        String pdfFileName =
            FilenameUtils.pdfFileName(
                invoiceNumber
            );

        Path sourceXml =
            resolveExistingFile(
                xRechnungProperties.getOutputDirectory(),
                xmlFileName,
                "XRechnung"
            );

        Path sourcePdf =
            resolveExistingFile(
                pdfProperties.getOutputDirectory(),
                pdfFileName,
                "PDF-Rechnung"
            );

        Path workingDirectory =
            createWorkingDirectory(
                invoiceNumber
            );

        boolean preserveWorkingDirectory =
            false;

        try {
            Path temporaryXml =
                workingDirectory.resolve(
                    xmlFileName
                );

            Path temporaryPdf =
                workingDirectory.resolve(
                    pdfFileName
                );

            Files.copy(
                sourceXml,
                temporaryXml,
                StandardCopyOption.REPLACE_EXISTING
            );

            Files.copy(
                sourcePdf,
                temporaryPdf,
                StandardCopyOption.REPLACE_EXISTING
            );

            return validateAndArchive(
                invoice,
                invoiceNumber,
                temporaryPdf,
                temporaryXml,
                workingDirectory
            );

        } catch (InvalidXRechnungException exception) {
            preserveWorkingDirectory =
                true;

            throw exception;

        } catch (IOException exception) {
            preserveWorkingDirectory =
                true;

            throw new IllegalStateException(
                "Die vorhandenen Rechnungsdokumente "
                    + "konnten nicht verarbeitet werden.",
                exception
            );

        } catch (RuntimeException exception) {
            preserveWorkingDirectory =
                true;

            throw exception;

        } finally {
            if (!preserveWorkingDirectory) {
                deleteRecursively(
                    workingDirectory
                );
            }
        }
    }

    private ArchivedInvoiceDocuments validateAndArchive(
        InvoiceEntity invoice,
        String invoiceNumber,
        Path temporaryPdf,
        Path temporaryXml,
        Path workingDirectory
    ) throws IOException {

        Path reportDirectory =
            workingDirectory.resolve(
                VALIDATION_DIRECTORY
            );

        KositValidationResult validation =
            validationService.validate(
                temporaryXml,
                reportDirectory
            );

        if (!validation.valid()) {
            throw new InvalidXRechnungException(
                invoiceNumber,
                validation.errors(),
                validation.warnings(),
                validation.reportFile()
            );
        }

        Path archiveDirectory =
            createArchiveDirectory(
                invoiceNumber
            );

        String pdfFileName =
            FilenameUtils.pdfFileName(
                invoiceNumber
            );

        String xmlFileName =
            FilenameUtils.xmlFileName(
                invoiceNumber
            );

        Path archivedPdf =
            archiveDirectory.resolve(
                pdfFileName
            );

        Path archivedXml =
            archiveDirectory.resolve(
                xmlFileName
            );

        Path archivedReport =
            archiveDirectory.resolve(
                invoiceNumber
                    + "-validation-report.xml"
            );

        Path archivedValidatorLog =
            archiveDirectory.resolve(
                invoiceNumber
                    + "-validator-console.log"
            );

        moveReplacing(
            temporaryPdf,
            archivedPdf
        );

        moveReplacing(
            temporaryXml,
            archivedXml
        );

        Files.copy(
            validation.reportFile(),
            archivedReport,
            StandardCopyOption.REPLACE_EXISTING
        );

        Path temporaryValidatorLog =
            reportDirectory.resolve(
                VALIDATOR_LOG_FILE
            );

        String validatorLogPath =
            null;

        if (Files.isRegularFile(
            temporaryValidatorLog
        )) {
            Files.copy(
                temporaryValidatorLog,
                archivedValidatorLog,
                StandardCopyOption.REPLACE_EXISTING
            );

            validatorLogPath =
                archivedValidatorLog.toString();
        }

        LocalDateTime archivedAt =
            LocalDateTime.now();

        saveArchiveEntry(
            invoice.getId(),
            invoiceNumber,
            archiveDirectory,
            archivedPdf,
            archivedXml,
            archivedReport,
            validatorLogPath,
            archivedAt
        );

        markInvoiceAsArchived(
            invoice,
            archivedAt
        );

        return new ArchivedInvoiceDocuments(
            invoiceNumber,
            archiveDirectory.toString(),
            archivedPdf.toString(),
            archivedXml.toString(),
            archivedReport.toString(),
            true,
            validation.warnings()
        );
    }

    private void saveArchiveEntry(
        Long invoiceId,
        String invoiceNumber,
        Path archiveDirectory,
        Path archivedPdf,
        Path archivedXml,
        Path archivedReport,
        String validatorLogPath,
        LocalDateTime archivedAt
    ) {
        requirePath(
            archiveDirectory,
            "Archivverzeichnis"
        );

        requirePath(
            archivedPdf,
            "Archivierte PDF-Datei"
        );

        requirePath(
            archivedXml,
            "Archivierte XML-Datei"
        );

        requirePath(
            archivedReport,
            "Archivierter Prüfbericht"
        );

        InvoiceDocumentArchiveEntity entity =
            archiveRepository
                .findByInvoiceId(
                    invoiceId
                )
                .orElseGet(
                    InvoiceDocumentArchiveEntity::new
                );

        entity.setInvoiceId(
            invoiceId
        );

        entity.setInvoiceNumber(
            invoiceNumber
        );

        entity.setArchiveDirectory(
            archiveDirectory.toString()
        );

        entity.setPdfFile(
            archivedPdf.toString()
        );

        entity.setXmlFile(
            archivedXml.toString()
        );

        entity.setValidationReport(
            archivedReport.toString()
        );

        entity.setValidatorLog(
            validatorLogPath
        );

        entity.setValid(
            true
        );

        entity.setArchivedAt(
            archivedAt
        );

        archiveRepository.saveAndFlush(
            entity
        );
    }

    private void markInvoiceAsArchived(
        InvoiceEntity invoice,
        LocalDateTime archivedAt
    ) {
        invoice.setArchived(
            true
        );

        invoice.setArchivedAt(
            archivedAt
        );

        invoiceRepository.save(
            invoice
        );
    }

    private InvoiceEntity findInvoice(
        Long invoiceId
    ) {
        return invoiceRepository
            .findById(
                invoiceId
            )
            .orElseThrow(() ->
                new ResourceNotFoundException(
                    "Rechnung mit ID "
                        + invoiceId
                        + " wurde nicht gefunden."
                )
            );
    }

    private void validateNotArchived(
        InvoiceEntity invoice
    ) {
        if (Boolean.TRUE.equals(
            invoice.getArchived()
        )) {
            throw new InvoiceAlreadyArchivedException(
                invoice.getId(),
                invoice.getInvoiceNumber(),
                invoice.getArchivedAt()
            );
        }
    }

    private Path resolveExistingFile(
        String configuredDirectory,
        String fileName,
        String documentType
    ) {
        if (configuredDirectory == null
            || configuredDirectory.isBlank()) {

            throw new IllegalStateException(
                "Das Ausgabeverzeichnis für "
                    + documentType
                    + " ist nicht konfiguriert."
            );
        }

        Path directory =
            Path.of(
                    configuredDirectory
                )
                .toAbsolutePath()
                .normalize();

        Path file =
            directory.resolve(
                    fileName
                )
                .normalize();

        if (!file.startsWith(
            directory
        )) {
            throw new IllegalStateException(
                "Der ermittelte Dateipfad für "
                    + documentType
                    + " ist ungültig."
            );
        }

        if (!Files.isRegularFile(
            file
        )) {
            throw new ResourceNotFoundException(
                documentType
                    + " wurde nicht gefunden: "
                    + file
            );
        }

        if (!Files.isReadable(
            file
        )) {
            throw new IllegalStateException(
                documentType
                    + " ist nicht lesbar: "
                    + file
            );
        }

        return file;
    }

    private Path createWorkingDirectory(
        String invoiceNumber
    ) {
        try {
            Path tempRoot =
                Path.of(
                        properties.getTempDirectory()
                    )
                    .toAbsolutePath()
                    .normalize();

            Files.createDirectories(
                tempRoot
            );

            String directoryName =
                safeDirectoryName(
                    invoiceNumber
                )
                    + "-"
                    + UUID.randomUUID();

            Path workingDirectory =
                tempRoot.resolve(
                        directoryName
                    )
                    .normalize();

            if (!workingDirectory.startsWith(
                tempRoot
            )) {
                throw new IllegalStateException(
                    "Der temporäre Arbeitsordner liegt "
                        + "außerhalb des konfigurierten "
                        + "Verzeichnisses."
                );
            }

            return Files.createDirectory(
                workingDirectory
            );

        } catch (IOException exception) {
            throw new IllegalStateException(
                "Das temporäre Arbeitsverzeichnis "
                    + "konnte nicht angelegt werden.",
                exception
            );
        }
    }

    private Path createArchiveDirectory(
        String invoiceNumber
    ) throws IOException {

        Path archiveRoot =
            Path.of(
                    properties.getArchiveDirectory()
                )
                .toAbsolutePath()
                .normalize();

        Files.createDirectories(
            archiveRoot
        );

        String invoiceYear =
            extractInvoiceYear(
                invoiceNumber
            );

        Path archiveDirectory =
            archiveRoot
                .resolve(
                    invoiceYear
                )
                .resolve(
                    safeDirectoryName(
                        invoiceNumber
                    )
                )
                .normalize();

        if (!archiveDirectory.startsWith(
            archiveRoot
        )) {
            throw new IllegalStateException(
                "Der ermittelte Archivpfad ist ungültig."
            );
        }

        Files.createDirectories(
            archiveDirectory
        );

        return archiveDirectory;
    }

    private void verifyInvoiceNumbers(
        String xmlInvoiceNumber,
        String pdfInvoiceNumber
    ) {
        String normalizedXmlNumber =
            normalizeInvoiceNumber(
                xmlInvoiceNumber
            );

        String normalizedPdfNumber =
            normalizeInvoiceNumber(
                pdfInvoiceNumber
            );

        if (!normalizedXmlNumber.equals(
            normalizedPdfNumber
        )) {
            throw new IllegalStateException(
                "PDF und XRechnung besitzen nicht "
                    + "dieselbe Rechnungsnummer. XML: "
                    + normalizedXmlNumber
                    + ", PDF: "
                    + normalizedPdfNumber
            );
        }
    }

    private String normalizeInvoiceNumber(
        String invoiceNumber
    ) {
        if (invoiceNumber == null
            || invoiceNumber.isBlank()) {

            throw new IllegalStateException(
                "Die Rechnungsnummer fehlt."
            );
        }

        return invoiceNumber.trim();
    }

    private String extractInvoiceYear(
        String invoiceNumber
    ) {
        String[] parts =
            invoiceNumber.split(
                "-"
            );

        for (String part : parts) {
            if (part.matches(
                "(19|20)\\d{2}"
            )) {
                return part;
            }
        }

        return Integer.toString(
            Year.now().getValue()
        );
    }

    private String safeDirectoryName(
        String invoiceNumber
    ) {
        return normalizeInvoiceNumber(
            invoiceNumber
        ).replaceAll(
            "[\\\\/:*?\"<>|]",
            "-"
        );
    }

    private void requirePath(
        Path path,
        String fieldName
    ) {
        if (path == null) {
            throw new IllegalStateException(
                fieldName
                    + " darf nicht null sein."
            );
        }
    }

    private void moveReplacing(
        Path source,
        Path target
    ) throws IOException {
        try {
            Files.move(
                source,
                target,
                StandardCopyOption.REPLACE_EXISTING,
                StandardCopyOption.ATOMIC_MOVE
            );

        } catch (
            AtomicMoveNotSupportedException exception
        ) {
            Files.move(
                source,
                target,
                StandardCopyOption.REPLACE_EXISTING
            );
        }
    }

    private void deleteRecursively(
        Path directory
    ) {
        if (directory == null
            || !Files.exists(
            directory
        )) {

            return;
        }

        try (
            Stream<Path> paths =
                Files.walk(
                    directory
                )
        ) {
            paths
                .sorted(
                    Comparator.reverseOrder()
                )
                .forEach(path -> {
                    try {
                        Files.deleteIfExists(
                            path
                        );
                    } catch (IOException ignored) {
                    }
                });

        } catch (IOException ignored) {
        }
    }

    private void validateInvoiceId(
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
