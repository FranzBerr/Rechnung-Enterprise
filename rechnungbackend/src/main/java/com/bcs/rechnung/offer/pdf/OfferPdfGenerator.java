package com.bcs.rechnung.offer.pdf;

import com.bcs.rechnung.offer.OfferEntity;

import java.io.IOException;

public interface OfferPdfGenerator {

    byte[] generate(
        OfferEntity offer
    ) throws IOException;
}
