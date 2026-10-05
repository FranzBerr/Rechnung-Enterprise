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
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id")
    private InvoiceEntity invoice;

    private Integer positionNumber;
    private Long productId;
    private String productNumber;
    private String description;
    private BigDecimal quantity;
    private String unit;
    private BigDecimal unitPriceNet;
    private BigDecimal discountPercent;
    private BigDecimal vatRate;
    private BigDecimal lineNet;
    private BigDecimal taxAmount;
    private BigDecimal lineGross;
}
