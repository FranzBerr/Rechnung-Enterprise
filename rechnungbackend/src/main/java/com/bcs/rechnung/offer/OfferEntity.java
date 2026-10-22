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
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "offer")
public class OfferEntity {

    @Id
    @GeneratedValue(
        strategy = GenerationType.IDENTITY
    )
    private Long id;

    @Column(
        name = "offer_number",
        nullable = false,
        length = 30,
        unique = true
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

    @Enumerated(EnumType.STRING)
    @Column(
        name = "status",
        nullable = false,
        length = 30
    )
    private OfferStatus status =
        OfferStatus.DRAFT;

    // ---------------------------------------------------------
    // Beziehungen
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
    // Snapshot Kundendaten
    // ---------------------------------------------------------

    @Column(
        name = "customer_number",
        nullable = false,
        length = 30
    )
    private String customerNumber;

    @Column(
        name = "customer_name",
        nullable = false,
        length = 200
    )
    private String customerName;

    @Column(
        name = "customer_contact_person",
        length = 200
    )
    private String customerContactPerson;

    @Column(
        name = "customer_department",
        length = 200
    )
    private String customerDepartment;

    @Column(
        name = "customer_street",
        length = 200
    )
    private String customerStreet;

    @Column(
        name = "customer_zip_code",
        length = 20
    )
    private String customerZipCode;

    @Column(
        name = "customer_city",
        length = 100
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
    // Angebotseinstellungen
    // ---------------------------------------------------------

    @Column(
        name = "currency_code",
        nullable = false,
        length = 3
    )
    private String currencyCode =
        "EUR";

    @Column(
        name = "payment_term_days",
        nullable = false
    )
    private Integer paymentTermDays = 14;

    // ---------------------------------------------------------
    // Summen
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
    // Texte
    // ---------------------------------------------------------

    @Column(
        name = "introduction_text",
        length = 2000
    )
    private String introductionText;

    @Column(
        name = "closing_text",
        length = 2000
    )
    private String closingText;

    @Column(
        name = "notes",
        length = 2000
    )
    private String notes;

    // ---------------------------------------------------------
    // Aktiv
    // ---------------------------------------------------------

    @Column(
        name = "active",
        nullable = false
    )
    private boolean active = true;

    // ---------------------------------------------------------
    // Archivierung
    // ---------------------------------------------------------

    @Column(
        name = "archived",
        nullable = false
    )
    private boolean archived = false;

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
    // Positionen
    // ---------------------------------------------------------

    @OneToMany(
        mappedBy = "offer",
        cascade = CascadeType.ALL,
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    @OrderBy("positionNumber ASC")
    private List<OfferItemEntity> items =
        new ArrayList<>();

    // ---------------------------------------------------------
    // Helper
    // ---------------------------------------------------------

    public void addItem(
        OfferItemEntity item
    ) {
        if (item == null) {
            return;
        }

        item.setOffer(this);
        items.add(item);
    }

    public void removeItem(
        OfferItemEntity item
    ) {
        if (item == null) {
            return;
        }

        items.remove(item);
        item.setOffer(null);
    }

    public void replaceItems(
        List<OfferItemEntity> newItems
    ) {
        for (
            OfferItemEntity existingItem : items
        ) {
            existingItem.setOffer(null);
        }

        items.clear();

        if (newItems == null) {
            return;
        }

        for (
            OfferItemEntity item : newItems
        ) {
            addItem(item);
        }
    }
}
