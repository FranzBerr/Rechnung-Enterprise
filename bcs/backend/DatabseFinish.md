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
customer
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
invoice
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
invoice_item
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
# Testdaten

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
```Sql

```

```Sql

```

```Sql

```

```Sql

```

```Sql

```
