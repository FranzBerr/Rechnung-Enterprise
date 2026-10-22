package com.bcs.rechnung.deliverynote.service;

import com.bcs.rechnung.deliverynote.dto.DeliveryNoteDto;
import com.bcs.rechnung.deliverynote.dto.DeliveryNoteRequest;

import java.util.List;

public interface DeliveryNoteService {

    List<DeliveryNoteDto> findAll();

    DeliveryNoteDto findById(Long id);

    DeliveryNoteDto create(
        DeliveryNoteRequest request
    );

    DeliveryNoteDto update(
        Long id,
        DeliveryNoteRequest request
    );

    void delete(Long id);
}
