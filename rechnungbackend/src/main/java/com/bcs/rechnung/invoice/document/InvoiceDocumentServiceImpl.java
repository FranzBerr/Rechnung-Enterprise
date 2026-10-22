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
import java.util.Comparator;
import java.util.UUID;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class InvoiceDocumentServiceImpl
    implements InvoiceDocumentService {

    private static final String INVOICE_DIRECTORY =
        "Rechnungen";

    private static final String VALIDATION_DIRECTORY =
        "validation";

    private static final String VALIDATION_REPORT_FILE =
        "validation-report.xml";

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
        validateInvoiceId(invoiceId);

        InvoiceEntity invoice =
            findInvoice(invoiceId);

        validateNotArchived(invoice);

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

            writeTemporaryFile(
                temporaryXml,
                generatedXml.content(),
                "XRechnung"
            );

            writeTemporaryFile(
                temporaryPdf,
                generatedPdf.content(),
                "PDF-Rechnung"
            );

            return validateAndArchive(
                invoice,
                invoiceNumber,
                temporaryPdf,
                temporaryXml,
                workingDirectory
            );

        } catch (InvalidXRechnungException exception) {
            preserveWorkingDirectory = true;
            throw exception;

        } catch (IOException exception) {
            preserveWorkingDirectory = true;

            throw new IllegalStateException(
                "Die Rechnungsdokumente konnten nicht "
                    + "temporär gespeichert werden.",
                exception
            );

        } catch (RuntimeException exception) {
            preserveWorkingDirectory = true;
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
        validateInvoiceId(invoiceId);

        InvoiceEntity invoice =
            findInvoice(invoiceId);

        validateNotArchived(invoice);

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
            preserveWorkingDirectory = true;
            throw exception;

        } catch (IOException exception) {
            preserveWorkingDirectory = true;

            throw new IllegalStateException(
                "Die vorhandenen Rechnungsdokumente "
                    + "konnten nicht verarbeitet werden.",
                exception
            );

        } catch (RuntimeException exception) {
            preserveWorkingDirectory = true;
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
                invoice
            );

        ArchiveFiles archiveFiles =
            createArchiveFiles(
                invoiceNumber,
                archiveDirectory
            );

        moveReplacing(
            temporaryPdf,
            archiveFiles.pdf()
        );

        moveReplacing(
            temporaryXml,
            archiveFiles.xml()
        );

        copyRequiredFile(
            validation.reportFile(),
            archiveFiles.validationReport(),
            "KoSIT-Prüfbericht"
        );

        Path temporaryValidatorLog =
            reportDirectory.resolve(
                VALIDATOR_LOG_FILE
            );

        copyRequiredFile(
            temporaryValidatorLog,
            archiveFiles.validatorLog(),
            "Validator-Protokoll"
        );

        validateArchiveFiles(
            archiveFiles
        );

        LocalDateTime archivedAt =
            LocalDateTime.now();

        InvoiceDocumentArchiveEntity archive =
            saveArchiveEntry(
                invoice.getId(),
                invoiceNumber,
                archiveDirectory,
                archiveFiles,
                archivedAt
            );

        markInvoiceAsArchived(
            invoice,
            archive
        );

        return new ArchivedInvoiceDocuments(
            invoiceNumber,
            absolutePath(archiveDirectory),
            absolutePath(archiveFiles.pdf()),
            absolutePath(archiveFiles.xml()),
            absolutePath(
                archiveFiles.validationReport()
            ),
            true,
            validation.warnings()
        );
    }

    private InvoiceDocumentArchiveEntity saveArchiveEntry(
        Long invoiceId,
        String invoiceNumber,
        Path archiveDirectory,
        ArchiveFiles archiveFiles,
        LocalDateTime archivedAt
    ) {
        requirePath(
            archiveDirectory,
            "Archivverzeichnis"
        );

        requirePath(
            archiveFiles.pdf(),
            "Archivierte PDF-Datei"
        );

        requirePath(
            archiveFiles.xml(),
            "Archivierte XML-Datei"
        );

        requirePath(
            archiveFiles.validationReport(),
            "Archivierter Prüfbericht"
        );

        requirePath(
            archiveFiles.validatorLog(),
            "Archiviertes Validator-Protokoll"
        );

        InvoiceDocumentArchiveEntity entity =
            archiveRepository
                .findByInvoiceId(invoiceId)
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
            absolutePath(archiveDirectory)
        );

        entity.setPdfFile(
            absolutePath(
                archiveFiles.pdf()
            )
        );

        entity.setXmlFile(
            absolutePath(
                archiveFiles.xml()
            )
        );

        entity.setValidationReport(
            absolutePath(
                archiveFiles.validationReport()
            )
        );

        entity.setValidatorLog(
            absolutePath(
                archiveFiles.validatorLog()
            )
        );

        entity.setValid(true);

        entity.setArchivedAt(
            archivedAt
        );

        return archiveRepository.saveAndFlush(
            entity
        );
    }

    private void markInvoiceAsArchived(
        InvoiceEntity invoice,
        InvoiceDocumentArchiveEntity archive
    ) {
        invoice.setArchived(true);

        invoice.setArchivedAt(
            archive.getArchivedAt()
        );

        invoice.setArchivePath(
            archive.getArchiveDirectory()
        );

        invoiceRepository.saveAndFlush(
            invoice
        );
    }

    private InvoiceEntity findInvoice(
        Long invoiceId
    ) {
        return invoiceRepository
            .findById(invoiceId)
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
        if (
            Boolean.TRUE.equals(
                invoice.getArchived()
            )
        ) {
            throw new InvoiceAlreadyArchivedException(
                invoice.getId(),
                invoice.getInvoiceNumber(),
                invoice.getArchivedAt()
            );
        }

        if (
            archiveRepository.existsByInvoiceId(
                invoice.getId()
            )
        ) {
            throw new IllegalStateException(
                "Für die Rechnung "
                    + invoice.getInvoiceNumber()
                    + " existiert bereits ein Archivdatensatz."
            );
        }
    }

    private Path createArchiveDirectory(
        InvoiceEntity invoice
    ) throws IOException {

        if (invoice.getInvoiceDate() == null) {
            throw new IllegalStateException(
                "Die Rechnung besitzt kein Rechnungsdatum."
            );
        }

        String invoiceNumber =
            normalizeInvoiceNumber(
                invoice.getInvoiceNumber()
            );

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
            String.valueOf(
                invoice.getInvoiceDate().getYear()
            );

        Path yearDirectory =
            archiveRoot
                .resolve(invoiceYear)
                .normalize();

        Path invoiceTypeDirectory =
            yearDirectory
                .resolve(INVOICE_DIRECTORY)
                .normalize();

        Path archiveDirectory =
            invoiceTypeDirectory
                .resolve(
                    safeDirectoryName(
                        invoiceNumber
                    )
                )
                .normalize();

        if (!yearDirectory.startsWith(archiveRoot)) {
            throw new IllegalStateException(
                "Das ermittelte Jahresverzeichnis "
                    + "liegt außerhalb des Archivs."
            );
        }

        if (
            !invoiceTypeDirectory.startsWith(
                archiveRoot
            )
        ) {
            throw new IllegalStateException(
                "Das Rechnungsverzeichnis liegt "
                    + "außerhalb des Archivs."
            );
        }

        if (
            !archiveDirectory.startsWith(
                invoiceTypeDirectory
            )
        ) {
            throw new IllegalStateException(
                "Der ermittelte Rechnungsarchivpfad "
                    + "ist ungültig."
            );
        }

        Files.createDirectories(
            archiveDirectory
        );

        return archiveDirectory;
    }

    private ArchiveFiles createArchiveFiles(
        String invoiceNumber,
        Path archiveDirectory
    ) {
        String normalizedInvoiceNumber =
            normalizeInvoiceNumber(
                invoiceNumber
            );

        String pdfFileName =
            FilenameUtils.pdfFileName(
                normalizedInvoiceNumber
            );

        String xmlFileName =
            FilenameUtils.xmlFileName(
                normalizedInvoiceNumber
            );

        return new ArchiveFiles(
            archiveDirectory.resolve(
                pdfFileName
            ),
            archiveDirectory.resolve(
                xmlFileName
            ),
            archiveDirectory.resolve(
                VALIDATION_REPORT_FILE
            ),
            archiveDirectory.resolve(
                VALIDATOR_LOG_FILE
            )
        );
    }

    private void validateArchiveFiles(
        ArchiveFiles files
    ) {
        validateArchiveFile(
            files.pdf(),
            "PDF-Datei"
        );

        validateArchiveFile(
            files.xml(),
            "XRechnung-XML"
        );

        validateArchiveFile(
            files.validationReport(),
            "KoSIT-Prüfbericht"
        );

        validateArchiveFile(
            files.validatorLog(),
            "Validator-Protokoll"
        );
    }

    private void validateArchiveFile(
        Path file,
        String documentType
    ) {
        requirePath(
            file,
            documentType
        );

        if (!Files.isRegularFile(file)) {
            throw new IllegalStateException(
                documentType
                    + " wurde nicht im Archiv erzeugt: "
                    + file
            );
        }

        if (!Files.isReadable(file)) {
            throw new IllegalStateException(
                documentType
                    + " ist nicht lesbar: "
                    + file
            );
        }

        try {
            if (Files.size(file) == 0) {
                throw new IllegalStateException(
                    documentType
                        + " ist leer: "
                        + file
                );
            }

        } catch (IOException exception) {
            throw new IllegalStateException(
                documentType
                    + " konnte nicht geprüft werden: "
                    + file,
                exception
            );
        }
    }

    private void copyRequiredFile(
        Path source,
        Path target,
        String documentType
    ) throws IOException {

        requirePath(
            source,
            documentType
        );

        requirePath(
            target,
            documentType + " Zielpfad"
        );

        if (!Files.isRegularFile(source)) {
            throw new IllegalStateException(
                documentType
                    + " wurde nicht erzeugt: "
                    + source
            );
        }

        if (!Files.isReadable(source)) {
            throw new IllegalStateException(
                documentType
                    + " ist nicht lesbar: "
                    + source
            );
        }

        Files.copy(
            source,
            target,
            StandardCopyOption.REPLACE_EXISTING
        );
    }

    private void writeTemporaryFile(
        Path file,
        byte[] content,
        String documentType
    ) throws IOException {

        if (
            content == null
                || content.length == 0
        ) {
            throw new IllegalStateException(
                documentType
                    + " enthält keine Daten."
            );
        }

        Files.write(
            file,
            content,
            StandardOpenOption.CREATE_NEW,
            StandardOpenOption.WRITE
        );
    }

    private Path resolveExistingFile(
        String configuredDirectory,
        String fileName,
        String documentType
    ) {
        if (
            configuredDirectory == null
                || configuredDirectory.isBlank()
        ) {
            throw new IllegalStateException(
                "Das Ausgabeverzeichnis für "
                    + documentType
                    + " ist nicht konfiguriert."
            );
        }

        Path directory =
            Path.of(configuredDirectory)
                .toAbsolutePath()
                .normalize();

        Path file =
            directory
                .resolve(fileName)
                .normalize();

        if (!file.startsWith(directory)) {
            throw new IllegalStateException(
                "Der ermittelte Dateipfad für "
                    + documentType
                    + " ist ungültig."
            );
        }

        if (!Files.isRegularFile(file)) {
            throw new ResourceNotFoundException(
                documentType
                    + " wurde nicht gefunden: "
                    + file
            );
        }

        if (!Files.isReadable(file)) {
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
        String tempDirectory =
            properties.getTempDirectory();

        if (
            tempDirectory == null
                || tempDirectory.isBlank()
        ) {
            throw new IllegalStateException(
                "Das temporäre Arbeitsverzeichnis "
                    + "ist nicht konfiguriert."
            );
        }

        try {
            Path tempRoot =
                Path.of(tempDirectory)
                    .toAbsolutePath()
                    .normalize();

            Files.createDirectories(
                tempRoot
            );

            String directoryName =
                safeDirectoryName(invoiceNumber)
                    + "-"
                    + UUID.randomUUID();

            Path workingDirectory =
                tempRoot
                    .resolve(directoryName)
                    .normalize();

            if (
                !workingDirectory.startsWith(
                    tempRoot
                )
            ) {
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

        if (
            !normalizedXmlNumber.equals(
                normalizedPdfNumber
            )
        ) {
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
        if (
            invoiceNumber == null
                || invoiceNumber.isBlank()
        ) {
            throw new IllegalStateException(
                "Die Rechnungsnummer fehlt."
            );
        }

        return invoiceNumber.trim();
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

    private String absolutePath(
        Path path
    ) {
        requirePath(
            path,
            "Dateipfad"
        );

        return path
            .toAbsolutePath()
            .normalize()
            .toString();
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

        requirePath(
            source,
            "Quelldatei"
        );

        requirePath(
            target,
            "Zieldatei"
        );

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
        if (
            directory == null
                || !Files.exists(directory)
        ) {
            return;
        }

        try (
            Stream<Path> paths =
                Files.walk(directory)
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
                        // Aufräumfehler verändern das
                        // fachliche Ergebnis nicht.
                    }
                });

        } catch (IOException ignored) {
            // Aufräumfehler verändern das
            // fachliche Ergebnis nicht.
        }
    }

    private void validateInvoiceId(
        Long invoiceId
    ) {
        if (
            invoiceId == null
                || invoiceId <= 0
        ) {
            throw new IllegalArgumentException(
                "Die Rechnungs-ID muss größer als 0 sein."
            );
        }
    }

    private record ArchiveFiles(
        Path pdf,
        Path xml,
        Path validationReport,
        Path validatorLog
    ) {
    }
}
