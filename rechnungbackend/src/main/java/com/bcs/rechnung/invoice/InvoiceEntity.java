package com.bcs.rechnung.invoice;

import com.bcs.rechnung.bank_account.BankAccountEntity;
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
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "invoice")
public class InvoiceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
        name = "invoice_number",
        nullable = false,
        length = 30,
        unique = true
    )
    private String invoiceNumber;

    @Column(
        name = "invoice_date",
        nullable = false
    )
    private LocalDate invoiceDate;

    @Column(
        name = "service_date"
    )
    private LocalDate serviceDate;

    @Column(
        name = "due_date",
        nullable = false
    )
    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    @Column(
        name = "status",
        nullable = false,
        length = 30
    )
    private InvoiceStatus status =
        InvoiceStatus.DRAFT;

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

    @ManyToOne(
        fetch = FetchType.LAZY,
        optional = false
    )
    @JoinColumn(
        name = "bank_account_id",
        nullable = false
    )
    private BankAccountEntity bankAccount;

    /*
     * Snapshot Kundendaten
     */

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

    /*
     * Snapshot Bankdaten
     */

    @Column(
        name = "bank_name",
        nullable = false,
        length = 200
    )
    private String bankName;

    @Column(
        name = "iban",
        nullable = false,
        length = 34
    )
    private String iban;

    @Column(
        name = "bic",
        nullable = false,
        length = 11
    )
    private String bic;

    /*
     * Zahlungsbedingungen
     */

    @Column(
        name = "payment_term_days",
        nullable = false
    )
    private Integer paymentTermDays;

    @Column(
        name = "discount_percent",
        nullable = false,
        precision = 7,
        scale = 4
    )
    private BigDecimal discountPercent =
        BigDecimal.ZERO;

    @Column(
        name = "discount_days",
        nullable = false
    )
    private Integer discountDays = 0;

    /*
     * Summen
     */

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

    /*
     * XRechnung / ZUGFeRD
     */

    @Column(
        name = "buyer_reference",
        length = 100
    )
    private String buyerReference;

    @Column(
        name = "order_reference",
        length = 100
    )
    private String orderReference;

    @Column(
        name = "contract_reference",
        length = 100
    )
    private String contractReference;

    @Column(
        name = "project_reference",
        length = 100
    )
    private String projectReference;

    @Column(
        name = "delivery_note_reference",
        length = 100
    )
    private String deliveryNoteReference;

    @Column(
        name = "customer_electronic_address",
        length = 255
    )
    private String customerElectronicAddress;

    @Column(
        name = "customer_electronic_address_scheme",
        length = 20
    )
    private String customerElectronicAddressScheme;

    /**
     * 380 = Rechnung
     * 381 = Gutschrift
     * 384 = Korrekturrechnung
     */
    @Column(
        name = "invoice_type_code",
        nullable = false,
        length = 10
    )
    private String invoiceTypeCode = "380";

    /**
     * 58 = SEPA Überweisung
     */
    @Column(
        name = "payment_means_code",
        nullable = false,
        length = 10
    )
    private String paymentMeansCode = "58";

    /**
     * S = Standard VAT
     * Z = Steuerfrei
     * E = Exempt
     */
    @Column(
        name = "tax_category_code",
        nullable = false,
        length = 10
    )
    private String taxCategoryCode = "S";

    /**
     * ISO 4217
     */
    @Column(
        name = "currency_code",
        nullable = false,
        length = 3
    )
    private String currencyCode = "EUR";

    /**
     * Steuerpunkt
     */
    @Column(
        name = "tax_point_date"
    )
    private LocalDate taxPointDate;

    /**
     * Zahlungsreferenz
     */
    @Column(
        name = "payment_reference",
        length = 100
    )
    private String paymentReference;

    /**
     * Verkäuferreferenz
     */
    @Column(
        name = "seller_reference",
        length = 100
    )
    private String sellerReference;

    /**
     * Business Process Identifier
     */
    @Column(
        name = "business_process_id",
        length = 200
    )
    private String businessProcessId =
        "urn:fdc:peppol.eu:2017:poacc:billing:01:1.0";

    /**
     * EN16931 / XRechnung Kennung
     */
    @Column(
        name = "specification_identifier",
        length = 300
    )
    private String specificationIdentifier =
        "urn:xeinkauf.de:kosit:xrechnung_3.0";

    /**
     * Leitweg-ID Snapshot
     */
    @Column(
        name = "customer_leitweg_id",
        length = 100
    )
    private String customerLeitwegId;

//    @Column(
//        name = "product_name",
//        nullable = false,
//        length = 200
//    )
//    private String productName;

    /*
     * Sonstiges
     */

    @Column(
        name = "notes",
        length = 2000
    )
    private String notes;

    @OneToMany(
        mappedBy = "invoice",
        cascade = CascadeType.ALL,
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    @OrderBy("positionNumber ASC")
    private List<InvoiceItemEntity> items =
        new ArrayList<>();

    public void addItem(
        InvoiceItemEntity item
    ) {

        if (item == null) {
            return;
        }

        item.setInvoice(this);
        items.add(item);
    }

    public void removeItem(
        InvoiceItemEntity item
    ) {

        if (item == null) {
            return;
        }

        items.remove(item);
        item.setInvoice(null);
    }

    public void replaceItems(
        List<InvoiceItemEntity> newItems
    ) {

        for (InvoiceItemEntity existingItem : items) {
            existingItem.setInvoice(null);
        }

        items.clear();

        if (newItems == null) {
            return;
        }

        for (InvoiceItemEntity item : newItems) {
            addItem(item);
        }
    }
}
