package com.bcs.rechnung.deliverynote.archive;

import com.bcs.rechnung.config.DocumentArchiveProperties;
import com.bcs.rechnung.deliverynote.DeliveryNoteEntity;
import com.bcs.rechnung.deliverynote.pdf.DeliveryNotePdfService;
import com.bcs.rechnung.deliverynote.repository.DeliveryNoteRepository;
import com.bcs.rechnung.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class DeliveryNoteArchiveServiceImpl
    implements DeliveryNoteArchiveService {

    private static final String DIRECTORY_NAME =
        "Lieferscheine";

    private final DeliveryNoteRepository deliveryNoteRepository;
    private final DeliveryNotePdfService deliveryNotePdfService;
    private final DocumentArchiveProperties archiveProperties;

    @Override
    public Path archive(Long deliveryNoteId) {
        val deliveryNote =
            findDeliveryNote(deliveryNoteId);

        if (deliveryNote.isArchived()) {
            return getExistingArchivePath(
                deliveryNote
            );
        }

        val generated =
            deliveryNotePdfService
                .generateDeliveryNotePdf(
                    deliveryNoteId
                );

        Path archiveDirectory =
            createArchiveDirectory(
                deliveryNote
            );

        Path archiveFile =
            archiveDirectory.resolve(
                createFileName(
                    deliveryNote
                )
            );

        writePdf(
            archiveDirectory,
            archiveFile,
            generated.content()
        );

        deliveryNote.setArchived(true);

        deliveryNote.setArchivedAt(
            LocalDateTime.now()
        );

        deliveryNote.setArchivePath(
            archiveFile
                .toAbsolutePath()
                .normalize()
                .toString()
        );

        deliveryNoteRepository.save(
            deliveryNote
        );

        return archiveFile;
    }

    @Override
    @Transactional(readOnly = true)
    public Path getArchivedPdf(
        Long deliveryNoteId
    ) {
        val deliveryNote =
            findDeliveryNote(deliveryNoteId);

        if (!deliveryNote.isArchived()) {
            throw new IllegalStateException(
                "Der Lieferschein wurde noch nicht archiviert."
            );
        }

        return getExistingArchivePath(
            deliveryNote
        );
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isArchived(
        Long deliveryNoteId
    ) {
        return findDeliveryNote(
            deliveryNoteId
        ).isArchived();
    }

    private void writePdf(
        Path archiveDirectory,
        Path archiveFile,
        byte[] content
    ) {
        try {
            Files.createDirectories(
                archiveDirectory
            );

            Files.write(
                archiveFile,
                content,
                StandardOpenOption.CREATE_NEW,
                StandardOpenOption.WRITE
            );

        } catch (IOException exception) {
            throw new IllegalStateException(
                "Lieferschein konnte nicht im Archiv gespeichert werden: "
                    + archiveFile,
                exception
            );
        }
    }

    private Path createArchiveDirectory(
        DeliveryNoteEntity deliveryNote
    ) {
        int year =
            deliveryNote
                .getDeliveryDate()
                .getYear();

        return archiveProperties
            .directory()
            .resolve(
                String.valueOf(year)
            )
            .resolve(
                DIRECTORY_NAME
            );
    }

    private String createFileName(
        DeliveryNoteEntity deliveryNote
    ) {
        String number =
            deliveryNote
                .getDeliveryNoteNumber();

        if (
            number == null
                || number.isBlank()
        ) {
            throw new IllegalStateException(
                "Der Lieferschein besitzt keine Lieferscheinnummer."
            );
        }

        return number + ".pdf";
    }

    private DeliveryNoteEntity findDeliveryNote(
        Long id
    ) {
        validateId(id);

        return deliveryNoteRepository
            .findById(id)
            .orElseThrow(
                () -> new ResourceNotFoundException(
                    "Lieferschein mit ID "
                        + id
                        + " nicht gefunden."
                )
            );
    }

    private Path getExistingArchivePath(
        DeliveryNoteEntity deliveryNote
    ) {
        String archivePath =
            deliveryNote.getArchivePath();

        if (
            archivePath == null
                || archivePath.isBlank()
        ) {
            throw new IllegalStateException(
                "Für den Lieferschein ist kein Archivpfad gespeichert."
            );
        }

        Path file =
            Path.of(archivePath);

        if (!Files.isRegularFile(file)) {
            throw new IllegalStateException(
                "Die archivierte PDF-Datei existiert nicht: "
                    + archivePath
            );
        }

        return file;
    }

    private void validateId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException(
                "Die Lieferschein-ID muss größer als 0 sein."
            );
        }
    }
}
