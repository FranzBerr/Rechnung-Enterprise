package com.bcs.rechnung.invoice;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "invoice_item")
@Getter
@Setter
@NoArgsConstructor
public class InvoiceItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //    @JsonIgnore
    @ManyToOne(
        fetch = FetchType.LAZY,
        optional = false
    )
    @JoinColumn(
        name = "invoice_id",
        nullable = false
    )
    private InvoiceEntity invoice;

    @Column(
        name = "position_number",
        nullable = false
    )
    private Integer positionNumber;

    @Column(
        name = "product_id"
    )
    private Long productId;

    @Column(
        name = "product_number",
        length = 30
    )
    private String productNumber;

    @Column(
        name = "product_name",
        length = 200
    )
    private String productName;


    @Column(
        name = "description",
        nullable = false,
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
        length = 20
    )
    private String unit;

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
    private BigDecimal discountPercent = BigDecimal.ZERO;

    @Column(
        name = "vat_rate",
        nullable = false,
        precision = 7,
        scale = 4
    )
    private BigDecimal vatRate;

    @Column(
        name = "line_net",
        nullable = false,
        precision = 19,
        scale = 2
    )
    private BigDecimal lineNet = BigDecimal.ZERO;


    @Column(
        name = "tax_amount",
        nullable = false,
        precision = 19,
        scale = 2
    )
    private BigDecimal taxAmount = BigDecimal.ZERO;

    @Column(
        name = "line_gross",
        nullable = false,
        precision = 19,
        scale = 2
    )
    private BigDecimal lineGross = BigDecimal.ZERO;

    @Column(
        name = "item_classification_code",
        length = 100
    )
    private String itemClassificationCode;

    @Column(
        name = "tax_category_code",
        nullable = false,
        length = 10
    )
    private String taxCategoryCode = "S";
}


