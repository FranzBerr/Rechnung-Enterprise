package com.bcs.rechnung.offer;

import com.bcs.rechnung.product.ProductEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
    name = "offer_item",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "UK_OFFER_ITEM_POSITION",
            columnNames = {
                "offer_id",
                "position_number"
            }
        )
    }
)
public class OfferItemEntity {

    @Id
    @GeneratedValue(
        strategy = GenerationType.IDENTITY
    )
    private Long id;

    // ---------------------------------------------------------
    // Angebot
    // ---------------------------------------------------------

    @ManyToOne(
        fetch = FetchType.LAZY,
        optional = false
    )
    @JoinColumn(
        name = "offer_id",
        nullable = false
    )
    private OfferEntity offer;

    @Column(
        name = "position_number",
        nullable = false
    )
    private Integer positionNumber;

    // ---------------------------------------------------------
    // Produktreferenz
    // ---------------------------------------------------------

    @ManyToOne(
        fetch = FetchType.LAZY
    )
    @JoinColumn(
        name = "product_id"
    )
    private ProductEntity product;

    // ---------------------------------------------------------
    // Produkt-Snapshot
    // ---------------------------------------------------------

    @Column(
        name = "product_number",
        length = 30
    )
    private String productNumber;

    @Column(
        name = "product_name",
        nullable = false,
        length = 200
    )
    private String productName;

    @Column(
        name = "description",
        length = 1000
    )
    private String description;

    // ---------------------------------------------------------
    // Menge / Einheit
    // ---------------------------------------------------------

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

    // ---------------------------------------------------------
    // Preis
    // ---------------------------------------------------------

    @Column(
        name = "unit_price_net",
        nullable = false,
        precision = 19,
        scale = 4
    )
    private BigDecimal unitPriceNet;

    @Column(
        name = "discount_percent",
        nullable = false,
        precision = 7,
        scale = 4
    )
    private BigDecimal discountPercent =
        BigDecimal.ZERO;

    @Column(
        name = "vat_rate",
        nullable = false,
        precision = 7,
        scale = 4
    )
    private BigDecimal vatRate;

    // ---------------------------------------------------------
    // Positionssummen
    // ---------------------------------------------------------

    @Column(
        name = "line_net",
        nullable = false,
        precision = 19,
        scale = 2
    )
    private BigDecimal lineNet =
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
        name = "line_gross",
        nullable = false,
        precision = 19,
        scale = 2
    )
    private BigDecimal lineGross =
        BigDecimal.ZERO;

    // ---------------------------------------------------------
    // Optionale Position
    // ---------------------------------------------------------

    @Column(
        name = "optional",
        nullable = false
    )
    private boolean optional = false;

    // ---------------------------------------------------------
    // Sonstiges
    // ---------------------------------------------------------

    @Column(
        name = "notes",
        length = 1000
    )
    private String notes;
}
