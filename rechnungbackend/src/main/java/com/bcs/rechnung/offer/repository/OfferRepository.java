package com.bcs.rechnung.offer.repository;

import com.bcs.rechnung.offer.OfferEntity;
import com.bcs.rechnung.offer.OfferStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OfferRepository
    extends JpaRepository<OfferEntity, Long> {

    // ---------------------------------------------------------
    // Angebot über Angebotsnummer suchen
    // ---------------------------------------------------------

    Optional<OfferEntity> findByOfferNumber(
        String offerNumber
    );


    // ---------------------------------------------------------
    // Prüfen, ob Angebotsnummer existiert
    // ---------------------------------------------------------

    boolean existsByOfferNumber(
        String offerNumber
    );


    // ---------------------------------------------------------
    // Letzte Angebotsnummer eines Nummernkreises ermitteln
    // für automatische Nummernvergabe
    // ---------------------------------------------------------

    Optional<OfferEntity>
    findTopByOfferNumberStartingWithOrderByOfferNumberDesc(
        String prefix
    );


    // ---------------------------------------------------------
    // Alle Angebote nach Datum absteigend
    // ---------------------------------------------------------

    List<OfferEntity>
    findAllByOrderByOfferDateDesc();


    // ---------------------------------------------------------
    // Angebote eines Kunden
    // ---------------------------------------------------------

    List<OfferEntity>
    findByCustomerIdOrderByOfferDateDesc(
        Long customerId
    );


    // ---------------------------------------------------------
    // Angebote nach Status
    // ---------------------------------------------------------

    List<OfferEntity>
    findByStatusOrderByOfferDateDesc(
        OfferStatus status
    );


    // ---------------------------------------------------------
    // Angebote nach Archivstatus
    // ---------------------------------------------------------

    List<OfferEntity>
    findByArchivedOrderByOfferDateDesc(
        boolean archived
    );
}
