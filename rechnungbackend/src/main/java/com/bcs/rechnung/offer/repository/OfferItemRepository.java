package com.bcs.rechnung.offer.repository;

import com.bcs.rechnung.offer.OfferItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OfferItemRepository
    extends JpaRepository<OfferItemEntity, Long> {

    List<OfferItemEntity>
    findByOfferIdOrderByPositionNumber(
        Long offerId
    );

    List<OfferItemEntity>
    findByProductId(
        Long productId
    );
}
