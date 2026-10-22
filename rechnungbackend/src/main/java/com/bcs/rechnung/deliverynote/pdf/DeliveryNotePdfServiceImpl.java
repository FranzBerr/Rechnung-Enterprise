package com.bcs.rechnung.deliverynote.pdf;

import com.bcs.rechnung.deliverynote.DeliveryNoteEntity;
import com.bcs.rechnung.deliverynote.repository.DeliveryNoteRepository;
import com.bcs.rechnung.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DeliveryNotePdfServiceImpl
    implements DeliveryNotePdfService {

    private final DeliveryNoteRepository deliveryNoteRepository;
    private final DeliveryNotePdfGenerator deliveryNotePdfGenerator;

    @Override
    public GeneratedDeliveryNotePdf generateDeliveryNotePdf(
        Long deliveryNoteId
    ) {
        validateId(deliveryNoteId);

        val deliveryNote =
            findDeliveryNote(deliveryNoteId);

        try {
            val pdf =
                deliveryNotePdfGenerator.generate(
                    deliveryNote
                );

            return new GeneratedDeliveryNotePdf(
                deliveryNote.getDeliveryNoteNumber(),
                pdf
            );

        } catch (IOException exception) {
            throw new IllegalStateException(
                "Lieferschein-PDF konnte nicht erzeugt werden.",
                exception
            );
        }
    }

    private DeliveryNoteEntity findDeliveryNote(
        Long deliveryNoteId
    ) {
        return deliveryNoteRepository
            .findById(deliveryNoteId)
            .orElseThrow(
                () -> new ResourceNotFoundException(
                    "Lieferschein mit ID "
                        + deliveryNoteId
                        + " nicht gefunden."
                )
            );
    }

    private void validateId(
        Long deliveryNoteId
    ) {
        if (
            deliveryNoteId == null
                || deliveryNoteId <= 0
        ) {
            throw new IllegalArgumentException(
                "Die Lieferschein-ID muss größer als 0 sein."
            );
        }
    }
}
