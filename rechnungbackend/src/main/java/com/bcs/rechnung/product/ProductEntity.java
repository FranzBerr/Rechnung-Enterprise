package com.bcs.rechnung.product;

import com.bcs.rechnung.company.CompanyEntity;
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
    name = "product",
    uniqueConstraints = @UniqueConstraint(
        name = "UK_PRODUCT_PRODUCT_NUMBER",
        columnNames = "product_number"
    )
)
public class ProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "company_id", nullable = false)
    private CompanyEntity company;

    @Column(
        name = "product_number",
        nullable = false,
        length = 30
    )
    private String productNumber;

    @Column(
        name = "name",
        nullable = false,
        length = 200
    )
    private String name;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(
        name = "unit",
        nullable = false,
        length = 30
    )
    private String unit;

    @Column(
        name = "net_price",
        nullable = false,
        precision = 19,
        scale = 4
    )
    private BigDecimal netPrice;

    @Column(
        name = "vat_rate",
        nullable = false,
        precision = 5, scale = 2
    )
    private BigDecimal vatRate;

    @Column(name = "active", nullable = false)
    private boolean active = true;
}
