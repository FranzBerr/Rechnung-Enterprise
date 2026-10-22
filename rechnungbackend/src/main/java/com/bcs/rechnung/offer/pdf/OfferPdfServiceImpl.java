package com.bcs.rechnung.offer.pdf;

import com.bcs.rechnung.exception.ResourceNotFoundException;
import com.bcs.rechnung.offer.OfferEntity;
import com.bcs.rechnung.offer.repository.OfferRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OfferPdfServiceImpl
    implements OfferPdfService {

    private final OfferRepository offerRepository;

    private final OfferPdfGenerator offerPdfGenerator;

    @Override
    public GeneratedOfferPdf generateOfferPdf(
        Long offerId
    ) {
        validateId(offerId);

        OfferEntity offer =
            offerRepository
                .findById(offerId)
                .orElseThrow(() ->
                    new ResourceNotFoundException(
                        "Angebot mit ID "
                            + offerId
                            + " wurde nicht gefunden."
                    )
                );

        try {
            byte[] content =
                offerPdfGenerator.generate(
                    offer
                );

            return new GeneratedOfferPdf(
                offer.getOfferNumber(),
                content
            );

        } catch (IOException exception) {
            throw new IllegalStateException(
                "Das Angebots-PDF konnte nicht erzeugt werden.",
                exception
            );
        }
    }

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
