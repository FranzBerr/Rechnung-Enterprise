package com.bcs.rechnung.deliverynote;

import com.bcs.rechnung.product.ProductEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
    name = "delivery_note_item",
    uniqueConstraints = @UniqueConstraint(
        name = "UK_DELIVERY_NOTE_ITEM_POSITION",
        columnNames = {
            "delivery_note_id",
            "position_number"
        }
    )
)
public class DeliveryNoteItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(
        fetch = FetchType.LAZY,
        optional = false
    )
    @JoinColumn(
        name = "delivery_note_id",
        nullable = false
    )
    private DeliveryNoteEntity deliveryNote;

    @Column(
        name = "position_number",
        nullable = false
    )
    private Integer positionNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "product_id"
    )
    private ProductEntity product;

    @Column(
        name = "product_number",
        length = 30
    )
    private String productNumber;

    @Column(
        name = "name",
        nullable = false,
        length = 200
    )
    private String name;

    @Column(
        name = "description",
        length = 1000
    )
    private String description;

    @Column(
        name = "quantity",
        nullable = false,
        precision = 19,
        scale = 4
    )
    private BigDecimal quantity;

    @Column(
        name = "unit",
        nullable = false,
        length = 30
    )
    private String unit;

    @Column(
        name = "notes",
        length = 1000
    )
    private String notes;
}
