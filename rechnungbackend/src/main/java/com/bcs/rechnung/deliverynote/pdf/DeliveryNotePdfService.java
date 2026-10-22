package com.bcs.rechnung.deliverynote.pdf;

public interface DeliveryNotePdfService {

    GeneratedDeliveryNotePdf generateDeliveryNotePdf(
        Long deliveryNoteId
    );
}
