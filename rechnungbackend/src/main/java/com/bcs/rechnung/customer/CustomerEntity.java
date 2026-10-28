package com.bcs.rechnung.customer;

import com.bcs.rechnung.company.CompanyEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * JPA-Entity für einen Kunden.
 *
 * <p>
 * Die Entity repräsentiert einen Datensatz der
 * SQL-Server-Tabelle {@code customer}.
 * </p>
 *
 * <p>
 * Neben den allgemeinen Kundenstammdaten enthält die Entity
 * auch optionale Informationen für die elektronische
 * Rechnungsstellung und XRechnung.
 * </p>
 */
@Entity
@Table(
    name = "customer",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_customer_customer_number",
            columnNames = "customer_number"
        )
    }
)
@Getter
@Setter
public class CustomerEntity {

    /**
     * Technische Primärschlüssel-ID des Kunden.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;


    // =========================================================
    // Firma
    // =========================================================

    /**
     * Firma, der dieser Kunde zugeordnet ist.
     * Die Fremdschlüsselspalte in der Tabelle customer
     * lautet company_id.
     */
    @ManyToOne(
        fetch = FetchType.LAZY,
        optional = false
    )
    @JoinColumn(
        name = "company_id",
        nullable = false
    )
    private CompanyEntity company;


    // =========================================================
    // Kundenstammdaten
    // =========================================================

    /**
     * Eindeutige Kundennummer.
     *
     * Beispiel:
     * K100000
     */
    @Column(
        name = "customer_number",
        nullable = false,
        length = 30
    )
    private String customerNumber;


    /**
     * Name des Kunden.
     *
     * Beispiel:
     * Muster GmbH
     */
    @Column(
        name = "name",
        nullable = false,
        length = 200
    )
    private String name;


    /**
     * Ansprechpartner beim Kunden.
     */
    @Column(
        name = "contact_person",
        length = 200
    )
    private String contactPerson;


    /**
     * Straße und Hausnummer.
     */
    @Column(
        name = "street",
        length = 200
    )
    private String street;


    /**
     * Postleitzahl.
     */
    @Column(
        name = "zip_code",
        length = 20
    )
    private String zipCode;


    /**
     * Ort.
     */
    @Column(
        name = "city",
        length = 100
    )
    private String city;


    /**
     * Land bzw. Ländercode.
     *
     * Beispiel:
     * DE
     */
    @Column(
        name = "country",
        length = 100
    )
    private String country;


    /**
     * Telefonnummer.
     */
    @Column(
        name = "phone",
        length = 100
    )
    private String phone;


    /**
     * E-Mail-Adresse.
     */
    @Column(
        name = "email",
        length = 200
    )
    private String email;


    /**
     * Umsatzsteuer-Identifikationsnummer.
     *
     * Beispiel:
     * DE123456789
     */
    @Column(
        name = "vat_id",
        length = 50
    )
    private String vatId;


    /**
     * Kennzeichnet, ob der Kunde aktiv ist.
     */
    @Column(
        name = "active",
        nullable = false
    )
    private boolean active = true;


    // =========================================================
    // XRechnung / elektronische Rechnungsstellung
    // =========================================================

    /**
     * Leitweg-ID des Rechnungsempfängers.
     *
     * Wird insbesondere bei öffentlichen Auftraggebern
     * für die elektronische Rechnungsstellung verwendet.
     *
     * Beispiel:
     * 09184-12345-67
     */
    @Column(
        name = "leitweg_id",
        length = 100
    )
    private String leitwegId;


    /**
     * Elektronische Adresse des Rechnungsempfängers.
     *
     * Das zugehörige Identifikationsschema befindet sich
     * in electronicAddressScheme.
     */
    @Column(
        name = "electronic_address",
        length = 200
    )
    private String electronicAddress;


    /**
     * Schema der elektronischen Adresse.
     *
     * Beispiel:
     * 0204
     */
    @Column(
        name = "electronic_address_scheme",
        length = 20
    )
    private String electronicAddressScheme;


    /**
     * Geschäftsprozess-ID für die elektronische
     * Rechnungsstellung.
     */
    @Column(
        name = "business_process_id",
        length = 250
    )
    private String businessProcessId;


    /**
     * Käuferreferenz.
     *
     * Bei bestimmten Rechnungsempfängern kann hier
     * beispielsweise eine Leitweg-ID verwendet werden.
     */
    @Column(
        name = "buyer_reference",
        length = 200
    )
    private String buyerReference;


    /**
     * Abteilung bzw. Organisationseinheit
     * des Rechnungsempfängers.
     *
     * Beispiele:
     * Buchhaltung
     * Zentrale Rechnungsstelle
     * Einkauf
     */
    @Column(
        name = "department",
        length = 200
    )
    private String department;
}
