package com.bcs.rechnung.deliverynote;

import com.bcs.rechnung.customer.CustomerEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
    name = "delivery_note",
    uniqueConstraints = @UniqueConstraint(
        name = "UK_DELIVERY_NOTE_NUMBER",
        columnNames = "delivery_note_number"
    )
)
public class DeliveryNoteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
        name = "delivery_note_number",
        nullable = false,
        length = 30
    )
    private String deliveryNoteNumber;

    @ManyToOne(
        fetch = FetchType.LAZY,
        optional = false
    )
    @JoinColumn(
        name = "customer_id",
        nullable = false
    )
    private CustomerEntity customer;

    @Column(
        name = "delivery_date",
        nullable = false
    )
    private LocalDate deliveryDate;

    @Column(
        name = "delivery_name",
        nullable = false,
        length = 200
    )
    private String deliveryName;

    @Column(
        name = "delivery_contact_person",
        length = 200
    )
    private String deliveryContactPerson;

    @Column(
        name = "delivery_department",
        length = 200
    )
    private String deliveryDepartment;

    @Column(
        name = "delivery_street",
        length = 200
    )
    private String deliveryStreet;

    @Column(
        name = "delivery_zip_code",
        length = 20
    )
    private String deliveryZipCode;

    @Column(
        name = "delivery_city",
        length = 100
    )
    private String deliveryCity;

    @Column(
        name = "delivery_country",
        length = 100
    )
    private String deliveryCountry;

    @Column(
        name = "order_reference",
        length = 100
    )
    private String orderReference;

    @Column(
        name = "shipping_method",
        length = 100
    )
    private String shippingMethod;

    @Column(
        name = "tracking_number",
        length = 100
    )
    private String trackingNumber;

    @Column(
        name = "notes",
        length = 2000
    )
    private String notes;

    @Column(
        name = "active",
        nullable = false
    )
    private boolean active = true;

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

    @Column(
        name = "delivery_date_signed"
    )
    private LocalDate deliveryDateSigned;

    @Column(
        name = "delivery_person",
        length = 200
    )
    private String deliveryPerson;

    @Lob
    @Column(
        name = "delivery_signature"
    )
    private byte[] deliverySignature;

    @Column(
        name = "receipt_date_signed"
    )
    private LocalDate receiptDateSigned;

    @Column(
        name = "receipt_person",
        length = 200
    )
    private String receiptPerson;

    @Lob
    @Column(
        name = "receipt_signature"
    )
    private byte[] receiptSignature;

    @OneToMany(
        mappedBy = "deliveryNote",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    @OrderBy("positionNumber ASC")
    private List<DeliveryNoteItemEntity> items =
        new ArrayList<>();

    public void addItem(
        DeliveryNoteItemEntity item
    ) {
        if (item == null) {
            return;
        }

        items.add(item);
        item.setDeliveryNote(this);
    }

    public void removeItem(
        DeliveryNoteItemEntity item
    ) {
        if (item == null) {
            return;
        }

        items.remove(item);
        item.setDeliveryNote(null);
    }

    public void clearItems() {
        new ArrayList<>(items)
            .forEach(this::removeItem);
    }
}
