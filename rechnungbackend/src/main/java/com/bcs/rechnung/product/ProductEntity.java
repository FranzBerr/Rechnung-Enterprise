package com.bcs.rechnung.product;

import com.bcs.rechnung.company.CompanyEntity;
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
    name = "product",
    uniqueConstraints = @UniqueConstraint(
        name = "UK_PRODUCT_PRODUCT_NUMBER",
        columnNames = "product_number"
    )
)
public class ProductEntity {

    @Id
    @GeneratedValue(
        strategy = GenerationType.IDENTITY
    )
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
        name = "unit",
        nullable = false,
        length = 30
    )
    private String unit;

    /*
     * Netto-Preis des Produktes.
     *
     * In InvoiceItemEntity wird daraus beim Erstellen
     * der Rechnung ein Snapshot erzeugt.
     */
    @Column(
        name = "net_price",
        nullable = false,
        precision = 19,
        scale = 4
    )
    private BigDecimal netPrice =
        BigDecimal.ZERO;

    /*
     * Umsatzsteuersatz.
     *
     * Beispiele:
     * 7.00
     * 19.00
     */
    @Column(
        name = "vat_rate",
        nullable = false,
        precision = 5,
        scale = 2
    )
    private BigDecimal vatRate =
        BigDecimal.ZERO;

    /*
     * Steuerkategorie für XRechnung.
     *
     * Beispiele:
     * S = Standard rated
     * Z = Zero rated
     * E = Exempt
     */
    @Column(
        name = "tax_category_code",
        nullable = false,
        length = 10
    )
    private String taxCategoryCode =
        "S";

    /*
     * Klassifizierung des Produktes bzw.
     * der Dienstleistung für die XRechnung.
     */
    @Column(
        name = "item_classification_code",
        length = 100
    )
    private String itemClassificationCode;

    @Column(
        name = "active",
        nullable = false
    )
    private boolean active =
        true;
}
