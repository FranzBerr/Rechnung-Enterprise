package com.bcs.rechnung.product;

import com.bcs.rechnung.company.CompanyEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "product")
public class ProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(
        fetch = FetchType.LAZY,
        optional = false
    )
    @JoinColumn(
        name = "company_id",
        nullable = false
    )
    private CompanyEntity company;

    @Column(
        name = "product_number",
        nullable = false,
        length = 30
    )
    private String productNumber;

    @Column(
        nullable = false,
        length = 200
    )
    private String name;

    @Column(
        length = 1000
    )
    private String description;

    @Column(
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
        nullable = false
    )
    private boolean active = true;
}
