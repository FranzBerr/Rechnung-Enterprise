package com.bcs.rechnung.offer.repository;

import com.bcs.rechnung.offer.OfferEntity;
import com.bcs.rechnung.offer.OfferStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OfferRepository
    extends JpaRepository<OfferEntity, Long> {

    Optional<OfferEntity> findByOfferNumber(
        String offerNumber
    );

    boolean existsByOfferNumber(
        String offerNumber
    );

    List<OfferEntity>
    findAllByOrderByOfferDateDesc();

    List<OfferEntity>
    findByCustomerIdOrderByOfferDateDesc(
        Long customerId
    );

    List<OfferEntity>
    findByStatusOrderByOfferDateDesc(
        OfferStatus status
    );

    List<OfferEntity>
    findByArchivedOrderByOfferDateDesc(
        boolean archived
    );
}
