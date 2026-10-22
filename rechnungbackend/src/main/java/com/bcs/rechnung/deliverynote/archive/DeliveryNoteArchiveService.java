package com.bcs.rechnung.deliverynote.archive;

import java.nio.file.Path;

public interface DeliveryNoteArchiveService {

    Path archive(Long deliveryNoteId);

    Path getArchivedPdf(Long deliveryNoteId);

    boolean isArchived(Long deliveryNoteId);
}
