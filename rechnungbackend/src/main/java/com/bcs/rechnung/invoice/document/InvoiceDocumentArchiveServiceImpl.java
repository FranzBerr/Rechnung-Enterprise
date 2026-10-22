package com.bcs.rechnung.invoice.document;

import com.bcs.rechnung.exception.ResourceNotFoundException;
import com.bcs.rechnung.invoice.InvoiceEntity;
import com.bcs.rechnung.invoice.InvoiceRepository;
import com.bcs.rechnung.invoice.document.dto.ArchivedDocumentDto;
import com.bcs.rechnung.invoice.document.mapper.InvoiceDocumentArchiveMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InvoiceDocumentArchiveServiceImpl
    implements InvoiceDocumentArchiveService {

    private static final MediaType ZIP_MEDIA_TYPE =
        MediaType.parseMediaType(
            "application/zip"
        );

    private final InvoiceDocumentArchiveRepository repository;
    private final InvoiceDocumentArchiveMapper mapper;
    private final InvoiceDocumentService invoiceDocumentService;
    private final InvoiceRepository invoiceRepository;

    // ---------------------------------------------------------
    // Archivieren
    // ---------------------------------------------------------

    @Override
    @Transactional
    public ArchivedDocumentDto archive(
        Long invoiceId
    ) {
        validateId(
            invoiceId,
            "Rechnungs-ID"
        );

        InvoiceEntity invoice =
            findInvoice(invoiceId);

        /*
         * Bereits archiviert:
         * Vorhandenes Archiv nicht neu erzeugen.
         */
        if (
            Boolean.TRUE.equals(
                invoice.getArchived()
            )
        ) {
            InvoiceDocumentArchiveEntity archive =
                findEntityByInvoiceId(invoiceId);

            validateArchivedFiles(archive);

            synchronizeInvoiceArchiveState(
                invoice,
                archive
            );

            return mapper.toDto(archive);
        }

        /*
         * Archivdatensatz existiert bereits,
         * InvoiceEntity ist aber noch nicht synchron.
         */
        if (
            repository.existsByInvoiceId(
                invoiceId
            )
        ) {
            InvoiceDocumentArchiveEntity archive =
                findEntityByInvoiceId(invoiceId);

            validateArchivedFiles(archive);

            synchronizeInvoiceArchiveState(
                invoice,
                archive
            );

            return mapper.toDto(archive);
        }

        /*
         * Neue Archivierung.
         *
         * InvoiceDocumentService übernimmt:
         *
         * PDF
         * XML
         * KoSIT-Validierung
         * validation-report.xml
         * validator-console.log
         * InvoiceDocumentArchiveEntity
         */
        invoiceDocumentService
            .generateValidateAndArchive(
                invoiceId
            );

        InvoiceDocumentArchiveEntity archive =
            repository
                .findByInvoiceId(invoiceId)
                .orElseThrow(() ->
                    new IllegalStateException(
                        "Die Rechnung "
                            + invoice.getInvoiceNumber()
                            + " wurde verarbeitet, "
                            + "aber es wurde kein "
                            + "Archivdatensatz gespeichert."
                    )
                );

        validateArchivedFiles(archive);

        synchronizeInvoiceArchiveState(
            invoice,
            archive
        );

        return mapper.toDto(archive);
    }

    // ---------------------------------------------------------
    // Archivierung rückgängig machen
    // ---------------------------------------------------------

    @Override
    @Transactional
    public void undoArchive(
        Long invoiceId
    ) {
        validateId(
            invoiceId,
            "Rechnungs-ID"
        );

        InvoiceEntity invoice =
            findInvoice(invoiceId);

        if (
            !Boolean.TRUE.equals(
                invoice.getArchived()
            )
        ) {
            throw new IllegalStateException(
                "Die Rechnung "
                    + invoice.getInvoiceNumber()
                    + " ist nicht archiviert."
            );
        }

        InvoiceDocumentArchiveEntity archive =
            repository
                .findByInvoiceId(invoiceId)
                .orElseThrow(() ->
                    new ResourceNotFoundException(
                        "Für die Rechnung "
                            + invoice.getInvoiceNumber()
                            + " wurde kein "
                            + "Archivdatensatz gefunden."
                    )
                );

        /*
         * Archivdatensatz löschen.
         *
         * Die physischen Dateien bleiben absichtlich
         * erhalten.
         */
        repository.delete(archive);

        invoice.setArchived(false);
        invoice.setArchivedAt(null);
        invoice.setArchivePath(null);

        invoiceRepository.save(invoice);
    }

    // ---------------------------------------------------------
    // Archivstatus
    // ---------------------------------------------------------

    @Override
    public boolean isArchived(
        Long invoiceId
    ) {
        validateId(
            invoiceId,
            "Rechnungs-ID"
        );

        InvoiceEntity invoice =
            findInvoice(invoiceId);

        if (
            !Boolean.TRUE.equals(
                invoice.getArchived()
            )
        ) {
            return false;
        }

        return repository
            .findByInvoiceId(invoiceId)
            .map(this::areArchivedFilesAvailable)
            .orElse(false);
    }

    // ---------------------------------------------------------
    // Lesen
    // ---------------------------------------------------------

    @Override
    public List<ArchivedDocumentDto> findAll() {
        return repository
            .findAllByOrderByArchivedAtDesc()
            .stream()
            .map(mapper::toDto)
            .toList();
    }

    @Override
    public ArchivedDocumentDto findById(
        Long id
    ) {
        return mapper.toDto(
            findEntityById(id)
        );
    }

    @Override
    public ArchivedDocumentDto findByInvoiceId(
        Long invoiceId
    ) {
        return mapper.toDto(
            findEntityByInvoiceId(invoiceId)
        );
    }

    @Override
    public ArchivedDocumentDto findByInvoiceNumber(
        String invoiceNumber
    ) {
        String normalizedInvoiceNumber =
            normalizeInvoiceNumber(
                invoiceNumber
            );

        InvoiceDocumentArchiveEntity entity =
            repository
                .findByInvoiceNumber(
                    normalizedInvoiceNumber
                )
                .orElseThrow(() ->
                    new ResourceNotFoundException(
                        "Für die Rechnungsnummer "
                            + normalizedInvoiceNumber
                            + " wurde kein "
                            + "Archivdatensatz gefunden."
                    )
                );

        return mapper.toDto(entity);
    }

    // ---------------------------------------------------------
    // Downloads über Archiv-ID
    // ---------------------------------------------------------

    @Override
    public ResponseEntity<Resource> downloadPdf(
        Long id
    ) {
        InvoiceDocumentArchiveEntity entity =
            findEntityById(id);

        return createFileDownloadResponse(
            entity.getPdfFile(),
            MediaType.APPLICATION_PDF,
            "PDF-Datei"
        );
    }

    @Override
    public ResponseEntity<Resource> downloadXml(
        Long id
    ) {
        InvoiceDocumentArchiveEntity entity =
            findEntityById(id);

        return createFileDownloadResponse(
            entity.getXmlFile(),
            MediaType.APPLICATION_XML,
            "XRechnung-XML"
        );
    }

    @Override
    public ResponseEntity<Resource> downloadValidationReport(
        Long id
    ) {
        InvoiceDocumentArchiveEntity entity =
            findEntityById(id);

        return createFileDownloadResponse(
            entity.getValidationReport(),
            MediaType.APPLICATION_XML,
            "KoSIT-Prüfbericht"
        );
    }

    @Override
    public ResponseEntity<Resource> downloadValidatorLog(
        Long id
    ) {
        InvoiceDocumentArchiveEntity entity =
            findEntityById(id);

        return createFileDownloadResponse(
            entity.getValidatorLog(),
            MediaType.TEXT_PLAIN,
            "Validator-Protokoll"
        );
    }

    // ---------------------------------------------------------
    // Downloads über Rechnungs-ID
    // ---------------------------------------------------------

    @Override
    public ResponseEntity<Resource> downloadPdfByInvoiceId(
        Long invoiceId
    ) {
        InvoiceDocumentArchiveEntity entity =
            findEntityByInvoiceId(invoiceId);

        return createFileDownloadResponse(
            entity.getPdfFile(),
            MediaType.APPLICATION_PDF,
            "PDF-Datei"
        );
    }

    @Override
    public ResponseEntity<Resource> downloadXmlByInvoiceId(
        Long invoiceId
    ) {
        InvoiceDocumentArchiveEntity entity =
            findEntityByInvoiceId(invoiceId);

        return createFileDownloadResponse(
            entity.getXmlFile(),
            MediaType.APPLICATION_XML,
            "XRechnung-XML"
        );
    }

    @Override
    public ResponseEntity<Resource>
    downloadValidationReportByInvoiceId(
        Long invoiceId
    ) {
        InvoiceDocumentArchiveEntity entity =
            findEntityByInvoiceId(invoiceId);

        return createFileDownloadResponse(
            entity.getValidationReport(),
            MediaType.APPLICATION_XML,
            "KoSIT-Prüfbericht"
        );
    }

    @Override
    public ResponseEntity<Resource>
    downloadValidatorLogByInvoiceId(
        Long invoiceId
    ) {
        InvoiceDocumentArchiveEntity entity =
            findEntityByInvoiceId(invoiceId);

        return createFileDownloadResponse(
            entity.getValidatorLog(),
            MediaType.TEXT_PLAIN,
            "Validator-Protokoll"
        );
    }

    // ---------------------------------------------------------
    // ZIP
    // ---------------------------------------------------------

    @Override
    public ResponseEntity<Resource> downloadZip(
        Long id
    ) {
        return createZipDownloadResponse(
            findEntityById(id)
        );
    }

    @Override
    public ResponseEntity<Resource> downloadZipByInvoiceId(
        Long invoiceId
    ) {
        return createZipDownloadResponse(
            findEntityByInvoiceId(invoiceId)
        );
    }

    // ---------------------------------------------------------
    // Invoice
    // ---------------------------------------------------------

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

    // ---------------------------------------------------------
    // Archive Entity
    // ---------------------------------------------------------

    private InvoiceDocumentArchiveEntity findEntityById(
        Long id
    ) {
        validateId(
            id,
            "Archiv-ID"
        );

        return repository
            .findById(id)
            .orElseThrow(() ->
                new ResourceNotFoundException(
                    "Archivdatensatz mit ID "
                        + id
                        + " wurde nicht gefunden."
                )
            );
    }

    private InvoiceDocumentArchiveEntity findEntityByInvoiceId(
        Long invoiceId
    ) {
        validateId(
            invoiceId,
            "Rechnungs-ID"
        );

        return repository
            .findByInvoiceId(invoiceId)
            .orElseThrow(() ->
                new ResourceNotFoundException(
                    "Für die Rechnung mit ID "
                        + invoiceId
                        + " wurde kein "
                        + "Archivdatensatz gefunden."
                )
            );
    }

    // ---------------------------------------------------------
    // Vollständigkeit des Archivs
    // ---------------------------------------------------------

    private void validateArchivedFiles(
        InvoiceDocumentArchiveEntity entity
    ) {
        resolveRequiredFile(
            entity.getPdfFile(),
            "PDF-Datei"
        );

        resolveRequiredFile(
            entity.getXmlFile(),
            "XRechnung-XML"
        );

        resolveRequiredFile(
            entity.getValidationReport(),
            "KoSIT-Prüfbericht"
        );

        resolveRequiredFile(
            entity.getValidatorLog(),
            "Validator-Protokoll"
        );
    }

    private boolean areArchivedFilesAvailable(
        InvoiceDocumentArchiveEntity entity
    ) {
        return isRegularReadableFile(
            entity.getPdfFile()
        )
            && isRegularReadableFile(
            entity.getXmlFile()
        )
            && isRegularReadableFile(
            entity.getValidationReport()
        )
            && isRegularReadableFile(
            entity.getValidatorLog()
        );
    }

    private boolean isRegularReadableFile(
        String filePath
    ) {
        if (
            filePath == null
                || filePath.isBlank()
        ) {
            return false;
        }

        try {
            Path path =
                Path.of(filePath)
                    .toAbsolutePath()
                    .normalize();

            return Files.isRegularFile(path)
                && Files.isReadable(path);

        } catch (InvalidPathException exception) {
            return false;
        }
    }

    // ---------------------------------------------------------
    // Invoice Archivstatus synchronisieren
    // ---------------------------------------------------------

    private void synchronizeInvoiceArchiveState(
        InvoiceEntity invoice,
        InvoiceDocumentArchiveEntity archive
    ) {
        String archiveDirectory =
            normalizeArchiveDirectory(
                archive.getArchiveDirectory()
            );

        boolean changed =
            !Boolean.TRUE.equals(
                invoice.getArchived()
            )
                || invoice.getArchivedAt() == null
                || !archiveDirectory.equals(
                invoice.getArchivePath()
            );

        if (!changed) {
            return;
        }

        invoice.setArchived(true);

        invoice.setArchivedAt(
            archive.getArchivedAt()
        );

        invoice.setArchivePath(
            archiveDirectory
        );

        invoiceRepository.save(invoice);
    }

    private String normalizeArchiveDirectory(
        String archiveDirectory
    ) {
        if (
            archiveDirectory == null
                || archiveDirectory.isBlank()
        ) {
            throw new IllegalStateException(
                "Im Archivdatensatz ist kein "
                    + "Archivverzeichnis gespeichert."
            );
        }

        try {
            return Path.of(
                    archiveDirectory
                )
                .toAbsolutePath()
                .normalize()
                .toString();

        } catch (InvalidPathException exception) {
            throw new IllegalStateException(
                "Das gespeicherte Archivverzeichnis "
                    + "ist ungültig: "
                    + archiveDirectory,
                exception
            );
        }
    }

    // ---------------------------------------------------------
    // Einzeldatei-Download
    // ---------------------------------------------------------

    private ResponseEntity<Resource> createFileDownloadResponse(
        String filePath,
        MediaType mediaType,
        String documentType
    ) {
        Path path =
            resolveRequiredFile(
                filePath,
                documentType
            );

        try {
            Resource resource =
                new UrlResource(
                    path.toUri()
                );

            if (!resource.exists()) {
                throw new ResourceNotFoundException(
                    documentType
                        + " wurde nicht gefunden: "
                        + path
                );
            }

            if (!resource.isReadable()) {
                throw new IllegalStateException(
                    documentType
                        + " ist nicht lesbar: "
                        + path
                );
            }

            String fileName =
                path.getFileName()
                    .toString();

            ContentDisposition contentDisposition =
                ContentDisposition
                    .attachment()
                    .filename(
                        fileName,
                        StandardCharsets.UTF_8
                    )
                    .build();

            return ResponseEntity
                .ok()
                .header(
                    HttpHeaders.CONTENT_DISPOSITION,
                    contentDisposition.toString()
                )
                .contentType(mediaType)
                .contentLength(
                    Files.size(path)
                )
                .body(resource);

        } catch (IOException exception) {
            throw new IllegalStateException(
                documentType
                    + " konnte nicht für den Download "
                    + "geöffnet werden: "
                    + path,
                exception
            );
        }
    }

    // ---------------------------------------------------------
    // ZIP-Download
    // ---------------------------------------------------------

    private ResponseEntity<Resource> createZipDownloadResponse(
        InvoiceDocumentArchiveEntity entity
    ) {
        byte[] zipContent =
            createZipContent(entity);

        String invoiceNumber =
            normalizeInvoiceNumber(
                entity.getInvoiceNumber()
            );

        String zipFileName =
            createSafeZipFileName(
                invoiceNumber
            );

        ContentDisposition contentDisposition =
            ContentDisposition
                .attachment()
                .filename(
                    zipFileName,
                    StandardCharsets.UTF_8
                )
                .build();

        Resource resource =
            new ByteArrayResource(
                zipContent
            );

        return ResponseEntity
            .ok()
            .header(
                HttpHeaders.CONTENT_DISPOSITION,
                contentDisposition.toString()
            )
            .contentType(ZIP_MEDIA_TYPE)
            .contentLength(
                zipContent.length
            )
            .body(resource);
    }

    private byte[] createZipContent(
        InvoiceDocumentArchiveEntity entity
    ) {
        /*
         * Alle vier Dateien sind für das Rechnungsarchiv Pflicht.
         */
        Path pdfFile =
            resolveRequiredFile(
                entity.getPdfFile(),
                "PDF-Datei"
            );

        Path xmlFile =
            resolveRequiredFile(
                entity.getXmlFile(),
                "XRechnung-XML"
            );

        Path validationReport =
            resolveRequiredFile(
                entity.getValidationReport(),
                "KoSIT-Prüfbericht"
            );

        Path validatorLog =
            resolveRequiredFile(
                entity.getValidatorLog(),
                "Validator-Protokoll"
            );

        Set<String> entryNames =
            new HashSet<>();

        try (
            ByteArrayOutputStream byteStream =
                new ByteArrayOutputStream();

            ZipOutputStream zipStream =
                new ZipOutputStream(
                    byteStream,
                    StandardCharsets.UTF_8
                )
        ) {
            addFileToZip(
                zipStream,
                pdfFile,
                entryNames
            );

            addFileToZip(
                zipStream,
                xmlFile,
                entryNames
            );

            addFileToZip(
                zipStream,
                validationReport,
                entryNames
            );

            addFileToZip(
                zipStream,
                validatorLog,
                entryNames
            );

            zipStream.finish();

            return byteStream.toByteArray();

        } catch (IOException exception) {
            throw new IllegalStateException(
                "Das ZIP-Archiv für die Rechnung "
                    + entity.getInvoiceNumber()
                    + " konnte nicht erzeugt werden.",
                exception
            );
        }
    }

    private void addFileToZip(
        ZipOutputStream zipStream,
        Path file,
        Set<String> entryNames
    ) throws IOException {

        String entryName =
            file.getFileName()
                .toString();

        if (!entryNames.add(entryName)) {
            throw new IllegalStateException(
                "Der Dateiname "
                    + entryName
                    + " ist im ZIP-Archiv "
                    + "mehrfach vorhanden."
            );
        }

        ZipEntry zipEntry =
            new ZipEntry(entryName);

        zipEntry.setTime(
            Files.getLastModifiedTime(file)
                .toMillis()
        );

        zipStream.putNextEntry(
            zipEntry
        );

        Files.copy(
            file,
            zipStream
        );

        zipStream.closeEntry();
    }

    // ---------------------------------------------------------
    // Pfade
    // ---------------------------------------------------------

    private Path resolveRequiredFile(
        String filePath,
        String documentType
    ) {
        Path path =
            resolvePath(
                filePath,
                documentType
            );

        if (!Files.isRegularFile(path)) {
            throw new ResourceNotFoundException(
                documentType
                    + " wurde im Dateisystem "
                    + "nicht gefunden: "
                    + path
            );
        }

        if (!Files.isReadable(path)) {
            throw new IllegalStateException(
                documentType
                    + " ist nicht lesbar: "
                    + path
            );
        }

        return path;
    }

    private Path resolvePath(
        String filePath,
        String documentType
    ) {
        if (
            filePath == null
                || filePath.isBlank()
        ) {
            throw new ResourceNotFoundException(
                "Für den Archivdatensatz ist kein Pfad für "
                    + documentType
                    + " hinterlegt."
            );
        }

        try {
            return Path.of(
                    filePath
                )
                .toAbsolutePath()
                .normalize();

        } catch (InvalidPathException exception) {
            throw new IllegalStateException(
                "Der gespeicherte Dateipfad für "
                    + documentType
                    + " ist ungültig: "
                    + filePath,
                exception
            );
        }
    }

    // ---------------------------------------------------------
    // Hilfsmethoden
    // ---------------------------------------------------------

    private String createSafeZipFileName(
        String invoiceNumber
    ) {
        String safeInvoiceNumber =
            invoiceNumber.replaceAll(
                "[\\\\/:*?\"<>|]",
                "-"
            );

        return safeInvoiceNumber
            + ".zip";
    }

    private String normalizeInvoiceNumber(
        String invoiceNumber
    ) {
        if (
            invoiceNumber == null
                || invoiceNumber.isBlank()
        ) {
            throw new IllegalArgumentException(
                "Die Rechnungsnummer darf nicht leer sein."
            );
        }

        return invoiceNumber.trim();
    }

    private void validateId(
        Long id,
        String fieldName
    ) {
        if (
            id == null
                || id <= 0
        ) {
            throw new IllegalArgumentException(
                fieldName
                    + " muss größer als 0 sein."
            );
        }
    }
}
