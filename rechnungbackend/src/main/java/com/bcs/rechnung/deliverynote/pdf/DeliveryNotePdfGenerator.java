package com.bcs.rechnung.deliverynote.pdf;

import com.bcs.rechnung.deliverynote.DeliveryNoteEntity;

import java.io.IOException;

public interface DeliveryNotePdfGenerator {

    byte[] generate(
        DeliveryNoteEntity deliveryNote
    ) throws IOException;
}
