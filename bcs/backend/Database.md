# Hier ist ein SQL-Server-Skript mit 30 Testdatensätzen für die Tabelle bank_account:

```Sql
INSERT INTO bank_account
(
    company_id,
    bank_name,
    account_holder,
    iban,
    bic,
    default_account,
    active
)
VALUES
(1, 'Deutsche Bank', 'Musterfirma GmbH', 'DE89370400440532013000', 'DEUTDEFF', 1, 1),
(1, 'Commerzbank', 'Musterfirma GmbH', 'DE75512108001245126199', 'COBADEFF', 0, 1),
(2, 'Sparkasse München', 'ABC Solutions GmbH', 'DE12500105170648489890', 'BYLADEM1001', 1, 1),
(2, 'HypoVereinsbank', 'ABC Solutions GmbH', 'DE21500500009876543210', 'HYVEDEMMXXX', 0, 1),
(3, 'Volksbank Bayern', 'Tech Consulting AG', 'DE44500105175407324931', 'GENODEF1M01', 1, 1),
(3, 'ING Deutschland', 'Tech Consulting AG', 'DE02500105179573024931', 'INGDDEFFXXX', 0, 1),
(4, 'Postbank', 'Logistik GmbH', 'DE34500105170648489891', 'PBNKDEFF', 1, 1),
(4, 'Santander Bank', 'Logistik GmbH', 'DE54500105170648489892', 'SCFBDE33XXX', 0, 1),
(5, 'Sparkasse Nürnberg', 'Handelspartner KG', 'DE64500105170648489893', 'BYLADEM1NUE', 1, 1),
(5, 'Deutsche Bank', 'Handelspartner KG', 'DE74500105170648489894', 'DEUTDEFF', 0, 0),

(6, 'Commerzbank', 'Industrie AG', 'DE84500105170648489895', 'COBADEFF', 1, 1),
(6, 'Volksbank Rhein', 'Industrie AG', 'DE94500105170648489896', 'GENODED1RHE', 0, 1),
(7, 'Sparkasse Köln', 'Bauunternehmen GmbH', 'DE10500105170648489897', 'COLSDE33', 1, 1),
(7, 'ING Deutschland', 'Bauunternehmen GmbH', 'DE11500105170648489898', 'INGDDEFFXXX', 0, 1),
(8, 'HypoVereinsbank', 'IT Services GmbH', 'DE12500105170648489899', 'HYVEDEMMXXX', 1, 1),
(8, 'Commerzbank', 'IT Services GmbH', 'DE13500105170648489900', 'COBADEFF', 0, 1),
(9, 'Volksbank Süd', 'Elektro Müller GmbH', 'DE14500105170648489901', 'GENODEF1SUD', 1, 1),
(9, 'Sparkasse Augsburg', 'Elektro Müller GmbH', 'DE15500105170648489902', 'BYLADEM1AUG', 0, 1),
(10, 'Deutsche Bank', 'Meier Consulting', 'DE16500105170648489903', 'DEUTDEFF', 1, 1),
(10, 'Postbank', 'Meier Consulting', 'DE17500105170648489904', 'PBNKDEFF', 0, 0),

(11, 'Commerzbank', 'Alpha GmbH', 'DE18500105170648489905', 'COBADEFF', 1, 1),
(12, 'Sparkasse Hamburg', 'Beta AG', 'DE19500105170648489906', 'HASPDEHHXXX', 1, 1),
(13, 'Volksbank Nord', 'Gamma GmbH', 'DE20500105170648489907', 'GENODEF1NRD', 1, 1),
(14, 'ING Deutschland', 'Delta Consulting', 'DE21500105170648489908', 'INGDDEFFXXX', 1, 1),
(15, 'Santander Bank', 'Epsilon GmbH', 'DE22500105170648489909', 'SCFBDE33XXX', 1, 0),

(16, 'Deutsche Bank', 'Zeta AG', 'DE23500105170648489910', 'DEUTDEFF', 1, 1),
(17, 'Sparkasse Frankfurt', 'Eta GmbH', 'DE24500105170648489911', 'HELADEF1822', 1, 1),
(18, 'Commerzbank', 'Theta KG', 'DE25500105170648489912', 'COBADEFF', 1, 1),
(19, 'Volksbank München', 'Iota Services GmbH', 'DE26500105170648489913', 'GENODEF1MUC', 1, 1),
(20, 'HypoVereinsbank', 'Kappa Solutions AG', 'DE27500105170648489914', 'HYVEDEMMXXX', 1, 1);
```

Diese 30 Datensätze passen direkt zu den zuvor erzeugten bank_account-Datensätzen mit company_id 1-30.
```Sql
INSERT INTO company
(
    company_name,
    street,
    postal_code,
    city,
    country,
    tax_number,
    vat_id
)
VALUES
('Musterfirma GmbH', 'Hauptstraße 1', '80331', 'München', 'Deutschland', '143/123/10001', 'DE100000001'),
('ABC Solutions GmbH', 'Industriestraße 12', '86150', 'Augsburg', 'Deutschland', '143/123/10002', 'DE100000002'),
('Tech Consulting AG', 'Bahnhofstraße 25', '90402', 'Nürnberg', 'Deutschland', '143/123/10003', 'DE100000003'),
('Logistik GmbH', 'Speditionsweg 8', '50667', 'Köln', 'Deutschland', '143/123/10004', 'DE100000004'),
('Handelspartner KG', 'Marktplatz 15', '70173', 'Stuttgart', 'Deutschland', '143/123/10005', 'DE100000005'),
('Industrie AG', 'Werkstraße 100', '44135', 'Dortmund', 'Deutschland', '143/123/10006', 'DE100000006'),
('Bauunternehmen GmbH', 'Baumeisterweg 10', '60549', 'Frankfurt am Main', 'Deutschland', '143/123/10007', 'DE100000007'),
('IT Services GmbH', 'Digitalpark 5', '28195', 'Bremen', 'Deutschland', '143/123/10008', 'DE100000008'),
('Elektro Müller GmbH', 'Stromweg 22', '01067', 'Dresden', 'Deutschland', '143/123/10009', 'DE100000009'),
('Meier Consulting', 'Beraterallee 7', '04109', 'Leipzig', 'Deutschland', '143/123/10010', 'DE100000010'),

('Alpha GmbH', 'Parkstraße 11', '24103', 'Kiel', 'Deutschland', '143/123/10011', 'DE100000011'),
('Beta AG', 'Schillerstraße 18', '20095', 'Hamburg', 'Deutschland', '143/123/10012', 'DE100000012'),
('Gamma GmbH', 'Gewerbering 9', '99084', 'Erfurt', 'Deutschland', '143/123/10013', 'DE100000013'),
('Delta Consulting GmbH', 'Am Markt 3', '55116', 'Mainz', 'Deutschland', '143/123/10014', 'DE100000014'),
('Epsilon GmbH', 'Lindenweg 27', '39104', 'Magdeburg', 'Deutschland', '143/123/10015', 'DE100000015'),
('Zeta AG', 'Am Hafen 4', '18055', 'Rostock', 'Deutschland', '143/123/10016', 'DE100000016'),
('Eta GmbH', 'Tulpenstraße 6', '66111', 'Saarbrücken', 'Deutschland', '143/123/10017', 'DE100000017'),
('Theta KG', 'Kirchplatz 19', '54290', 'Trier', 'Deutschland', '143/123/10018', 'DE100000018'),
('Iota Services GmbH', 'Rosenweg 14', '37073', 'Göttingen', 'Deutschland', '143/123/10019', 'DE100000019'),
('Kappa Solutions AG', 'Innovationsallee 2', '76133', 'Karlsruhe', 'Deutschland', '143/123/10020', 'DE100000020'),

('Lambda Systems GmbH', 'Technologiepark 1', '93047', 'Regensburg', 'Deutschland', '143/123/10021', 'DE100000021'),
('Mu Engineering GmbH', 'Maschinenring 8', '97070', 'Würzburg', 'Deutschland', '143/123/10022', 'DE100000022'),
('Nu Automotive GmbH', 'Motorstraße 30', '85049', 'Ingolstadt', 'Deutschland', '143/123/10023', 'DE100000023'),
('Xi Logistik AG', 'Containerweg 77', '27568', 'Bremerhaven', 'Deutschland', '143/123/10024', 'DE100000024'),
('Omikron Medical GmbH', 'Klinikstraße 12', '79098', 'Freiburg', 'Deutschland', '143/123/10025', 'DE100000025'),
('Pi Software GmbH', 'Codeweg 21', '48143', 'Münster', 'Deutschland', '143/123/10026', 'DE100000026'),
('Rho Energie AG', 'Kraftwerksallee 1', '45127', 'Essen', 'Deutschland', '143/123/10027', 'DE100000027'),
('Sigma Retail GmbH', 'Einkaufspark 5', '56068', 'Koblenz', 'Deutschland', '143/123/10028', 'DE100000028'),
('Tau Pharma GmbH', 'Laborweg 9', '89073', 'Ulm', 'Deutschland', '143/123/10029', 'DE100000029'),
('Omega Holding AG', 'Vorstandsplatz 1', '10117', 'Berlin', 'Deutschland', '143/123/10030', 'DE100000030');
```

für 30 Produkte:
```SQL
INSERT INTO product
(
    product_number,
    name,
    description,
    unit,
    net_price,
    vat_rate,
    active
)
VALUES
('P-100001', 'Laptop Business 14', 'Business Notebook mit 14 Zoll Display und 16 GB RAM', 'Stk', 899.0000, 19.00, 1),
('P-100002', 'Monitor 27 Zoll', 'Full-HD Monitor für Büroarbeitsplätze', 'Stk', 199.9000, 19.00, 1),
('P-100003', 'Tastatur USB', 'Ergonomische USB-Tastatur', 'Stk', 29.9000, 19.00, 1),
('P-100004', 'Maus Wireless', 'Kabellose optische Maus', 'Stk', 19.9000, 19.00, 1),
('P-100005', 'Dockingstation USB-C', 'Universal Dockingstation für Notebooks', 'Stk', 149.0000, 19.00, 1),

('P-100006', 'Server Rack 1HE', '19 Zoll Rack-Server mit Intel Xeon CPU', 'Stk', 2899.0000, 19.00, 1),
('P-100007', 'NAS Speicher 8TB', 'Netzwerkspeicher für Backups und Archivierung', 'Stk', 649.0000, 19.00, 1),
('P-100008', 'Laserdrucker A4', 'Schwarzweiß-Laserdrucker für Büros', 'Stk', 249.9000, 19.00, 1),
('P-100009', 'Multifunktionsdrucker', 'Drucken, Scannen und Kopieren', 'Stk', 399.9000, 19.00, 1),
('P-100010', 'Netzwerkswitch 24 Port', 'Managed Gigabit Layer-2 Switch', 'Stk', 459.0000, 19.00, 1),

('P-100011', 'Router VPN', 'Unternehmensrouter mit VPN-Unterstützung', 'Stk', 329.0000, 19.00, 1),
('P-100012', 'Patchkabel CAT6 2m', 'Geschirmtes Netzwerkkabel', 'Stk', 4.9000, 19.00, 1),
('P-100013', 'Patchpanel 24 Port', '19 Zoll Patchpanel CAT6', 'Stk', 89.9000, 19.00, 1),
('P-100014', 'USV 1500VA', 'Unterbrechungsfreie Stromversorgung', 'Stk', 349.0000, 19.00, 1),
('P-100015', 'Externe SSD 1TB', 'Portable SSD mit USB-C Anschluss', 'Stk', 129.0000, 19.00, 1),

('P-100016', 'Office Lizenz', 'Jahreslizenz für Office-Anwendungen', 'Lizenz', 99.0000, 19.00, 1),
('P-100017', 'Windows Lizenz', 'Betriebssystemlizenz Windows Professional', 'Lizenz', 149.0000, 19.00, 1),
('P-100018', 'SQL Server Lizenz', 'Datenbankserver Lizenz', 'Lizenz', 2999.0000, 19.00, 1),
('P-100019', 'Virenschutz Enterprise', 'Sicherheitssoftware für Unternehmen', 'Lizenz', 39.9000, 19.00, 1),
('P-100020', 'Cloud Backup Paket', 'Monatlicher Cloud-Speicher-Service', 'Monat', 49.9000, 19.00, 1),

('P-100021', 'Beratungsleistung IT', 'IT-Beratung und Architekturdesign', 'Std', 120.0000, 19.00, 1),
('P-100022', 'Softwareentwicklung', 'Individuelle Softwareentwicklung', 'Std', 135.0000, 19.00, 1),
('P-100023', 'Projektmanagement', 'Projektleitung und Koordination', 'Std', 110.0000, 19.00, 1),
('P-100024', 'Support Standard', 'Technischer Support während Geschäftszeiten', 'Std', 85.0000, 19.00, 1),
('P-100025', 'Support Premium', '24x7 Bereitschaft und Support', 'Std', 145.0000, 19.00, 1),

('P-100026', 'Schreibtisch höhenverstellbar', 'Elektrisch höhenverstellbarer Schreibtisch', 'Stk', 699.0000, 19.00, 1),
('P-100027', 'Bürostuhl Ergonomisch', 'Ergonomischer Drehstuhl', 'Stk', 399.0000, 19.00, 1),
('P-100028', 'Aktenschrank Metall', 'Abschließbarer Büroschrank', 'Stk', 249.0000, 19.00, 1),
('P-100029', 'Konferenztisch', 'Besprechungstisch für 8 Personen', 'Stk', 899.0000, 19.00, 1),
('P-100030', 'Whiteboard Magnetisch', 'Magnetisches Whiteboard 180x120 cm', 'Stk', 179.0000, 19.00, 1);
```


# company

```Sql
CREATE TABLE company
(
    id BIGINT IDENTITY(1,1) PRIMARY KEY,

    name NVARCHAR(200) NOT NULL,

    legal_form NVARCHAR(200),

    tax_number NVARCHAR(100),

    vat_id NVARCHAR(50),

    street NVARCHAR(200),

    zip_code NVARCHAR(20),

    city NVARCHAR(100),

    country NVARCHAR(100),

    phone NVARCHAR(100),

    email NVARCHAR(200),

    website NVARCHAR(500),

    active BIT NOT NULL
        CONSTRAINT DF_COMPANY_ACTIVE DEFAULT(1)
);
```
# Banken:

Tabelle bank
```SQL
CREATE TABLE bank
(
    id BIGINT IDENTITY(1,1) NOT NULL
        CONSTRAINT PK_BANK PRIMARY KEY,

    bank_code NVARCHAR(20) NULL,

    bank_name NVARCHAR(200) NOT NULL,

    bic NVARCHAR(11) NOT NULL,

    country NVARCHAR(10) NULL,

    active BIT NOT NULL
        CONSTRAINT DF_BANK_ACTIVE DEFAULT(1)
);
GO

CREATE UNIQUE INDEX UX_BANK_BIC
    ON bank(bic);
GO
```
Tabelle bank_account
```Sql
CREATE TABLE bank_account
(
    id BIGINT IDENTITY(1,1) NOT NULL
        CONSTRAINT PK_BANK_ACCOUNT PRIMARY KEY,

    company_id BIGINT NOT NULL,

    bank_id BIGINT NOT NULL,

    account_holder NVARCHAR(200) NOT NULL,

    iban NVARCHAR(34) NOT NULL,

    default_account BIT NOT NULL
        CONSTRAINT DF_BANK_ACCOUNT_DEFAULT DEFAULT(0),

    active BIT NOT NULL
        CONSTRAINT DF_BANK_ACCOUNT_ACTIVE DEFAULT(1),

    CONSTRAINT FK_BANK_ACCOUNT_COMPANY
        FOREIGN KEY (company_id)
            REFERENCES company(id),

    CONSTRAINT FK_BANK_ACCOUNT_BANK
        FOREIGN KEY (bank_id)
            REFERENCES bank(id)
);
GO

CREATE UNIQUE INDEX UX_BANK_ACCOUNT_IBAN
    ON bank_account(iban);
GO

CREATE INDEX IX_BANK_ACCOUNT_COMPANY
    ON bank_account(company_id);
GO

CREATE INDEX IX_BANK_ACCOUNT_BANK
    ON bank_account(bank_id);
GO
```

Ergänze im BankAccountEntity
```Sql
@ManyToOne
@JoinColumn(name = "company_id")
private CompanyEntity company;

@ManyToOne
@JoinColumn(name = "bank_id")
private BankEntity bank;
```
# Testdaten für Banken

```Sql
INSERT INTO bank
(
    bank_code,
    bank_name,
    bic,
    country,
    active
)
VALUES
(
    '70020270',
    'HypoVereinsbank',
    'HYVEDEMMXXX',
    'DE',
    1
),
(
    '70040041',
    'Commerzbank',
    'COBADEFFXXX',
    'DE',
    1
),
(
    '50010517',
    'ING Deutschland',
    'INGDDEFFXXX',
    'DE',
    1
),
(
    '79050000',
    'Sparkasse Bad Neustadt',
    'BYLADEM1NES',
    'DE',
    1
);
GO
```
# Testdaten für Bankkonten

(Beispiel, wenn company.id = 1 existiert)
```Sql
INSERT INTO bank_account
(
    company_id,
    bank_id,
    account_holder,
    iban,
    default_account,
    active
)
VALUES
(
    1,
    1,
    'BCS GmbH',
    'DE02120300000000202051',
    1,
    1
),
(
    1,
    2,
    'BCS GmbH',
    'DE44500105175407324931',
    0,
    1
);
GO
```
table company
```Sql
CREATE TABLE company
(
    id BIGINT IDENTITY(1,1) NOT NULL
        CONSTRAINT PK_COMPANY PRIMARY KEY,

    name NVARCHAR(200) NOT NULL,

    legal_form NVARCHAR(200),

    tax_number NVARCHAR(100),

    vat_id NVARCHAR(50),

    street NVARCHAR(200),

    zip_code NVARCHAR(20),

    city NVARCHAR(100),

    country NVARCHAR(100),

    phone NVARCHAR(100),

    email NVARCHAR(200),

    website NVARCHAR(500),

    active BIT NOT NULL
        CONSTRAINT DF_COMPANY_ACTIVE DEFAULT(1)
);
```
# Erweiterung für customer Tabelle
```SQL
USE [Firma];
GO

SET NOCOUNT ON;
SET XACT_ABORT ON;

BEGIN TRANSACTION;

BEGIN TRY

    /* =========================================================
       100 Testkunden für company_id = 1
       Kundennummern: K110001 bis K110100
       ========================================================= */

    ;WITH Numbers AS
    (
        SELECT 1 AS n

        UNION ALL

        SELECT n + 1
        FROM Numbers
        WHERE n < 100
    )
    INSERT INTO [dbo].[customer]
    (
        [company_id],
        [customer_number],
        [name],
        [contact_person],
        [street],
        [zip_code],
        [city],
        [country],
        [phone],
        [email],
        [vat_id],
        [active],
        [leitweg_id],
        [electronic_address],
        [electronic_address_scheme],
        [business_process_id],
        [buyer_reference],
        [department]
    )
    SELECT
        1 AS company_id,

        CONCAT(
            'K11',
            RIGHT(
                '0000' + CAST(n AS varchar(4)),
                4
            )
        ) AS customer_number,

        CONCAT(
            CASE (n - 1) % 10
                WHEN 0 THEN 'Mustertechnik'
                WHEN 1 THEN 'Industrieservice'
                WHEN 2 THEN 'Elektrotechnik'
                WHEN 3 THEN 'Maschinenbau'
                WHEN 4 THEN 'Systemtechnik'
                WHEN 5 THEN 'Automatisierung'
                WHEN 6 THEN 'Präzisionstechnik'
                WHEN 7 THEN 'Logistikservice'
                WHEN 8 THEN 'Engineering'
                ELSE 'Industriebedarf'
            END,
            ' ',
            FORMAT(n, '000'),
            CASE n % 4
                WHEN 0 THEN ' GmbH'
                WHEN 1 THEN ' AG'
                WHEN 2 THEN ' GmbH & Co. KG'
                ELSE ' KG'
            END
        ) AS name,

        CASE n % 8
            WHEN 0 THEN 'Max Mustermann'
            WHEN 1 THEN 'Peter Müller'
            WHEN 2 THEN 'Sabine Schneider'
            WHEN 3 THEN 'Thomas Fischer'
            WHEN 4 THEN 'Andrea Weber'
            WHEN 5 THEN 'Michael Wagner'
            WHEN 6 THEN 'Kerstin Becker'
            ELSE 'Stefan Hoffmann'
        END AS contact_person,

        CONCAT(
            CASE n % 8
                WHEN 0 THEN 'Hauptstrasse'
                WHEN 1 THEN 'Industriestrasse'
                WHEN 2 THEN 'Bahnhofstrasse'
                WHEN 3 THEN 'Gewerbering'
                WHEN 4 THEN 'Siemensstrasse'
                WHEN 5 THEN 'Robert-Bosch-Strasse'
                WHEN 6 THEN 'Carl-Benz-Strasse'
                ELSE 'Am Gewerbepark'
            END,
            ' ',
            (n % 80) + 1
        ) AS street,

        CASE n % 10
            WHEN 0 THEN '80331'
            WHEN 1 THEN '90402'
            WHEN 2 THEN '86150'
            WHEN 3 THEN '85049'
            WHEN 4 THEN '93047'
            WHEN 5 THEN '97070'
            WHEN 6 THEN '94032'
            WHEN 7 THEN '96047'
            WHEN 8 THEN '84028'
            ELSE '87435'
        END AS zip_code,

        CASE n % 10
            WHEN 0 THEN 'München'
            WHEN 1 THEN 'Nürnberg'
            WHEN 2 THEN 'Augsburg'
            WHEN 3 THEN 'Ingolstadt'
            WHEN 4 THEN 'Regensburg'
            WHEN 5 THEN 'Würzburg'
            WHEN 6 THEN 'Passau'
            WHEN 7 THEN 'Bamberg'
            WHEN 8 THEN 'Landshut'
            ELSE 'Kempten'
        END AS city,

        'DE' AS country,

        CONCAT(
            '+49 ',
            800 + (n % 100),
            ' ',
            RIGHT(
                '000000' + CAST(100000 + n AS varchar(10)),
                6
            )
        ) AS phone,

        CONCAT(
            'kunde',
            FORMAT(n, '000'),
            '@testfirma1.de'
        ) AS email,

        CONCAT(
            'DE',
            RIGHT(
                '000000000'
                    + CAST(300000000 + n AS varchar(9)),
                9
            )
        ) AS vat_id,

        CASE
            WHEN n % 10 = 0 THEN 0
            ELSE 1
        END AS active,

        NULL AS leitweg_id,
        NULL AS electronic_address,
        NULL AS electronic_address_scheme,
        NULL AS business_process_id,
        NULL AS buyer_reference,

        CASE n % 5
            WHEN 0 THEN 'Einkauf'
            WHEN 1 THEN 'Buchhaltung'
            WHEN 2 THEN 'Verwaltung'
            WHEN 3 THEN 'Technik'
            ELSE 'Geschäftsleitung'
        END AS department

    FROM Numbers

    OPTION (MAXRECURSION 100);


    /* =========================================================
       20 Testkunden für company_id = 2
       Kundennummern: K210001 bis K210020
       ========================================================= */

    ;WITH Numbers AS
    (
        SELECT 1 AS n

        UNION ALL

        SELECT n + 1
        FROM Numbers
        WHERE n < 20
    )
    INSERT INTO [dbo].[customer]
    (
        [company_id],
        [customer_number],
        [name],
        [contact_person],
        [street],
        [zip_code],
        [city],
        [country],
        [phone],
        [email],
        [vat_id],
        [active],
        [leitweg_id],
        [electronic_address],
        [electronic_address_scheme],
        [business_process_id],
        [buyer_reference],
        [department]
    )
    SELECT
        2 AS company_id,

        CONCAT(
            'K21',
            RIGHT(
                '0000' + CAST(n AS varchar(4)),
                4
            )
        ) AS customer_number,

        CONCAT(
            CASE (n - 1) % 10
                WHEN 0 THEN 'Nordwerk'
                WHEN 1 THEN 'Südtechnik'
                WHEN 2 THEN 'Pro Industrie'
                WHEN 3 THEN 'TechSystems'
                WHEN 4 THEN 'Innovativ Engineering'
                WHEN 5 THEN 'Automation Center'
                WHEN 6 THEN 'Precision Systems'
                WHEN 7 THEN 'Logistik Partner'
                WHEN 8 THEN 'Industrie Technik'
                ELSE 'Systembau'
            END,
            ' ',
            FORMAT(n, '000'),
            CASE n % 4
                WHEN 0 THEN ' GmbH'
                WHEN 1 THEN ' AG'
                WHEN 2 THEN ' GmbH & Co. KG'
                ELSE ' KG'
            END
        ) AS name,

        CASE n % 8
            WHEN 0 THEN 'Martin Schmidt'
            WHEN 1 THEN 'Julia Wagner'
            WHEN 2 THEN 'Andreas Becker'
            WHEN 3 THEN 'Claudia Hoffmann'
            WHEN 4 THEN 'Markus Schäfer'
            WHEN 5 THEN 'Petra Koch'
            WHEN 6 THEN 'Daniel Bauer'
            ELSE 'Susanne Richter'
        END AS contact_person,

        CONCAT(
            CASE n % 8
                WHEN 0 THEN 'Hauptstrasse'
                WHEN 1 THEN 'Industriestrasse'
                WHEN 2 THEN 'Werkstrasse'
                WHEN 3 THEN 'Gewerbering'
                WHEN 4 THEN 'Technologiepark'
                WHEN 5 THEN 'Dieselstrasse'
                WHEN 6 THEN 'Daimlerstrasse'
                ELSE 'Am Industriepark'
            END,
            ' ',
            (n % 60) + 1
        ) AS street,

        CASE n % 10
            WHEN 0 THEN '04109'
            WHEN 1 THEN '01067'
            WHEN 2 THEN '99084'
            WHEN 3 THEN '39104'
            WHEN 4 THEN '06108'
            WHEN 5 THEN '09111'
            WHEN 6 THEN '08056'
            WHEN 7 THEN '07545'
            WHEN 8 THEN '07743'
            ELSE '08523'
        END AS zip_code,

        CASE n % 10
            WHEN 0 THEN 'Leipzig'
            WHEN 1 THEN 'Dresden'
            WHEN 2 THEN 'Erfurt'
            WHEN 3 THEN 'Magdeburg'
            WHEN 4 THEN 'Halle'
            WHEN 5 THEN 'Chemnitz'
            WHEN 6 THEN 'Zwickau'
            WHEN 7 THEN 'Gera'
            WHEN 8 THEN 'Jena'
            ELSE 'Plauen'
        END AS city,

        'DE' AS country,

        CONCAT(
            '+49 ',
            300 + (n % 100),
            ' ',
            RIGHT(
                '000000' + CAST(200000 + n AS varchar(10)),
                6
            )
        ) AS phone,

        CONCAT(
            'kunde',
            FORMAT(n, '000'),
            '@testfirma2.de'
        ) AS email,

        CONCAT(
            'DE',
            RIGHT(
                '000000000'
                    + CAST(400000000 + n AS varchar(9)),
                9
            )
        ) AS vat_id,

        CASE
            WHEN n % 10 = 0 THEN 0
            ELSE 1
        END AS active,

        NULL AS leitweg_id,
        NULL AS electronic_address,
        NULL AS electronic_address_scheme,
        NULL AS business_process_id,
        NULL AS buyer_reference,

        CASE n % 5
            WHEN 0 THEN 'Einkauf'
            WHEN 1 THEN 'Buchhaltung'
            WHEN 2 THEN 'Verwaltung'
            WHEN 3 THEN 'Technik'
            ELSE 'Geschäftsleitung'
        END AS department

    FROM Numbers

    OPTION (MAXRECURSION 20);


    /* =========================================================
       Transaktion abschließen
       ========================================================= */

    COMMIT TRANSACTION;

END TRY

BEGIN CATCH

    IF @@TRANCOUNT > 0
        ROLLBACK TRANSACTION;

    THROW;

END CATCH;
GO
```

Danach kontrollieren
```SQL
SELECT
    company_id,
    COUNT(*) AS anzahl
FROM [Firma].[dbo].[customer]
GROUP BY company_id
ORDER BY company_id;
```
##Ergänze Testdaten für Leitweg-ID und XRechnung

### SQL-Testdaten für XRechnung
```Sql
USE [Firma];
GO

SET NOCOUNT ON;
SET XACT_ABORT ON;

BEGIN TRANSACTION;

BEGIN TRY

    /* =========================================================
       TESTDATEN XRECHNUNG
       company_id = 1

       K110001 - K110020 erhalten XRechnungsdaten
       ========================================================= */

    UPDATE [dbo].[customer]
    SET
        [leitweg_id] =
            CONCAT(
                '09-TEST',
                RIGHT(
                    '0000' + CAST(id AS varchar(4)),
                    4
                ),
                '-99'
            ),

        [electronic_address] =
            CONCAT(
                '09-TEST',
                RIGHT(
                    '0000' + CAST(id AS varchar(4)),
                    4
                ),
                '-99'
            ),

        [electronic_address_scheme] =
            '0204',

        [business_process_id] =
            'urn:fdc:peppol.eu:2017:poacc:billing:01:1.0',

        [buyer_reference] =
            CONCAT(
                '09-TEST',
                RIGHT(
                    '0000' + CAST(id AS varchar(4)),
                    4
                ),
                '-99'
            ),

        [department] =
            CASE id % 5
                WHEN 0 THEN 'Zentrale Rechnungsstelle'
                WHEN 1 THEN 'Finanzverwaltung'
                WHEN 2 THEN 'Beschaffung'
                WHEN 3 THEN 'Gebäudemanagement'
                ELSE 'IT-Beschaffung'
                END

    WHERE
        [company_id] = 1
      AND [customer_number]
        BETWEEN 'K110001' AND 'K110020';


    /* =========================================================
       TESTDATEN XRECHNUNG
       company_id = 2

       K210001 - K210010 erhalten XRechnungsdaten
       ========================================================= */

    UPDATE [dbo].[customer]
    SET
        [leitweg_id] =
            CONCAT(
                '14-TEST',
                RIGHT(
                    '0000' + CAST(id AS varchar(4)),
                    4
                ),
                '-99'
            ),

        [electronic_address] =
            CONCAT(
                '14-TEST',
                RIGHT(
                    '0000' + CAST(id AS varchar(4)),
                    4
                ),
                '-99'
            ),

        [electronic_address_scheme] =
            '0204',

        [business_process_id] =
            'urn:fdc:peppol.eu:2017:poacc:billing:01:1.0',

        [buyer_reference] =
            CONCAT(
                '14-TEST',
                RIGHT(
                    '0000' + CAST(id AS varchar(4)),
                    4
                ),
                '-99'
            ),

        [department] =
            CASE id % 5
                WHEN 0 THEN 'Zentrale Rechnungsstelle'
                WHEN 1 THEN 'Finanzverwaltung'
                WHEN 2 THEN 'Beschaffung'
                WHEN 3 THEN 'Liegenschaften'
                ELSE 'IT'
                END

    WHERE
        [company_id] = 2
      AND [customer_number]
        BETWEEN 'K210001' AND 'K210010';


    COMMIT TRANSACTION;

END TRY

BEGIN CATCH

    IF @@TRANCOUNT > 0
        ROLLBACK TRANSACTION;

    THROW;

END CATCH;
GO
```

Ergebnis prüfen
```Sql
SELECT
    [id],
    [company_id],
    [customer_number],
    [name],
    [leitweg_id],
    [electronic_address],
    [electronic_address_scheme],
    [business_process_id],
    [buyer_reference],
    [department]
FROM [Firma].[dbo].[customer]
WHERE
       [leitweg_id] IS NOT NULL
    OR [electronic_address] IS NOT NULL
    OR [buyer_reference] IS NOT NULL
ORDER BY
    [company_id],
    [customer_number];
```
Damit hast du anschließend drei interessante Gruppen für deine Tests:

* normale Kunden ohne XRechnungsdaten
* vorhandener realitätsnaher Datensatz wie Landratsamt München
* synthetische B2G/XRechnung-Testkunden mit Leitweg-ID, 0204, Buyer Reference und Business Process
