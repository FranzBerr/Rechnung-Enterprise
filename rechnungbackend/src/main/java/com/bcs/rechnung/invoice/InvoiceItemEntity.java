package com.bcs.rechnung.invoice;

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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "invoice_item")
public class InvoiceItemEntity {

    private static final int MONEY_SCALE = 2;

    private static final int CALCULATION_SCALE = 8;

    private static final RoundingMode MONEY_ROUNDING =
        RoundingMode.HALF_UP;

    @Id
    @GeneratedValue(
        strategy = GenerationType.IDENTITY
    )
    private Long id;

    // ---------------------------------------------------------
    // Rechnung
    // ---------------------------------------------------------

    @ManyToOne(
        fetch = FetchType.LAZY,
        optional = false
    )
    @JoinColumn(
        name = "invoice_id",
        nullable = false
    )
    private InvoiceEntity invoice;

    // ---------------------------------------------------------
    // Produkt
    //
    // Das Produkt dient nur als Referenz zum Produktstamm.
    // Die abrechnungsrelevanten Produktdaten werden zusätzlich
    // als Snapshot in der Rechnungsposition gespeichert.
    // ---------------------------------------------------------

    @ManyToOne(
        fetch = FetchType.LAZY
    )
    @JoinColumn(
        name = "product_id"
    )
    private ProductEntity product;

    // ---------------------------------------------------------
    // Positionsdaten
    // ---------------------------------------------------------

    @Column(
        name = "position_number",
        nullable = false
    )
    private Integer positionNumber;

    // ---------------------------------------------------------
    // Produkt-Snapshot
    // ---------------------------------------------------------

    @Column(
        name = "product_number",
        length = 100
    )
    private String productNumber;

    @Column(
        name = "product_name",
        length = 255
    )
    private String productName;

    @Column(
        name = "description",
        nullable = false,
        length = 4000
    )
    private String description;

    @Column(
        name = "quantity",
        nullable = false,
        precision = 19,
        scale = 4
    )
    private BigDecimal quantity =
        BigDecimal.ZERO;

    @Column(
        name = "unit",
        nullable = false,
        length = 30
    )
    private String unit;

    @Column(
        name = "unit_price_net",
        nullable = false,
        precision = 19,
        scale = 4
    )
    private BigDecimal unitPriceNet =
        BigDecimal.ZERO;

    // ---------------------------------------------------------
    // Positionsrabatt
    // ---------------------------------------------------------

    @Column(
        name = "discount_percent",
        nullable = false,
        precision = 7,
        scale = 4
    )
    private BigDecimal discountPercent =
        BigDecimal.ZERO;

    // ---------------------------------------------------------
    // Umsatzsteuer
    // ---------------------------------------------------------

    @Column(
        name = "vat_rate",
        nullable = false,
        precision = 7,
        scale = 4
    )
    private BigDecimal vatRate =
        BigDecimal.ZERO;

    /**
     * Steuerkategorie für XRechnung.
     *
     * Beispiele:
     *
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

    // ---------------------------------------------------------
    // Klassifizierung
    // ---------------------------------------------------------

    @Column(
        name = "item_classification_code",
        length = 100
    )
    private String itemClassificationCode;

    // ---------------------------------------------------------
    // Berechnete Positionssummen
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
    // Positionsberechnung
    // ---------------------------------------------------------

    /**
     * Berechnet sämtliche abhängigen Beträge der Position.
     *
     * Reihenfolge:
     *
     * Menge x Netto-Einzelpreis
     * - Positionsrabatt
     * = Positionsnetto
     *
     * Positionsnetto x MwSt.-Satz
     * = Steuerbetrag
     *
     * Positionsnetto + Steuerbetrag
     * = Positionsbrutto
     */
    public void recalculateAmounts() {

        validateCalculationValues();

        BigDecimal normalizedQuantity =
            zeroIfNull(
                quantity
            );

        BigDecimal normalizedUnitPrice =
            zeroIfNull(
                unitPriceNet
            );

        BigDecimal normalizedDiscountPercent =
            zeroIfNull(
                discountPercent
            );

        BigDecimal normalizedVatRate =
            zeroIfNull(
                vatRate
            );

        /*
         * Bruttobasis der Position vor Rabatt.
         *
         * Hier bewusst noch nicht auf zwei
         * Nachkommastellen runden.
         */
        BigDecimal lineBaseAmount =
            normalizedQuantity.multiply(
                normalizedUnitPrice
            );

        /*
         * Positionsrabatt berechnen.
         *
         * Beispiel:
         *
         * 285,00 EUR x 10 %
         * = 28,50 EUR
         */
        BigDecimal discountAmount =
            lineBaseAmount
                .multiply(
                    normalizedDiscountPercent
                )
                .divide(
                    BigDecimal.valueOf(
                        100
                    ),
                    CALCULATION_SCALE,
                    MONEY_ROUNDING
                );

        /*
         * Positionsnetto nach Abzug des Rabatts.
         *
         * BT-131 in der XRechnung basiert auf
         * diesem rabattierten Positionswert.
         */
        lineNet =
            money(
                lineBaseAmount.subtract(
                    discountAmount
                )
            );

        /*
         * Umsatzsteuer auf Basis des bereits
         * rabattierten Positionsnettos.
         */
        taxAmount =
            lineNet
                .multiply(
                    normalizedVatRate
                )
                .divide(
                    BigDecimal.valueOf(
                        100
                    ),
                    MONEY_SCALE,
                    MONEY_ROUNDING
                );

        /*
         * Positionsbrutto.
         */
        lineGross =
            money(
                lineNet.add(
                    taxAmount
                )
            );
    }

    // ---------------------------------------------------------
    // Produkt-Snapshot
    // ---------------------------------------------------------

    /**
     * Übernimmt die abrechnungsrelevanten Stammdaten eines
     * Produktes als Snapshot in die Rechnungsposition.
     *
     * Nachträgliche Änderungen am Produktstamm verändern damit
     * die gespeicherte Rechnungsposition nicht.
     */
    public void applyProductSnapshot(
        ProductEntity product
    ) {
        if (product == null) {
            return;
        }

        this.product =
            product;

        this.productNumber =
            product.getProductNumber();

        this.productName =
            product.getName();

        this.description =
            product.getDescription();

        this.unit =
            product.getUnit();

        this.unitPriceNet =
            zeroIfNull(
                product.getNetPrice()
            );

        this.vatRate =
            zeroIfNull(
                product.getVatRate()
            );

        /*
         * Diese beiden Werte stehen nur zur Verfügung,
         * wenn ProductEntity entsprechend erweitert wurde.
         */
        this.taxCategoryCode =
            defaultTaxCategory(
                product.getTaxCategoryCode()
            );

        this.itemClassificationCode =
            product.getItemClassificationCode();
    }

    // ---------------------------------------------------------
    // Konsistenzprüfung
    // ---------------------------------------------------------

    /**
     * Prüft, ob die momentan gespeicherten Positionssummen
     * mit Menge, Preis, Rabatt und Steuersatz übereinstimmen.
     *
     * Die Entity wird dabei nicht verändert.
     */
    public boolean hasConsistentAmounts() {

        if (!hasValidCalculationValues()) {
            return false;
        }

        BigDecimal baseAmount =
            quantity.multiply(
                unitPriceNet
            );

        BigDecimal calculatedDiscount =
            baseAmount
                .multiply(
                    zeroIfNull(
                        discountPercent
                    )
                )
                .divide(
                    BigDecimal.valueOf(
                        100
                    ),
                    CALCULATION_SCALE,
                    MONEY_ROUNDING
                );

        BigDecimal calculatedLineNet =
            money(
                baseAmount.subtract(
                    calculatedDiscount
                )
            );

        BigDecimal calculatedTaxAmount =
            calculatedLineNet
                .multiply(
                    zeroIfNull(
                        vatRate
                    )
                )
                .divide(
                    BigDecimal.valueOf(
                        100
                    ),
                    MONEY_SCALE,
                    MONEY_ROUNDING
                );

        BigDecimal calculatedLineGross =
            money(
                calculatedLineNet.add(
                    calculatedTaxAmount
                )
            );

        return money(
            lineNet
        ).compareTo(
            calculatedLineNet
        ) == 0
            && money(
            taxAmount
        ).compareTo(
            calculatedTaxAmount
        ) == 0
            && money(
            lineGross
        ).compareTo(
            calculatedLineGross
        ) == 0;
    }

    // ---------------------------------------------------------
    // Validierung der Berechnungswerte
    // ---------------------------------------------------------

    private void validateCalculationValues() {

        if (quantity == null) {
            throw new IllegalStateException(
                "Die Positionsmenge darf nicht null sein."
            );
        }

        if (quantity.compareTo(
            BigDecimal.ZERO
        ) <= 0) {
            throw new IllegalStateException(
                "Die Positionsmenge muss größer als 0 sein."
            );
        }

        if (unitPriceNet == null) {
            throw new IllegalStateException(
                "Der Netto-Einzelpreis darf nicht null sein."
            );
        }

        if (unitPriceNet.compareTo(
            BigDecimal.ZERO
        ) < 0) {
            throw new IllegalStateException(
                "Der Netto-Einzelpreis darf nicht negativ sein."
            );
        }

        if (discountPercent == null) {
            discountPercent =
                BigDecimal.ZERO;
        }

        if (discountPercent.compareTo(
            BigDecimal.ZERO
        ) < 0
            || discountPercent.compareTo(
            BigDecimal.valueOf(
                100
            )
        ) > 0) {

            throw new IllegalStateException(
                "Der Positionsrabatt muss zwischen 0 und 100 Prozent liegen."
            );
        }

        if (vatRate == null) {
            throw new IllegalStateException(
                "Der Umsatzsteuersatz darf nicht null sein."
            );
        }

        if (vatRate.compareTo(
            BigDecimal.ZERO
        ) < 0
            || vatRate.compareTo(
            BigDecimal.valueOf(
                100
            )
        ) > 0) {

            throw new IllegalStateException(
                "Der Umsatzsteuersatz muss zwischen 0 und 100 Prozent liegen."
            );
        }
    }

    private boolean hasValidCalculationValues() {

        if (quantity == null
            || quantity.compareTo(
            BigDecimal.ZERO
        ) <= 0) {

            return false;
        }

        if (unitPriceNet == null
            || unitPriceNet.compareTo(
            BigDecimal.ZERO
        ) < 0) {

            return false;
        }

        BigDecimal discount =
            zeroIfNull(
                discountPercent
            );

        if (discount.compareTo(
            BigDecimal.ZERO
        ) < 0
            || discount.compareTo(
            BigDecimal.valueOf(
                100
            )
        ) > 0) {

            return false;
        }

        if (vatRate == null
            || vatRate.compareTo(
            BigDecimal.ZERO
        ) < 0
            || vatRate.compareTo(
            BigDecimal.valueOf(
                100
            )
        ) > 0) {

            return false;
        }

        return true;
    }

    // ---------------------------------------------------------
    // Hilfsmethoden
    // ---------------------------------------------------------

    private BigDecimal money(
        BigDecimal value
    ) {
        return zeroIfNull(
            value
        ).setScale(
            MONEY_SCALE,
            MONEY_ROUNDING
        );
    }

    private BigDecimal zeroIfNull(
        BigDecimal value
    ) {
        return value == null
            ? BigDecimal.ZERO
            : value;
    }

    private String defaultTaxCategory(
        String value
    ) {
        if (value == null
            || value.isBlank()) {

            return "S";
        }

        return value.trim();
    }
}
