package com.bcs.rechnung.deliverynote.repository;

import com.bcs.rechnung.deliverynote.DeliveryNoteItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DeliveryNoteItemRepository
    extends JpaRepository<DeliveryNoteItemEntity, Long> {

    List<DeliveryNoteItemEntity> findByDeliveryNoteIdOrderByPositionNumber(Long deliveryNoteId);

    List<DeliveryNoteItemEntity>findByProductId( Long productId);
}
