package com.bcs.rechnung.offer.service;

import com.bcs.rechnung.offer.dto.OfferDto;
import com.bcs.rechnung.offer.dto.OfferRequest;

import java.util.List;

public interface OfferService {

    List<OfferDto> findAll();

    OfferDto findById(
        Long id
    );

    OfferDto create(
        OfferRequest request
    );

    OfferDto update(
        Long id,
        OfferRequest request
    );

    void delete(
        Long id
    );
}
