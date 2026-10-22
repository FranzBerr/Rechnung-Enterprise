package com.bcs.rechnung.deliverynote.repository;

import com.bcs.rechnung.deliverynote.DeliveryNoteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DeliveryNoteRepository
    extends JpaRepository<DeliveryNoteEntity, Long> {

    Optional<DeliveryNoteEntity> findByDeliveryNoteNumber(
        String deliveryNoteNumber
    );

    boolean existsByDeliveryNoteNumber(
        String deliveryNoteNumber
    );

    List<DeliveryNoteEntity> findByCustomerId(
        Long customerId
    );

    List<DeliveryNoteEntity>
    findByCustomerIdOrderByDeliveryDateDesc(
        Long customerId
    );

    List<DeliveryNoteEntity>
    findAllByOrderByDeliveryDateDesc();
}
