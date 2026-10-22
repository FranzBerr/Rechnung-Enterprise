package com.bcs.rechnung.offer.archive;

import com.bcs.rechnung.config.DocumentArchiveProperties;
import com.bcs.rechnung.exception.ResourceNotFoundException;
import com.bcs.rechnung.offer.OfferEntity;
import com.bcs.rechnung.offer.pdf.GeneratedOfferPdf;
import com.bcs.rechnung.offer.pdf.OfferPdfService;
import com.bcs.rechnung.offer.repository.OfferRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class OfferArchiveServiceImpl
    implements OfferArchiveService {

    private static final String DIRECTORY_NAME =
        "Angebote";

    private final OfferRepository offerRepository;

    private final OfferPdfService offerPdfService;

    private final DocumentArchiveProperties archiveProperties;

    // ---------------------------------------------------------
    // Archivieren
    // ---------------------------------------------------------

    @Override
    public Path archive(
        Long offerId
    ) {
        OfferEntity offer =
            findOffer(offerId);

        /*
         * Bereits archiviertes Angebot:
         *
         * Keine Neuerzeugung und kein Überschreiben.
         * Stattdessen vorhandene Archivdatei zurückgeben.
         */
        if (offer.isArchived()) {
            return getExistingArchivePath(
                offer
            );
        }

        GeneratedOfferPdf generated =
            offerPdfService.generateOfferPdf(
                offerId
            );

        validateGeneratedPdf(
            offer,
            generated
        );

        Path archiveDirectory =
            createArchiveDirectory(
                offer
            );

        Path archiveFile =
            archiveDirectory.resolve(
                createFileName(
                    offer
                )
            );

        writePdf(
            archiveDirectory,
            archiveFile,
            generated.content()
        );

        /*
         * Erst nach erfolgreicher Speicherung
         * wird das Angebot als archiviert markiert.
         */
        offer.setArchived(true);

        offer.setArchivedAt(
            LocalDateTime.now()
        );

        offer.setArchivePath(
            archiveDirectory
                .toAbsolutePath()
                .normalize()
                .toString()
        );

        offerRepository.saveAndFlush(
            offer
        );

        return archiveFile;
    }

    // ---------------------------------------------------------
    // Archiviertes PDF lesen
    // ---------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public Path getArchivedPdf(
        Long offerId
    ) {
        OfferEntity offer =
            findOffer(offerId);

        if (!offer.isArchived()) {
            throw new IllegalStateException(
                "Das Angebot "
                    + offer.getOfferNumber()
                    + " wurde noch nicht archiviert."
            );
        }

        return getExistingArchivePath(
            offer
        );
    }

    // ---------------------------------------------------------
    // Archivstatus
    // ---------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public boolean isArchived(
        Long offerId
    ) {
        OfferEntity offer =
            findOffer(offerId);

        if (!offer.isArchived()) {
            return false;
        }

        try {
            Path archiveFile =
                resolveArchiveFile(
                    offer
                );

            return Files.isRegularFile(
                archiveFile
            ) && Files.isReadable(
                archiveFile
            );

        } catch (RuntimeException exception) {
            return false;
        }
    }

    // ---------------------------------------------------------
    // Archivierung zurücksetzen
    // ---------------------------------------------------------

    @Override
    public void undoArchive(
        Long offerId
    ) {
        OfferEntity offer =
            findOffer(offerId);

        if (!offer.isArchived()) {
            throw new IllegalStateException(
                "Das Angebot "
                    + offer.getOfferNumber()
                    + " ist nicht archiviert."
            );
        }

        /*
         * Die physische PDF-Datei wird bewusst
         * NICHT gelöscht.
         *
         * Es wird ausschließlich der
         * Datenbankstatus zurückgesetzt.
         */
        offer.setArchived(false);

        offer.setArchivedAt(null);

        offer.setArchivePath(null);

        offerRepository.saveAndFlush(
            offer
        );
    }

    // ---------------------------------------------------------
    // Angebot laden
    // ---------------------------------------------------------

    private OfferEntity findOffer(
        Long offerId
    ) {
        validateId(offerId);

        return offerRepository
            .findById(offerId)
            .orElseThrow(() ->
                new ResourceNotFoundException(
                    "Angebot mit ID "
                        + offerId
                        + " wurde nicht gefunden."
                )
            );
    }

    // ---------------------------------------------------------
    // Archivverzeichnis
    // ---------------------------------------------------------

    private Path createArchiveDirectory(
        OfferEntity offer
    ) {
        if (offer.getOfferDate() == null) {
            throw new IllegalStateException(
                "Das Angebot besitzt kein Angebotsdatum."
            );
        }

        String safeOfferNumber =
            createSafeFileName(
                normalizeOfferNumber(
                    offer.getOfferNumber()
                )
            );

        int year =
            offer.getOfferDate()
                .getYear();

        Path root =
            archiveProperties
                .directory()
                .toAbsolutePath()
                .normalize();

        Path yearDirectory =
            root
                .resolve(
                    String.valueOf(year)
                )
                .normalize();

        Path typeDirectory =
            yearDirectory
                .resolve(
                    DIRECTORY_NAME
                )
                .normalize();

        Path offerDirectory =
            typeDirectory
                .resolve(
                    safeOfferNumber
                )
                .normalize();

        /*
         * Schutz gegen ungültige bzw.
         * manipulierte Pfade.
         */
        if (!yearDirectory.startsWith(root)) {
            throw new IllegalStateException(
                "Das Jahresverzeichnis liegt "
                    + "außerhalb des Archivverzeichnisses."
            );
        }

        if (!typeDirectory.startsWith(root)) {
            throw new IllegalStateException(
                "Das Angebotsverzeichnis liegt "
                    + "außerhalb des Archivverzeichnisses."
            );
        }

        if (
            !offerDirectory.startsWith(
                typeDirectory
            )
        ) {
            throw new IllegalStateException(
                "Der ermittelte Angebotsarchivpfad "
                    + "ist ungültig."
            );
        }

        return offerDirectory;
    }

    // ---------------------------------------------------------
    // Dateiname
    // ---------------------------------------------------------

    private String createFileName(
        OfferEntity offer
    ) {
        String safeOfferNumber =
            createSafeFileName(
                normalizeOfferNumber(
                    offer.getOfferNumber()
                )
            );

        return safeOfferNumber
            + ".pdf";
    }

    // ---------------------------------------------------------
    // PDF schreiben
    // ---------------------------------------------------------

    private void writePdf(
        Path archiveDirectory,
        Path archiveFile,
        byte[] content
    ) {
        if (
            content == null
                || content.length == 0
        ) {
            throw new IllegalStateException(
                "Das erzeugte Angebots-PDF ist leer."
            );
        }

        try {
            Files.createDirectories(
                archiveDirectory
            );

            /*
             * CREATE_NEW ist hier bewusst gewählt.
             *
             * Eine bereits vorhandene Archivdatei
             * darf nicht unbemerkt überschrieben werden.
             */
            Files.write(
                archiveFile,
                content,
                StandardOpenOption.CREATE_NEW,
                StandardOpenOption.WRITE
            );

        } catch (IOException exception) {
            throw new IllegalStateException(
                "Das Angebots-PDF konnte nicht "
                    + "im Archiv gespeichert werden: "
                    + archiveFile,
                exception
            );
        }
    }

    // ---------------------------------------------------------
    // Bestehendes Archiv
    // ---------------------------------------------------------

    private Path getExistingArchivePath(
        OfferEntity offer
    ) {
        Path archiveFile =
            resolveArchiveFile(
                offer
            );

        if (!Files.isRegularFile(archiveFile)) {
            throw new IllegalStateException(
                "Die archivierte Angebots-PDF "
                    + "existiert nicht: "
                    + archiveFile
            );
        }

        if (!Files.isReadable(archiveFile)) {
            throw new IllegalStateException(
                "Die archivierte Angebots-PDF "
                    + "ist nicht lesbar: "
                    + archiveFile
            );
        }

        return archiveFile;
    }

    // ---------------------------------------------------------
    // Archivdatei aus gespeichertem Pfad ermitteln
    // ---------------------------------------------------------

    private Path resolveArchiveFile(
        OfferEntity offer
    ) {
        String archivePath =
            offer.getArchivePath();

        if (
            archivePath == null
                || archivePath.isBlank()
        ) {
            throw new IllegalStateException(
                "Für das Angebot "
                    + offer.getOfferNumber()
                    + " ist kein Archivpfad gespeichert."
            );
        }

        try {
            Path archiveDirectory =
                Path.of(
                        archivePath
                    )
                    .toAbsolutePath()
                    .normalize();

            Path archiveFile =
                archiveDirectory
                    .resolve(
                        createFileName(
                            offer
                        )
                    )
                    .normalize();

            if (
                !archiveFile.startsWith(
                    archiveDirectory
                )
            ) {
                throw new IllegalStateException(
                    "Der ermittelte Archivdateipfad "
                        + "ist ungültig."
                );
            }

            return archiveFile;

        } catch (
            InvalidPathException exception
        ) {
            throw new IllegalStateException(
                "Der gespeicherte Archivpfad "
                    + "des Angebots ist ungültig: "
                    + archivePath,
                exception
            );
        }
    }

    // ---------------------------------------------------------
    // Generiertes PDF prüfen
    // ---------------------------------------------------------

    private void validateGeneratedPdf(
        OfferEntity offer,
        GeneratedOfferPdf generated
    ) {
        if (generated == null) {
            throw new IllegalStateException(
                "Es wurde kein Angebots-PDF erzeugt."
            );
        }

        String offerNumber =
            normalizeOfferNumber(
                offer.getOfferNumber()
            );

        String generatedOfferNumber =
            normalizeOfferNumber(
                generated.offerNumber()
            );

        /*
         * Wichtig:
         * Das generierte PDF muss zur geladenen
         * OfferEntity gehören.
         */
        if (
            !offerNumber.equals(
                generatedOfferNumber
            )
        ) {
            throw new IllegalStateException(
                "Die Angebotsnummer des erzeugten PDFs "
                    + "stimmt nicht mit dem Angebot überein. "
                    + "Angebot: "
                    + offerNumber
                    + ", PDF: "
                    + generatedOfferNumber
            );
        }

        if (
            generated.content() == null
                || generated.content().length == 0
        ) {
            throw new IllegalStateException(
                "Das erzeugte Angebots-PDF ist leer."
            );
        }
    }

    // ---------------------------------------------------------
    // Angebotsnummer
    // ---------------------------------------------------------

    private String normalizeOfferNumber(
        String offerNumber
    ) {
        if (
            offerNumber == null
                || offerNumber.isBlank()
        ) {
            throw new IllegalStateException(
                "Das Angebot besitzt keine Angebotsnummer."
            );
        }

        return offerNumber.trim();
    }

    // ---------------------------------------------------------
    // Sicherer Dateiname
    // ---------------------------------------------------------

    private String createSafeFileName(
        String value
    ) {
        return value.replaceAll(
            "[\\\\/:*?\"<>|]",
            "-"
        );
    }

    // ---------------------------------------------------------
    // ID prüfen
    // ---------------------------------------------------------

    private void validateId(
        Long offerId
    ) {
        if (
            offerId == null
                || offerId <= 0
        ) {
            throw new IllegalArgumentException(
                "Die Angebots-ID muss größer als 0 sein."
            );
        }
    }
}
