package com.bcs.rechnung.offer.archive;

import java.nio.file.Path;

public interface OfferArchiveService {

    Path archive(
        Long offerId
    );

    Path getArchivedPdf(
        Long offerId
    );

    boolean isArchived(
        Long offerId
    );

    void undoArchive(
        Long offerId
    );
}
