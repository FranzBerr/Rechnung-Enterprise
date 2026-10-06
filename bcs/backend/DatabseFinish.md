# Datenbank mit creates und Testdaten

company
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

bank
```Sql
CREATE TABLE bank
(
    id BIGINT IDENTITY(1,1) PRIMARY KEY,

    bank_code NVARCHAR(20),

    bank_name NVARCHAR(200) NOT NULL,

    bic NVARCHAR(11) NOT NULL,

    country NVARCHAR(10),

    active BIT NOT NULL
        CONSTRAINT DF_BANK_ACTIVE DEFAULT(1)
);
```

bank_account
```Sql
CREATE TABLE bank_account
(
    id BIGINT IDENTITY(1,1) PRIMARY KEY,

    company_id BIGINT NOT NULL,

    bank_id BIGINT NOT NULL,

    account_holder NVARCHAR(200) NOT NULL,

    iban NVARCHAR(34) NOT NULL UNIQUE,

    default_account BIT NOT NULL,

    active BIT NOT NULL
        CONSTRAINT DF_BANK_ACCOUNT_ACTIVE DEFAULT(1),

    CONSTRAINT FK_BANK_ACCOUNT_COMPANY
        FOREIGN KEY (company_id)
        REFERENCES company(id),

    CONSTRAINT FK_BANK_ACCOUNT_BANK
        FOREIGN KEY (bank_id)
        REFERENCES bank(id)
);
```
customer ohne Behörde
```Sql
CREATE TABLE customer
(
    id BIGINT IDENTITY(1,1) PRIMARY KEY,

    company_id BIGINT NOT NULL,

    customer_number NVARCHAR(30) NOT NULL UNIQUE,

    name NVARCHAR(200) NOT NULL,

    contact_person NVARCHAR(200),

    street NVARCHAR(200),

    zip_code NVARCHAR(20),

    city NVARCHAR(100),

    country NVARCHAR(100),

    phone NVARCHAR(100),

    email NVARCHAR(200),

    vat_id NVARCHAR(50),

    active BIT NOT NULL
        CONSTRAINT DF_CUSTOMER_ACTIVE DEFAULT(1),

    CONSTRAINT FK_CUSTOMER_COMPANY
        FOREIGN KEY (company_id)
        REFERENCES company(id)
);
```
customer mit Behörde
```Sql
CREATE TABLE customer
(
    id BIGINT IDENTITY(1,1) PRIMARY KEY,

    company_id BIGINT NOT NULL,

    customer_number VARCHAR(30) NOT NULL,
    name VARCHAR(200) NOT NULL,
    contact_person VARCHAR(200),

    street VARCHAR(200),
    zip_code VARCHAR(20),
    city VARCHAR(100),
    country VARCHAR(100),

    phone VARCHAR(100),
    email VARCHAR(200),

    vat_id VARCHAR(50),

    leitweg_id VARCHAR(100),

    electronic_address VARCHAR(255),
    electronic_address_scheme VARCHAR(20),

    active BIT NOT NULL DEFAULT 1,

    CONSTRAINT fk_customer_company
        FOREIGN KEY (company_id)
        REFERENCES company(id)
);
```
product
```Sql
CREATE TABLE product
(
    id BIGINT IDENTITY(1,1) PRIMARY KEY,

    company_id BIGINT NOT NULL,

    product_number NVARCHAR(30) NOT NULL UNIQUE,

    name NVARCHAR(200) NOT NULL,

    description NVARCHAR(1000),

    unit NVARCHAR(30) NOT NULL,

    net_price DECIMAL(19,4) NOT NULL,

    active BIT NOT NULL
        CONSTRAINT DF_PRODUCT_ACTIVE DEFAULT(1),

    CONSTRAINT FK_PRODUCT_COMPANY
        FOREIGN KEY (company_id)
        REFERENCES company(id)
);
```
invoice ohne Behörde
```Sql
CREATE TABLE invoice
(
    id BIGINT IDENTITY(1,1) PRIMARY KEY,

    invoice_number NVARCHAR(30) UNIQUE,

    invoice_date DATE NOT NULL,

    service_date DATE,

    due_date DATE NOT NULL,

    status NVARCHAR(30) NOT NULL,

    company_id BIGINT NOT NULL,

    customer_id BIGINT NOT NULL,

    bank_account_id BIGINT NOT NULL,

    customer_number NVARCHAR(30) NOT NULL,

    customer_name NVARCHAR(200) NOT NULL,

    customer_street NVARCHAR(200),

    customer_zip_code NVARCHAR(20),

    customer_city NVARCHAR(100),

    customer_country NVARCHAR(100),

    customer_vat_id NVARCHAR(50),

    bank_name NVARCHAR(200) NOT NULL,

    iban NVARCHAR(34) NOT NULL,

    bic NVARCHAR(11) NOT NULL,

    payment_term_days INT NOT NULL,

    discount_percent DECIMAL(7,4) NOT NULL,

    discount_days INT NOT NULL,

    subtotal_net DECIMAL(19,2) NOT NULL,

    tax_amount DECIMAL(19,2) NOT NULL,

    total_gross DECIMAL(19,2) NOT NULL,

    notes NVARCHAR(2000),

    CONSTRAINT FK_INVOICE_COMPANY
        FOREIGN KEY (company_id)
        REFERENCES company(id),

    CONSTRAINT FK_INVOICE_CUSTOMER
        FOREIGN KEY (customer_id)
        REFERENCES customer(id),

    CONSTRAINT FK_INVOICE_BANK_ACCOUNT
        FOREIGN KEY (bank_account_id)
        REFERENCES bank_account(id)
);
```

Invoice mit Behörde:
```Sql
CREATE TABLE invoice
(
    id BIGINT IDENTITY(1,1) PRIMARY KEY,

    invoice_number VARCHAR(30) NOT NULL UNIQUE,

    invoice_date DATE NOT NULL,
    service_date DATE,
    due_date DATE NOT NULL,

    status VARCHAR(30) NOT NULL,

    company_id BIGINT NOT NULL,
    customer_id BIGINT NOT NULL,
    bank_account_id BIGINT NOT NULL,

    customer_number VARCHAR(30) NOT NULL,
    customer_name VARCHAR(200) NOT NULL,
    customer_street VARCHAR(200),
    customer_zip_code VARCHAR(20),
    customer_city VARCHAR(100),
    customer_country VARCHAR(100),
    customer_vat_id VARCHAR(50),

    customer_leitweg_id VARCHAR(100),
    customer_electronic_address VARCHAR(255),
    customer_electronic_address_scheme VARCHAR(20),

    bank_name VARCHAR(200) NOT NULL,
    iban VARCHAR(34) NOT NULL,
    bic VARCHAR(11) NOT NULL,

    payment_term_days INT NOT NULL,

    discount_percent DECIMAL(7,4) NOT NULL DEFAULT 0,
    discount_days INT NOT NULL DEFAULT 0,

    subtotal_net DECIMAL(19,2) NOT NULL DEFAULT 0,
    tax_amount DECIMAL(19,2) NOT NULL DEFAULT 0,
    total_gross DECIMAL(19,2) NOT NULL DEFAULT 0,

    buyer_reference VARCHAR(100),
    order_reference VARCHAR(100),
    contract_reference VARCHAR(100),
    project_reference VARCHAR(100),
    delivery_note_reference VARCHAR(100),

    invoice_type_code VARCHAR(10) NOT NULL DEFAULT '380',
    payment_means_code VARCHAR(10) NOT NULL DEFAULT '58',
    tax_category_code VARCHAR(10) NOT NULL DEFAULT 'S',

    currency_code VARCHAR(3) NOT NULL DEFAULT 'EUR',

    tax_point_date DATE,

    payment_reference VARCHAR(100),
    seller_reference VARCHAR(100),

    business_process_id VARCHAR(200),

    specification_identifier VARCHAR(300),

    notes VARCHAR(2000),

    CONSTRAINT fk_invoice_company
        FOREIGN KEY (company_id)
        REFERENCES company(id),

    CONSTRAINT fk_invoice_customer
        FOREIGN KEY (customer_id)
        REFERENCES customer(id),

    CONSTRAINT fk_invoice_bank_account
        FOREIGN KEY (bank_account_id)
        REFERENCES bank_account(id)
);
```
invoice_item ohne Behörde
```Sql
CREATE TABLE invoice_item
(
    id BIGINT IDENTITY(1,1) PRIMARY KEY,

    invoice_id BIGINT NOT NULL,

    product_id BIGINT NOT NULL,

    position_number INT NOT NULL,

    product_number NVARCHAR(30) NOT NULL,

    description NVARCHAR(1000) NOT NULL,

    quantity DECIMAL(19,4) NOT NULL,

    unit NVARCHAR(30) NOT NULL,

    unit_price_net DECIMAL(19,4) NOT NULL,

    discount_percent DECIMAL(7,4) NOT NULL,

    vat_rate DECIMAL(7,4) NOT NULL,

    line_net DECIMAL(19,2) NOT NULL,

    tax_amount DECIMAL(19,2) NOT NULL,

    line_gross DECIMAL(19,2) NOT NULL,

    CONSTRAINT FK_INVOICE_ITEM_INVOICE
        FOREIGN KEY (invoice_id)
        REFERENCES invoice(id),

    CONSTRAINT FK_INVOICE_ITEM_PRODUCT
        FOREIGN KEY (product_id)
        REFERENCES product(id)
);
```
invoice_item mit Behörde
```Sql
CREATE TABLE invoice_item
(
    id BIGINT IDENTITY(1,1) PRIMARY KEY,

    invoice_id BIGINT NOT NULL,

    position_number INT NOT NULL,

    product_id BIGINT,

    product_number VARCHAR(100),

    description VARCHAR(1000) NOT NULL,

    quantity DECIMAL(19,4) NOT NULL,

    unit VARCHAR(20) NOT NULL,

    unit_price_net DECIMAL(19,4) NOT NULL,

    discount_percent DECIMAL(7,4) NOT NULL DEFAULT 0,

    vat_rate DECIMAL(7,4) NOT NULL,

    line_net DECIMAL(19,2) NOT NULL DEFAULT 0,
    tax_amount DECIMAL(19,2) NOT NULL DEFAULT 0,
    line_gross DECIMAL(19,2) NOT NULL DEFAULT 0,

    tax_category_code VARCHAR(10) NOT NULL DEFAULT 'S',

    item_classification_code VARCHAR(100),

    CONSTRAINT fk_invoice_item_invoice
        FOREIGN KEY (invoice_id)
        REFERENCES invoice(id)
        ON DELETE CASCADE
);
```
# Testdaten ohne Behörde

Firma
```Sql
INSERT INTO company
(
    name,
    legal_form,
    tax_number,
    vat_id,
    street,
    zip_code,
    city,
    country,
    phone,
    email,
    website,
    active
)
VALUES
(
    'BCS GmbH',
    'GmbH',
    '123/456/789',
    'DE123456789',
    'Musterstrasse 1',
    '97616',
    'Bad Neustadt',
    'DE',
    '+49 9771 12345',
    'info@bcs.de',
    'https://www.bcs.de',
    1
);
```
Banken
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
('70020270','HypoVereinsbank','HYVEDEMMXXX','DE',1),
('70050000','Sparkasse München','BYLADEM1001','DE',1),
('70150000','Stadtsparkasse München','SSKMDEMMXXX','DE',1);
```

Bankkonto
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
);
```

Kunden
```Sql
INSERT INTO customer
(
    company_id,
    customer_number,
    name,
    contact_person,
    street,
    zip_code,
    city,
    country,
    phone,
    email,
    vat_id,
    active
)
VALUES
(1,'K100000','Muster GmbH','Max Mustermann','Hauptstrasse 10','80331','München','DE','089123456','info@muster.de','DE111111111',1),

(1,'K100001','Alpha AG','Peter Müller','Industriestrasse 5','90402','Nürnberg','DE','0911123456','kontakt@alpha.de','DE222222222',1);
```
Produkte
```Sql
INSERT INTO product
(
    company_id,
    product_number,
    name,
    description,
    unit,
    net_price,
    active
)
VALUES
(1,'P1000','Beratung','IT Beratung','STD',120.00,1),
(1,'P1001','Softwareentwicklung','Softwareentwicklung','STD',140.00,1),
(1,'P1002','Support','Telefonischer Support','STD',95.00,1),
(1,'P1003','Projektleitung','Projektmanagement','STD',150.00,1);
```

Beispielrechnung
```Sql
INSERT INTO invoice
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
    notes
)
VALUES
(
    'RE-2026-000001',
    '2026-09-29',
    '2026-09-29',
    '2026-10-13',
    'CREATED',
    1,
    1,
    1,
    'K100000',
    'Muster GmbH',
    'Hauptstrasse 10',
    '80331',
    'München',
    'DE',
    'DE111111111',
    'HypoVereinsbank',
    'DE02120300000000202051',
    'HYVEDEMMXXX',
    14,
    0,
    0,
    520.00,
    98.80,
    618.80,
    'Vielen Dank für Ihren Auftrag.'
);
```
Rechnungspositionen
```Sql
INSERT INTO invoice_item
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
    line_gross
)
VALUES
(1,1,1,'P1000','IT Beratung',2, 'STD',120.00,0,19,240.00,45.60,285.60),

(1,2,2,'P1001','Softwareentwicklung',2,'STD',140.00,0,19,280.00,53.20,333.20);
```
Mit diesen Daten kannst du sofort:
* Company CRUD testen
* Customer CRUD testen
* Product CRUD testen
* BankAccount CRUD testen
* Invoice inkl. Positionen testen
* Swagger-Endpunkte testen
* React DataGrid-Seiten befüllen.

# Testdaten mit Behörde
Testkunde Behörde
```Sql
INSERT INTO customer
(
    company_id,
    customer_number,
    name,
    contact_person,
    street,
    zip_code,
    city,
    country,
    phone,
    email,
    vat_id,
    leitweg_id,
    electronic_address,
    electronic_address_scheme,
    active
)
VALUES
    (
        1,
        'K1000',
        'Landratsamt München',
        'Max Mustermann',
        'Mariahilfplatz 17',
        '81541',
        'München',
        'Deutschland',
        '089123456',
        'rechnung@lra-m.bayern.de',
        NULL,
        '09184-12345-67',
        '09184-12345-67',
        '0204',
        1
    );
```
Testrechnung
```Sql
INSERT INTO invoice
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

    customer_leitweg_id,

    bank_name,
    iban,
    bic,

    payment_term_days,

    discount_percent,
    discount_days,

    subtotal_net,
    tax_amount,
    total_gross,

    buyer_reference,
    order_reference,

    invoice_type_code,
    payment_means_code,
    tax_category_code,

    currency_code,

    payment_reference,

    business_process_id,
    specification_identifier
)
VALUES
    (
        'RE-2026-000002',
        '2026-09-29',
        '2026-09-29',
        '2026-10-29',
        'OPEN',

        1,
        1,
        1,

        'K1000',
        'Landratsamt München',
        'Mariahilfplatz 17',
        '81541',
        'München',
        'Deutschland',

        '09184-12345-67',

        'Sparkasse München',
        'DE12500105170648489890',
        'BYLADEM1001',

        30,

        0.0000,
        0,

        1500.00,
        285.00,
        1785.00,

        'BEST-2026-4711',
        'PO-2026-0001',

        '380',
        '58',
        'S',

        'EUR',

        'RE-2026-000001',

        'urn:fdc:peppol.eu:2017:poacc:billing:01:1.0',
        'urn:xeinkauf.de:kosit:xrechnung_3.0'
    );
```
Erstelle vorher in dbo.product:
```Sql
INSERT INTO dbo.product
(
    company_id,
    product_number,
    name,
    description,
    unit,
    net_price,
    active
)
VALUES
(
    1,
    'SRV-001',
    'Architektur- und Beratungsleistungen',
    'Beratung, Architektur und Softwaredesign',
    'HUR',
    100.0000,
    1
),
(
    1,
    'SRV-002',
    'Implementierung XRechnung',
    'Implementierung und Integration von XRechnung',
    'HUR',
    100.0000,
    1
);
GO
```

Testpositionen
```Sql
INSERT INTO invoice_item
(
    invoice_id,
    position_number,
    product_id,
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
    tax_category_code
)
VALUES
(
    4,
    1,
    1001,
    'SRV-001',
    'Architektur- und Beratungsleistungen',
    10.0000,
    'HUR',
    100.0000,
    0,
    19.0000,
    1000.00,
    190.00,
    1190.00,
    'S'
),
(
    4,
    2,
    1002,
    'SRV-002',
    'Implementierung XRechnung',
    5.0000,
    'HUR',
    100.0000,
    0,
    19.0000,
    500.00,
    95.00,
    595.00,
    'S'
);
```

Falls Du die Tabellen Customer ohne Behörde angelegt hast: hier die Scripts für die Erweiterung
```Sql
USE [Firma]
GO

/* Leitweg-ID */

IF COL_LENGTH('dbo.customer', 'leitweg_id') IS NULL
BEGIN
    ALTER TABLE dbo.customer
    ADD leitweg_id NVARCHAR(100) NULL;
END
GO

/* Elektronische Adresse */

IF COL_LENGTH('dbo.customer', 'electronic_address') IS NULL
BEGIN
    ALTER TABLE dbo.customer
    ADD electronic_address NVARCHAR(255) NULL;
END
GO

/* Schema der elektronischen Adresse */

IF COL_LENGTH('dbo.customer', 'electronic_address_scheme') IS NULL
BEGIN
    ALTER TABLE dbo.customer
    ADD electronic_address_scheme NVARCHAR(20) NULL;
END
GO
```
Falls dieTabelle Invoice ohne Behörde bereits existiert, hier ist das Script für die Erweiterung für Behörden

```Sql
USE [Firma]
GO

/* Buyer Reference */

IF COL_LENGTH('dbo.invoice', 'buyer_reference') IS NULL
BEGIN
    ALTER TABLE dbo.invoice
    ADD buyer_reference NVARCHAR(100) NULL;
END
GO

/* Order Reference */

IF COL_LENGTH('dbo.invoice', 'order_reference') IS NULL
BEGIN
    ALTER TABLE dbo.invoice
    ADD order_reference NVARCHAR(100) NULL;
END
GO

/* Contract Reference */

IF COL_LENGTH('dbo.invoice', 'contract_reference') IS NULL
BEGIN
    ALTER TABLE dbo.invoice
    ADD contract_reference NVARCHAR(100) NULL;
END
GO

/* Project Reference */

IF COL_LENGTH('dbo.invoice', 'project_reference') IS NULL
BEGIN
    ALTER TABLE dbo.invoice
    ADD project_reference NVARCHAR(100) NULL;
END
GO

/* Delivery Note Reference */

IF COL_LENGTH('dbo.invoice', 'delivery_note_reference') IS NULL
BEGIN
    ALTER TABLE dbo.invoice
    ADD delivery_note_reference NVARCHAR(100) NULL;
END
GO

/* Customer Leitweg-ID Snapshot */

IF COL_LENGTH('dbo.invoice', 'customer_leitweg_id') IS NULL
BEGIN
    ALTER TABLE dbo.invoice
    ADD customer_leitweg_id NVARCHAR(100) NULL;
END
GO

/* Customer Electronic Address Snapshot */

IF COL_LENGTH('dbo.invoice', 'customer_electronic_address') IS NULL
BEGIN
    ALTER TABLE dbo.invoice
    ADD customer_electronic_address NVARCHAR(255) NULL;
END
GO

/* Customer Electronic Address Scheme Snapshot */

IF COL_LENGTH('dbo.invoice', 'customer_electronic_address_scheme') IS NULL
BEGIN
    ALTER TABLE dbo.invoice
    ADD customer_electronic_address_scheme NVARCHAR(20) NULL;
END
GO

/* Invoice Type Code */

IF COL_LENGTH('dbo.invoice', 'invoice_type_code') IS NULL
BEGIN
    ALTER TABLE dbo.invoice
    ADD invoice_type_code NVARCHAR(10) NOT NULL
        CONSTRAINT DF_INVOICE_TYPE_CODE DEFAULT('380');
END
GO

/* Payment Means Code */

IF COL_LENGTH('dbo.invoice', 'payment_means_code') IS NULL
BEGIN
    ALTER TABLE dbo.invoice
    ADD payment_means_code NVARCHAR(10) NOT NULL
        CONSTRAINT DF_INVOICE_PAYMENT_MEANS DEFAULT('58');
END
GO

/* Tax Category Code */

IF COL_LENGTH('dbo.invoice', 'tax_category_code') IS NULL
BEGIN
    ALTER TABLE dbo.invoice
    ADD tax_category_code NVARCHAR(10) NOT NULL
        CONSTRAINT DF_INVOICE_TAX_CATEGORY DEFAULT('S');
END
GO

/* Currency Code */

IF COL_LENGTH('dbo.invoice', 'currency_code') IS NULL
BEGIN
    ALTER TABLE dbo.invoice
    ADD currency_code NVARCHAR(3) NOT NULL
        CONSTRAINT DF_INVOICE_CURRENCY DEFAULT('EUR');
END
GO

/* Tax Point Date */

IF COL_LENGTH('dbo.invoice', 'tax_point_date') IS NULL
BEGIN
    ALTER TABLE dbo.invoice
    ADD tax_point_date DATE NULL;
END
GO

/* Payment Reference */

IF COL_LENGTH('dbo.invoice', 'payment_reference') IS NULL
BEGIN
    ALTER TABLE dbo.invoice
    ADD payment_reference NVARCHAR(100) NULL;
END
GO

/* Seller Reference */

IF COL_LENGTH('dbo.invoice', 'seller_reference') IS NULL
BEGIN
    ALTER TABLE dbo.invoice
    ADD seller_reference NVARCHAR(100) NULL;
END
GO

/* Business Process ID */

IF COL_LENGTH('dbo.invoice', 'business_process_id') IS NULL
BEGIN
    ALTER TABLE dbo.invoice
    ADD business_process_id NVARCHAR(200) NULL;
END
GO

/* Specification Identifier */

IF COL_LENGTH('dbo.invoice', 'specification_identifier') IS NULL
BEGIN
    ALTER TABLE dbo.invoice
    ADD specification_identifier NVARCHAR(300) NULL;
END
GO
```
Falls dieTabelle Invoice_Item ohne Behörde bereits existiert, hier ist das Script für die Erweiterung für Behörden
```Sql
USE [Firma]
GO

/* Tax Category Code */

IF COL_LENGTH('dbo.invoice_item', 'tax_category_code') IS NULL
BEGIN
    ALTER TABLE dbo.invoice_item
    ADD tax_category_code NVARCHAR(10) NOT NULL
        CONSTRAINT DF_INVOICE_ITEM_TAX_CATEGORY
        DEFAULT('S');
END
GO

/* Item Classification Code */

IF COL_LENGTH('dbo.invoice_item', 'item_classification_code') IS NULL
BEGIN
    ALTER TABLE dbo.invoice_item
    ADD item_classification_code NVARCHAR(100) NULL;
END
GO
```

Testdaten für InvoiceItem
```Sql
INSERT INTO dbo.invoice_item
(
    invoice_id,
    position_number,
    product_id,
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
    item_classification_code
)
VALUES
(
    4,
    1,
    5,
    'SRV-001',
    'Architektur- und Beratungsleistungen',
    10.0000,
    'HUR',
    100.0000,
    0.0000,
    19.0000,
    1000.00,
    190.00,
    1190.00,
    'S',
    '81101500'
),
(
    4,
    2,
    6,
    'SRV-002',
    'Implementierung XRechnung',
    5.0000,
    'HUR',
    100.0000,
    0.0000,
    19.0000,
    500.00,
    95.00,
    595.00,
    'S',
    '81111500'
);
GO
```
