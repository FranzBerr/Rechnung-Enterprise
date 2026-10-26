package com.bcs.rechnung.offer;

import com.bcs.rechnung.company.CompanyEntity;
import com.bcs.rechnung.customer.CustomerEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
    name = "offer",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "UK_OFFER_NUMBER",
            columnNames = {
                "offer_number"
            }
        )
    },
    indexes = {
        @Index(
            name = "IX_OFFER_COMPANY_ID",
            columnList = "company_id"
        ),
        @Index(
            name = "IX_OFFER_CUSTOMER_ID",
            columnList = "customer_id"
        ),
        @Index(
            name = "IX_OFFER_OFFER_DATE",
            columnList = "offer_date"
        ),
        @Index(
            name = "IX_OFFER_STATUS",
            columnList = "status"
        ),
        @Index(
            name = "IX_OFFER_ARCHIVED",
            columnList = "archived"
        )
    }
)
@Getter
@Setter
@NoArgsConstructor
public class OfferEntity {

    // ---------------------------------------------------------
    // Primärschlüssel
    // ---------------------------------------------------------

    @Id
    @GeneratedValue(
        strategy = GenerationType.IDENTITY
    )
    @Column(
        name = "id"
    )
    private Long id;


    // ---------------------------------------------------------
    // Angebotsnummer und Datum
    // ---------------------------------------------------------

    @Column(
        name = "offer_number",
        nullable = false,
        length = 50
    )
    private String offerNumber;

    @Column(
        name = "offer_date",
        nullable = false
    )
    private LocalDate offerDate;

    @Column(
        name = "valid_until"
    )
    private LocalDate validUntil;


    // ---------------------------------------------------------
    // Status
    // ---------------------------------------------------------

    @Enumerated(
        EnumType.STRING
    )
    @Column(
        name = "status",
        nullable = false,
        length = 40
    )
    private OfferStatus status =
        OfferStatus.DRAFT;


    // ---------------------------------------------------------
    // Firma
    // ---------------------------------------------------------

    @ManyToOne(
        fetch = FetchType.LAZY,
        optional = false
    )
    @JoinColumn(
        name = "company_id",
        nullable = false
    )
    private CompanyEntity company;


    // ---------------------------------------------------------
    // Kunde
    // ---------------------------------------------------------

    @ManyToOne(
        fetch = FetchType.LAZY,
        optional = false
    )
    @JoinColumn(
        name = "customer_id",
        nullable = false
    )
    private CustomerEntity customer;


    // ---------------------------------------------------------
    // Kundensnapshot
    // ---------------------------------------------------------

    @Column(
        name = "customer_number",
        nullable = false,
        length = 50
    )
    private String customerNumber;

    @Column(
        name = "customer_name",
        nullable = false,
        length = 255
    )
    private String customerName;

    @Column(
        name = "customer_contact_person",
        length = 255
    )
    private String customerContactPerson;

    @Column(
        name = "customer_department",
        length = 255
    )
    private String customerDepartment;

    @Column(
        name = "customer_street",
        length = 255
    )
    private String customerStreet;

    @Column(
        name = "customer_zip_code",
        length = 20
    )
    private String customerZipCode;

    @Column(
        name = "customer_city",
        length = 255
    )
    private String customerCity;

    @Column(
        name = "customer_country",
        length = 100
    )
    private String customerCountry;

    @Column(
        name = "customer_vat_id",
        length = 50
    )
    private String customerVatId;


    // ---------------------------------------------------------
    // Zahlungsdaten
    // ---------------------------------------------------------

    @Column(
        name = "currency_code",
        nullable = false,
        length = 3
    )
    private String currencyCode =
        "EUR";

    @Column(
        name = "payment_term_days"
    )
    private Integer paymentTermDays =
        14;


    // ---------------------------------------------------------
    // Angebotssummen
    // ---------------------------------------------------------

    @Column(
        name = "subtotal_net",
        nullable = false,
        precision = 19,
        scale = 2
    )
    private BigDecimal subtotalNet =
        BigDecimal.ZERO;

    @Column(
        name = "tax_amount",
        nullable = false,
        precision = 19,
        scale = 2
    )
    private BigDecimal taxAmount =
        BigDecimal.ZERO;

    @Column(
        name = "total_gross",
        nullable = false,
        precision = 19,
        scale = 2
    )
    private BigDecimal totalGross =
        BigDecimal.ZERO;


    // ---------------------------------------------------------
    // Angebotstexte
    // ---------------------------------------------------------

    @Column(
        name = "introduction_text",
        columnDefinition = "nvarchar(max)"
    )
    private String introductionText;

    @Column(
        name = "closing_text",
        columnDefinition = "nvarchar(max)"
    )
    private String closingText;

    @Column(
        name = "notes",
        columnDefinition = "nvarchar(max)"
    )
    private String notes;


    // ---------------------------------------------------------
    // Statusfelder
    // ---------------------------------------------------------

    @Column(
        name = "active",
        nullable = false
    )
    private boolean active =
        true;

    @Column(
        name = "archived",
        nullable = false
    )
    private boolean archived =
        false;

    @Column(
        name = "archived_at"
    )
    private LocalDateTime archivedAt;

    @Column(
        name = "archive_path",
        length = 1000
    )
    private String archivePath;


    // ---------------------------------------------------------
    // Angebotspositionen
    // ---------------------------------------------------------

    @OneToMany(
        mappedBy = "offer",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    @OrderBy(
        "positionNumber ASC"
    )
    private List<OfferItemEntity> items =
        new ArrayList<>();


    // ---------------------------------------------------------
    // Position hinzufügen
    // ---------------------------------------------------------

    public void addItem(
        OfferItemEntity item
    ) {

        if (item == null) {
            return;
        }

        items.add(
            item
        );

        item.setOffer(
            this
        );
    }


    // ---------------------------------------------------------
    // Position entfernen
    // ---------------------------------------------------------

    public void removeItem(
        OfferItemEntity item
    ) {

        if (item == null) {
            return;
        }

        boolean removed =
            items.remove(
                item
            );

        if (removed) {
            item.setOffer(
                null
            );
        }
    }


    // ---------------------------------------------------------
    // Alle Positionen entfernen
    // ---------------------------------------------------------

    public void clearItems() {

        for (
            OfferItemEntity item :
            items
        ) {
            item.setOffer(
                null
            );
        }

        items.clear();
    }


    // ---------------------------------------------------------
    // Positionen ersetzen
    // ---------------------------------------------------------

    public void replaceItems(
        List<OfferItemEntity> newItems
    ) {

        clearItems();

        if (newItems == null) {
            return;
        }

        for (
            OfferItemEntity item :
            newItems
        ) {
            addItem(
                item
            );
        }
    }


    // ---------------------------------------------------------
    // Archivieren
    // ---------------------------------------------------------

    public void archive(
        String archivePath
    ) {

        this.archived =
            true;

        this.archivedAt =
            LocalDateTime.now();

        this.archivePath =
            archivePath;
    }


    // ---------------------------------------------------------
    // Archivierung zurücksetzen
    // ---------------------------------------------------------

    public void undoArchive() {

        this.archived =
            false;

        this.archivedAt =
            null;

        this.archivePath =
            null;
    }
}
