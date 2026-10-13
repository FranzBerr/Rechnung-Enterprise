package com.bcs.rechnung.invoice.document;

import com.bcs.rechnung.exception.ResourceNotFoundException;
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
        InvoiceDocumentArchiveEntity entity =
            findEntityById(
                id
            );

        return mapper.toDto(
            entity
        );
    }

    @Override
    public ArchivedDocumentDto findByInvoiceId(
        Long invoiceId
    ) {
        InvoiceDocumentArchiveEntity entity =
            findEntityByInvoiceId(
                invoiceId
            );

        return mapper.toDto(
            entity
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
                            + " wurde kein Archivdatensatz gefunden."
                    )
                );

        return mapper.toDto(
            entity
        );
    }

    @Override
    public ResponseEntity<Resource> downloadPdf(
        Long id
    ) {
        InvoiceDocumentArchiveEntity entity =
            findEntityById(
                id
            );

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
            findEntityById(
                id
            );

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
            findEntityById(
                id
            );

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
            findEntityById(
                id
            );

        return createFileDownloadResponse(
            entity.getValidatorLog(),
            MediaType.TEXT_PLAIN,
            "Validator-Protokoll"
        );
    }

    @Override
    public ResponseEntity<Resource> downloadPdfByInvoiceId(
        Long invoiceId
    ) {
        InvoiceDocumentArchiveEntity entity =
            findEntityByInvoiceId(
                invoiceId
            );

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
            findEntityByInvoiceId(
                invoiceId
            );

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
            findEntityByInvoiceId(
                invoiceId
            );

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
            findEntityByInvoiceId(
                invoiceId
            );

        return createFileDownloadResponse(
            entity.getValidatorLog(),
            MediaType.TEXT_PLAIN,
            "Validator-Protokoll"
        );
    }

    @Override
    public ResponseEntity<Resource> downloadZip(
        Long id
    ) {
        InvoiceDocumentArchiveEntity entity =
            findEntityById(
                id
            );

        return createZipDownloadResponse(
            entity
        );
    }

    @Override
    public ResponseEntity<Resource> downloadZipByInvoiceId(
        Long invoiceId
    ) {
        InvoiceDocumentArchiveEntity entity =
            findEntityByInvoiceId(
                invoiceId
            );

        return createZipDownloadResponse(
            entity
        );
    }

    private InvoiceDocumentArchiveEntity findEntityById(
        Long id
    ) {
        validateId(
            id,
            "Archiv-ID"
        );

        return repository
            .findById(
                id
            )
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
            .findByInvoiceId(
                invoiceId
            )
            .orElseThrow(() ->
                new ResourceNotFoundException(
                    "Für die Rechnung mit ID "
                        + invoiceId
                        + " wurde kein Archivdatensatz gefunden."
                )
            );
    }

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
                .contentType(
                    mediaType
                )
                .contentLength(
                    Files.size(path)
                )
                .body(
                    resource
                );

        } catch (IOException exception) {
            throw new IllegalStateException(
                documentType
                    + " konnte nicht für den Download geöffnet werden: "
                    + path,
                exception
            );
        }
    }

    private ResponseEntity<Resource> createZipDownloadResponse(
        InvoiceDocumentArchiveEntity entity
    ) {
        byte[] zipContent =
            createZipContent(
                entity
            );

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
            .contentType(
                ZIP_MEDIA_TYPE
            )
            .contentLength(
                zipContent.length
            )
            .body(
                resource
            );
    }

    private byte[] createZipContent(
        InvoiceDocumentArchiveEntity entity
    ) {
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
            resolveOptionalFile(
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

            if (validatorLog != null) {
                addFileToZip(
                    zipStream,
                    validatorLog,
                    entryNames
                );
            }

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
                    + " ist im ZIP-Archiv mehrfach vorhanden."
            );
        }

        ZipEntry zipEntry =
            new ZipEntry(
                entryName
            );

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
                    + " wurde im Dateisystem nicht gefunden: "
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

    private Path resolveOptionalFile(
        String filePath,
        String documentType
    ) {
        if (filePath == null
            || filePath.isBlank()) {

            return null;
        }

        return resolveRequiredFile(
            filePath,
            documentType
        );
    }

    private Path resolvePath(
        String filePath,
        String documentType
    ) {
        if (filePath == null
            || filePath.isBlank()) {

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

    private String createSafeZipFileName(
        String invoiceNumber
    ) {
        String safeInvoiceNumber =
            invoiceNumber
                .replaceAll(
                    "[\\\\/:*?\"<>|]",
                    "-"
                );

        return safeInvoiceNumber
            + ".zip";
    }

    private String normalizeInvoiceNumber(
        String invoiceNumber
    ) {
        if (invoiceNumber == null
            || invoiceNumber.isBlank()) {

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
        if (id == null
            || id <= 0) {

            throw new IllegalArgumentException(
                fieldName
                    + " muss größer als 0 sein."
            );
        }
    }
}
