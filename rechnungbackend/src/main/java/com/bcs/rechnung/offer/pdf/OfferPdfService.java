package com.bcs.rechnung.offer.pdf;

public interface OfferPdfService {

    GeneratedOfferPdf generateOfferPdf(
        Long offerId
    );
}

