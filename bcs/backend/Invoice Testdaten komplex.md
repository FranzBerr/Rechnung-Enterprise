# Invoice Testdaten komplex

Für deinen Testfall ist ein SQL-Skript sinnvoll, das einen neuen Rechnungsdatensatz plus 20 Positionen erzeugt. Ich habe die Beträge so berechnet, dass 7 % und 19 % MwSt. vorkommen, Position 5 einen Rabatt von 10 % enthält und Position 19/20 als optionale Positionen gekennzeichnet sind.

```Sql
USE [Firma];
GO

SET NOCOUNT ON;
SET XACT_ABORT ON;
GO

BEGIN TRY

    BEGIN TRANSACTION;

    DECLARE @InvoiceId BIGINT;
    DECLARE @InvoiceNumber VARCHAR(50) =
        'RE-2026-000074';

    DECLARE @SubtotalNet DECIMAL(19, 2);
    DECLARE @TaxAmount DECIMAL(19, 2);
    DECLARE @TotalGross DECIMAL(19, 2);

    /*
     * Verhindert eine doppelte Anlage
     * derselben Testrechnung.
     */
    IF EXISTS
    (
        SELECT 1
        FROM dbo.invoice
        WHERE invoice_number = @InvoiceNumber
    )
    BEGIN
        THROW 50001,
            'Die Rechnung RE-2026-000074 ist bereits vorhanden.',
            1;
    END;

    /*
     * Temporäre Positionsdaten.
     *
     * Die Felder line_net, tax_amount und line_gross
     * werden weiter unten automatisch berechnet.
     */
    DECLARE @InvoiceItems TABLE
    (
        product_id BIGINT NOT NULL,
        position_number INT NOT NULL,
        product_number VARCHAR(100) NOT NULL,
        description NVARCHAR(4000) NOT NULL,
        quantity DECIMAL(19, 4) NOT NULL,
        unit VARCHAR(10) NOT NULL,
        unit_price_net DECIMAL(19, 4) NOT NULL,
        discount_percent DECIMAL(7, 4) NOT NULL,
        vat_rate DECIMAL(7, 4) NOT NULL,
        line_net DECIMAL(19, 2) NULL,
        tax_amount DECIMAL(19, 2) NULL,
        line_gross DECIMAL(19, 2) NULL,
        tax_category_code VARCHAR(10) NOT NULL,
        item_classification_code VARCHAR(100) NULL,
        product_name NVARCHAR(255) NOT NULL
    );

    /*
     * ---------------------------------------------------------
     * 20 reguläre Rechnungspositionen
     *
     * MwSt.-Sätze:
     * 7 %
     * 19 %
     *
     * Position 5:
     * 10 % Positionsrabatt
     * ---------------------------------------------------------
     */
    INSERT INTO @InvoiceItems
    (
        product_id,
        position_number,
        product_number,
        description,
        quantity,
        unit,
        unit_price_net,
        discount_percent,
        vat_rate,
        tax_category_code,
        item_classification_code,
        product_name
    )
    VALUES
    (
        1,
        1,
        'P1000',
        N'IT Beratung WinDev
Projektvertrag 47352-30490
Analyse der bestehenden Anwendung
Beratung und technische Abstimmung',
        8.0000,
        'STD',
        88.0000,
        0.0000,
        19.0000,
        'S',
        NULL,
        N'Beratung WinDev'
    ),
    (
        2,
        2,
        'P1001',
        N'Softwareentwicklung Java
Implementierung von Geschäftslogik
Spring Boot Backend',
        6.0000,
        'STD',
        110.0000,
        0.0000,
        19.0000,
        'S',
        '81111500',
        N'Softwareentwicklung Java'
    ),
    (
        5,
        3,
        'SRV-001',
        N'Architektur und Design des Systems
Analyse der Anforderungen
Erstellung des technischen Lösungskonzepts',
        4.0000,
        'HUR',
        125.0000,
        0.0000,
        19.0000,
        'S',
        '81101500',
        N'Architektur- und Beratungsleistungen'
    ),
    (
        6,
        4,
        'SRV-002',
        N'Implementierung XRechnung
Einbindung der Rechnungsdaten
Erzeugung der XML-Struktur',
        5.0000,
        'HUR',
        100.0000,
        0.0000,
        19.0000,
        'S',
        '81111500',
        N'Implementierung XRechnung'
    ),
    (
        1,
        5,
        'SRV-003',
        N'SQL Server Optimierung
Analyse bestehender Abfragen
Optimierung von Indizes
10 Prozent Positionsrabatt',
        3.0000,
        'HUR',
        95.0000,
        10.0000,
        19.0000,
        'S',
        '81111500',
        N'SQL Server Optimierung'
    ),
    (
        1,
        6,
        'MAT-001',
        N'Fachbuch Softwarearchitektur
Referenzwerk für das Projektteam',
        2.0000,
        'C62',
        49.9000,
        0.0000,
        7.0000,
        'S',
        NULL,
        N'Fachbuch Softwarearchitektur'
    ),
    (
        1,
        7,
        'MAT-002',
        N'Technische Dokumentation
Gedruckte Projektdokumentation',
        5.0000,
        'C62',
        18.0000,
        0.0000,
        7.0000,
        'S',
        NULL,
        N'Technische Dokumentation'
    ),
    (
        1,
        8,
        'SRV-004',
        N'PDFBox Anpassungen
Mehrseitige Rechnung
Positionstabellen und Seitenfuß',
        4.0000,
        'HUR',
        92.0000,
        0.0000,
        19.0000,
        'S',
        '81111500',
        N'PDFBox Anpassungen'
    ),
    (
        1,
        9,
        'SRV-005',
        N'React Frontend Entwicklung
Rechnungsmaske und Positionsverwaltung',
        6.0000,
        'HUR',
        105.0000,
        0.0000,
        19.0000,
        'S',
        '81111500',
        N'React Frontend Entwicklung'
    ),
    (
        1,
        10,
        'SRV-006',
        N'Spring Boot Backend Entwicklung
Service-Schicht
REST API und Validierung',
        6.0000,
        'HUR',
        112.0000,
        0.0000,
        19.0000,
        'S',
        '81111500',
        N'Spring Boot Backend Entwicklung'
    ),
    (
        1,
        11,
        'MAT-003',
        N'Schulungshandbuch
Gedrucktes Benutzerhandbuch',
        10.0000,
        'C62',
        12.5000,
        0.0000,
        7.0000,
        'S',
        NULL,
        N'Schulungshandbuch'
    ),
    (
        1,
        12,
        'SRV-007',
        N'XRechnung Validierung
Prüfung der generierten XML-Dokumente
Auswertung der Validierungsberichte',
        3.0000,
        'HUR',
        98.0000,
        0.0000,
        19.0000,
        'S',
        '81111500',
        N'XRechnung Validierung'
    ),
    (
        1,
        13,
        'SRV-008',
        N'KoSIT Integration
Anbindung des Validators
Erzeugung des Validierungsberichts',
        4.0000,
        'HUR',
        102.0000,
        0.0000,
        19.0000,
        'S',
        '81111500',
        N'KoSIT Integration'
    ),
    (
        1,
        14,
        'SRV-009',
        N'Datenbankmigration
Migration der bestehenden Rechnungsdaten
Kontrolle der übernommenen Datensätze',
        5.0000,
        'HUR',
        97.0000,
        0.0000,
        19.0000,
        'S',
        '81111500',
        N'Datenbankmigration'
    ),
    (
        1,
        15,
        'MAT-004',
        N'Referenzhandbuch Datenbank
Gedruckte technische Unterlagen',
        3.0000,
        'C62',
        35.0000,
        0.0000,
        7.0000,
        'S',
        NULL,
        N'Referenzhandbuch Datenbank'
    ),
    (
        1,
        16,
        'SRV-010',
        N'Deployment und Konfiguration
Installation der Anwendung
Konfiguration der Produktivumgebung',
        3.0000,
        'HUR',
        90.0000,
        0.0000,
        19.0000,
        'S',
        '81111500',
        N'Deployment und Konfiguration'
    ),
    (
        1,
        17,
        'SRV-011',
        N'Abnahmetest
Durchführung der fachlichen Tests
Dokumentation der Testergebnisse',
        4.0000,
        'HUR',
        85.0000,
        0.0000,
        19.0000,
        'S',
        '81111500',
        N'Abnahmetest'
    ),
    (
        1,
        18,
        'SRV-012',
        N'Projektabschluss und Dokumentation
Übergabe der technischen Unterlagen
Abschlussbesprechung',
        2.0000,
        'HUR',
        80.0000,
        0.0000,
        19.0000,
        'S',
        '81111500',
        N'Projektabschluss und Dokumentation'
    ),
    (
        1,
        19,
        'SRV-013',
        N'Erweiterte Anwenderschulung
Durchführung eines zusätzlichen Schulungstages
Praktische Übungen mit der Rechnungsanwendung',
        1.0000,
        'C62',
        650.0000,
        0.0000,
        19.0000,
        'S',
        '86101601',
        N'Erweiterte Anwenderschulung'
    ),
    (
        1,
        20,
        'MAT-005',
        N'Zusätzliche Dokumentationspakete
Gedruckte Referenzunterlagen
Unterlagen für das Projektteam',
        1.0000,
        'C62',
        180.0000,
        0.0000,
        7.0000,
        'S',
        NULL,
        N'Zusätzliche Dokumentationspakete'
    );

    /*
     * Positionsnettobeträge berechnen.
     *
     * Formel:
     *
     * Menge × Einzelpreis
     * minus Positionsrabatt
     */
    UPDATE @InvoiceItems
    SET line_net =
        ROUND
        (
            quantity
            * unit_price_net
            * (
                1
                - discount_percent / 100
            ),
            2
        );

    /*
     * Steuerbetrag auf Basis des bereits
     * rabattierten Positionsnettobetrags.
     */
    UPDATE @InvoiceItems
    SET tax_amount =
        ROUND
        (
            line_net
            * vat_rate
            / 100,
            2
        );

    /*
     * Positionsbruttobetrag.
     */
    UPDATE @InvoiceItems
    SET line_gross =
        line_net
        + tax_amount;

    /*
     * Rechnungssummen aus den berechneten
     * Positionswerten bestimmen.
     */
    SELECT
        @SubtotalNet =
            SUM(line_net),
        @TaxAmount =
            SUM(tax_amount),
        @TotalGross =
            SUM(line_gross)
    FROM @InvoiceItems;

    /*
     * Rechnungskopf anlegen.
     */
    INSERT INTO dbo.invoice
    (
        invoice_number,
        invoice_date,
        service_date,
        due_date,
        status,

        company_id,
        customer_id,
        bank_account_id,

        customer_number,
        customer_name,
        customer_street,
        customer_zip_code,
        customer_city,
        customer_country,
        customer_vat_id,

        bank_name,
        iban,
        bic,

        payment_term_days,
        discount_percent,
        discount_days,

        subtotal_net,
        tax_amount,
        total_gross,

        notes,

        buyer_reference,
        order_reference,
        contract_reference,
        project_reference,
        delivery_note_reference,

        customer_leitweg_id,
        customer_electronic_address,
        customer_electronic_address_scheme,

        invoice_type_code,
        payment_means_code,
        tax_category_code,
        currency_code,

        tax_point_date,
        payment_reference,
        seller_reference,

        business_process_id,
        specification_identifier,

        archived,
        archived_at,
        archive_path
    )
    VALUES
    (
        @InvoiceNumber,
        '2026-10-27',
        '2026-10-27',
        '2026-11-26',
        'CREATED',

        1,
        1,
        1,

        'K1145',
        'ProTip AG',
        'Moitzfeld 96',
        '51429',
        'Bergisch Gladbach',
        'Deutschland',
        NULL,

        'Sparkasse Fürstenfeldbruck',
        'DE61 7005 3070 0008 0545 95',
        'BYLADEM1FFB',

        30,
        2.0000,
        10,

        @SubtotalNet,
        @TaxAmount,
        @TotalGross,

        N'Testrechnung mit 20 Positionen, mehreren Mehrwertsteuersätzen und einem Positionsrabatt.',

        '47352-30490',
        '47352-30490',
        NULL,
        '47352-30490',
        NULL,

        '47352-30490',
        'buchhaltung@protip.de',
        'EM',

        '380',
        '58',
        'S',
        'EUR',

        NULL,
        @InvoiceNumber,
        NULL,

        'urn:fdc:peppol.eu:2017:poacc:billing:01:1.0',
        'urn:xeinkauf.de:kosit:xrechnung_3.0',

        0,
        NULL,
        NULL
    );

    SET @InvoiceId =
        SCOPE_IDENTITY();

    /*
     * Berechnete Positionen übernehmen.
     */
    INSERT INTO dbo.invoice_item
    (
        invoice_id,
        product_id,
        position_number,
        product_number,
        description,
        quantity,
        unit,
        unit_price_net,
        discount_percent,
        vat_rate,
        line_net,
        tax_amount,
        line_gross,
        tax_category_code,
        item_classification_code,
        product_name
    )
    SELECT
        @InvoiceId,
        product_id,
        position_number,
        product_number,
        description,
        quantity,
        unit,
        unit_price_net,
        discount_percent,
        vat_rate,
        line_net,
        tax_amount,
        line_gross,
        tax_category_code,
        item_classification_code,
        product_name
    FROM @InvoiceItems;

    /*
     * Rechnungskopf kontrollieren.
     */
    SELECT
        id,
        invoice_number,
        invoice_date,
        due_date,
        payment_term_days,
        discount_percent,
        discount_days,
        subtotal_net,
        tax_amount,
        total_gross
    FROM dbo.invoice
    WHERE id = @InvoiceId;

    /*
     * Alle 20 Positionen kontrollieren.
     */
    SELECT
        position_number,
        product_number,
        product_name,
        quantity,
        unit,
        unit_price_net,
        discount_percent,
        vat_rate,
        line_net,
        tax_amount,
        line_gross
    FROM dbo.invoice_item
    WHERE invoice_id = @InvoiceId
    ORDER BY position_number;

    /*
     * Steueraufschlüsselung nach Steuersatz.
     */
    SELECT
        vat_rate,
        SUM(line_net) AS tax_basis,
        SUM(tax_amount) AS tax_amount,
        SUM(line_gross) AS gross_amount
    FROM dbo.invoice_item
    WHERE invoice_id = @InvoiceId
    GROUP BY vat_rate
    ORDER BY vat_rate;

    /*
     * Summenkontrolle zwischen Rechnungskopf
     * und Rechnungspositionen.
     */
    SELECT
        i.subtotal_net AS invoice_subtotal_net,
        positions.calculated_subtotal_net,

        i.tax_amount AS invoice_tax_amount,
        positions.calculated_tax_amount,

        i.total_gross AS invoice_total_gross,
        positions.calculated_total_gross
    FROM dbo.invoice i
    CROSS APPLY
    (
        SELECT
            SUM(ii.line_net) AS calculated_subtotal_net,
            SUM(ii.tax_amount) AS calculated_tax_amount,
            SUM(ii.line_gross) AS calculated_total_gross
        FROM dbo.invoice_item ii
        WHERE ii.invoice_id = i.id
    ) positions
    WHERE i.id = @InvoiceId;

    COMMIT TRANSACTION;

END TRY
BEGIN CATCH

    IF @@TRANCOUNT > 0
        ROLLBACK TRANSACTION;

    THROW;

END CATCH;
GO
```
Bei unveränderten Werten ergeben sich:
```Code
Nettobetrag:   7.497,30 EUR
MwSt.-Betrag:  1.352,52 EUR
Gesamtbetrag:  8.849,82 EUR
```
