# Angebote
## 1. Vorgeschlagene Komponenten

```Code
com.bcs.rechnung.offer
├── OfferEntity.java
├── OfferItemEntity.java
├── OfferStatus.java
├── OfferRepository.java
├── OfferItemRepository.java
├── OfferMapper.java
│
├── dto
│   ├── OfferDto.java
│   ├── OfferItemDto.java
│   ├── OfferRequest.java
│   └── OfferItemRequest.java
│
├── service
│   ├── OfferService.java
│   └── OfferServiceImpl.java
│
├── pdf
│   ├── OfferPdfGenerator.java
│   ├── OfferPdfGeneratorImpl.java
│   ├── OfferPdfService.java
│   ├── OfferPdfServiceImpl.java
│   └── GeneratedOfferPdf.java
│
├── archive
│   ├── OfferArchiveService.java
│   └── OfferArchiveServiceImpl.java
│
└── controller
└── OfferController.java
```
## 2. Angebotsstatus

```Code
package com.bcs.rechnung.offer;

public enum OfferStatus {

    DRAFT,

    CREATED,

    SENT,

    ACCEPTED,

    REJECTED,

    EXPIRED,

    CONVERTED_TO_INVOICE
}
```

## 3. SQL-Tabelle offer
```Code
USE [Firma];
GO

CREATE TABLE [dbo].[offer]
(
    [id] BIGINT IDENTITY(1,1) NOT NULL,

    [offer_number] NVARCHAR(30) NOT NULL,

    [offer_date] DATE NOT NULL,

    [valid_until] DATE NULL,

    [status] NVARCHAR(30) NOT NULL
        CONSTRAINT [DF_OFFER_STATUS]
        DEFAULT (N'DRAFT'),

    [company_id] BIGINT NOT NULL,

    [customer_id] BIGINT NOT NULL,

    [customer_number] NVARCHAR(30) NOT NULL,

    [customer_name] NVARCHAR(200) NOT NULL,

    [customer_contact_person] NVARCHAR(200) NULL,

    [customer_department] NVARCHAR(200) NULL,

    [customer_street] NVARCHAR(200) NULL,

    [customer_zip_code] NVARCHAR(20) NULL,

    [customer_city] NVARCHAR(100) NULL,

    [customer_country] NVARCHAR(100) NULL,

    [customer_vat_id] NVARCHAR(50) NULL,

    [currency_code] NVARCHAR(3) NOT NULL
        CONSTRAINT [DF_OFFER_CURRENCY_CODE]
        DEFAULT (N'EUR'),

    [payment_term_days] INT NOT NULL
        CONSTRAINT [DF_OFFER_PAYMENT_TERM_DAYS]
        DEFAULT ((14)),

    [subtotal_net] DECIMAL(19,2) NOT NULL
        CONSTRAINT [DF_OFFER_SUBTOTAL_NET]
        DEFAULT ((0)),

    [tax_amount] DECIMAL(19,2) NOT NULL
        CONSTRAINT [DF_OFFER_TAX_AMOUNT]
        DEFAULT ((0)),

    [total_gross] DECIMAL(19,2) NOT NULL
        CONSTRAINT [DF_OFFER_TOTAL_GROSS]
        DEFAULT ((0)),

    [introduction_text] NVARCHAR(2000) NULL,

    [closing_text] NVARCHAR(2000) NULL,

    [notes] NVARCHAR(2000) NULL,

    [active] BIT NOT NULL
        CONSTRAINT [DF_OFFER_ACTIVE]
        DEFAULT ((1)),

    [archived] BIT NOT NULL
        CONSTRAINT [DF_OFFER_ARCHIVED]
        DEFAULT ((0)),

    [archived_at] DATETIME2 NULL,

    [archive_path] NVARCHAR(1000) NULL,

    CONSTRAINT [PK_OFFER]
        PRIMARY KEY ([id]),

    CONSTRAINT [UK_OFFER_NUMBER]
        UNIQUE ([offer_number]),

    CONSTRAINT [FK_OFFER_COMPANY]
        FOREIGN KEY ([company_id])
        REFERENCES [dbo].[company] ([id]),

    CONSTRAINT [FK_OFFER_CUSTOMER]
        FOREIGN KEY ([customer_id])
        REFERENCES [dbo].[customer] ([id])
);
GO
```
## 4. SQL-Tabelle offer_item

```Code
CREATE TABLE [dbo].[offer_item]
(
    [id] BIGINT IDENTITY(1,1) NOT NULL,

    [offer_id] BIGINT NOT NULL,

    [position_number] INT NOT NULL,

    [product_id] BIGINT NULL,

    [product_number] NVARCHAR(30) NULL,

    [product_name] NVARCHAR(200) NOT NULL,

    [description] NVARCHAR(1000) NULL,

    [quantity] DECIMAL(19,4) NOT NULL,

    [unit] NVARCHAR(30) NOT NULL,

    [unit_price_net] DECIMAL(19,4) NOT NULL,

    [discount_percent] DECIMAL(7,4) NOT NULL
        CONSTRAINT [DF_OFFER_ITEM_DISCOUNT_PERCENT]
        DEFAULT ((0)),

    [vat_rate] DECIMAL(7,4) NOT NULL,

    [line_net] DECIMAL(19,2) NOT NULL,

    [tax_amount] DECIMAL(19,2) NOT NULL,

    [line_gross] DECIMAL(19,2) NOT NULL,

    [notes] NVARCHAR(1000) NULL,

    CONSTRAINT [PK_OFFER_ITEM]
        PRIMARY KEY ([id]),

    CONSTRAINT [FK_OFFER_ITEM_OFFER]
        FOREIGN KEY ([offer_id])
        REFERENCES [dbo].[offer] ([id])
        ON DELETE CASCADE,

    CONSTRAINT [FK_OFFER_ITEM_PRODUCT]
        FOREIGN KEY ([product_id])
        REFERENCES [dbo].[product] ([id]),

    CONSTRAINT [UK_OFFER_ITEM_POSITION]
        UNIQUE
        (
            [offer_id],
            [position_number]
        )
);
GO
```
### 5. Indizes
```Code
CREATE INDEX [IX_OFFER_OFFER_DATE]
ON [dbo].[offer] ([offer_date]);
GO

CREATE INDEX [IX_OFFER_CUSTOMER_ID]
ON [dbo].[offer] ([customer_id]);
GO

CREATE INDEX [IX_OFFER_COMPANY_ID]
ON [dbo].[offer] ([company_id]);
GO

CREATE INDEX [IX_OFFER_STATUS]
ON [dbo].[offer] ([status]);
GO

CREATE INDEX [IX_OFFER_ARCHIVED_OFFER_DATE]
ON [dbo].[offer]
(
    [archived],
    [offer_date]
)
INCLUDE
(
    [offer_number],
    [archived_at],
    [archive_path]
);
GO

CREATE INDEX [IX_OFFER_ITEM_OFFER_POSITION]
ON [dbo].[offer_item]
(
    [offer_id],
    [position_number]
);
GO

CREATE INDEX [IX_OFFER_ITEM_PRODUCT_ID]
ON [dbo].[offer_item] ([product_id]);
GO
```

### 6. Archivierung

Für ein Angebot mit:
```Code
offerNumber = AN-2026-000001
offerDate   = 21.10.2026
```
wird folgender Ordner erzeugt:

```Code
C:\PolyRackDummy\Rechning gesamt\
Rechnung-Enterprise-formatiert\
Rechnung-Enterprise\
archive\
2026\
Angebote\
AN-2026-000001\
```

Darin:
```Code
AN-2026-000001.pdf
```
Die Angebots-Entity speichert:

```Code
archived     = 1
archived_at  = Archivierungszeitpunkt
archive_path = ...\archive\2026\Angebote\AN-2026-000001
```

## 7. REST-Endpunkte
```Code
GET    /api/offers
GET    /api/offers/{id}

POST   /api/offers
PUT    /api/offers/{id}
DELETE /api/offers/{id}

GET    /api/offers/{id}/pdf

POST   /api/offers/{id}/archive
GET    /api/offers/{id}/archive
GET    /api/offers/{id}/archive/status

DELETE /api/offers/{id}/archive
```
8. Fachliche Sperre

Nach der Archivierung bleiben erlaubt:

```Code
GET Angebot
GET archiviertes PDF
GET Archivstatus
```
Gesperrt werden:

```Code
PUT Angebot
DELETE Angebot
Änderung der Positionen
Überschreiben der Archivdatei
```
## 9. Unterschied zur Rechnung

Ein Angebot braucht zunächst keine XRechnung und keine KoSIT-Validierung:

```Code
Angebot
    PDF
    Archivierung

Rechnung
    PDF
    XRechnung-XML
    KoSIT-Validierung
    Prüfbericht
    Validator-Protokoll
    Archivierung
```
Die PDF des Angebots enthält typischerweise:

```Code
ANGEBOT

Angebotsnummer
Angebotsdatum
Gültig bis
Kundennummer

Position
Beschreibung
Menge
Einheit
Einzelpreis
Rabatt
Gesamtpreis

Nettosumme
Umsatzsteuer
Gesamtbetrag

Einleitungstext
Zahlungsbedingungen
Schlusstext
```
Datenmodell

Damit ergibt sich:
```Code
CompanyEntity
     |
     | 1
     |
     n
OfferEntity
     |
     | 1
     |
     n
OfferItemEntity
     |
     | 0..1
     |
ProductEntity
```
# Testdaten

Ja. Für deinen aktuellen Stand würde ich zunächst einen vollständigen Testdatensatz für ein Angebot mit zwei Positionen anlegen. Das Skript ist auf unsere Tabellen offer und offer_item abgestimmt und verwendet zunächst company_id = 1, customer_id = 1 sowie vorhandene Produkte.

Ich würde das Testangebot bewusst noch nicht archiviert anlegen, damit wir danach CRUD, PDF-Erzeugung und Archivierung vollständig testen können

## 1. Testangebot AN-2026-000001
```Code
USE [Firma];
GO

SET XACT_ABORT ON;
GO

BEGIN TRANSACTION;

BEGIN TRY

    DECLARE @OfferId BIGINT;

    /* ---------------------------------------------------------
       Vorhandenen Testdatensatz entfernen
       --------------------------------------------------------- */

    SELECT
        @OfferId = [id]
    FROM [dbo].[offer]
    WHERE [offer_number] = N'AN-2026-000001';

    IF @OfferId IS NOT NULL
    BEGIN
        DELETE FROM [dbo].[offer_item]
        WHERE [offer_id] = @OfferId;

        DELETE FROM [dbo].[offer]
        WHERE [id] = @OfferId;
    END;


    /* ---------------------------------------------------------
       Angebot
       --------------------------------------------------------- */

    INSERT INTO [dbo].[offer]
    (
        [offer_number],
        [offer_date],
        [valid_until],
        [status],

        [company_id],
        [customer_id],

        [customer_number],
        [customer_name],
        [customer_contact_person],
        [customer_department],
        [customer_street],
        [customer_zip_code],
        [customer_city],
        [customer_country],
        [customer_vat_id],

        [currency_code],
        [payment_term_days],

        [subtotal_net],
        [tax_amount],
        [total_gross],

        [introduction_text],
        [closing_text],
        [notes],

        [active],

        [archived],
        [archived_at],
        [archive_path]
    )
    VALUES
    (
        N'AN-2026-000001',
        '2026-10-21',
        '2026-11-20',
        N'CREATED',

        1,
        1,

        N'K100000',
        N'Muster GmbH',
        N'Max Mustermann',
        N'Einkauf',
        N'Hauptstrasse 10',
        N'80331',
        N'München',
        N'DE',
        N'DE111111111',

        N'EUR',
        14,

        0.00,
        0.00,
        0.00,

        N'Vielen Dank für Ihre Anfrage. Gerne unterbreiten wir Ihnen folgendes Angebot.',
        N'Wir freuen uns auf Ihren Auftrag.',
        N'Testangebot für die Entwicklung der Angebotsverwaltung.',

        1,

        0,
        NULL,
        NULL
    );

    SET @OfferId =
        SCOPE_IDENTITY();


    /* ---------------------------------------------------------
       Position 1
       Projektleitung

       5 x 150,00 = 750,00 netto
       19 % USt = 142,50
       892,50 brutto
       --------------------------------------------------------- */

    INSERT INTO [dbo].[offer_item]
    (
        [offer_id],
        [position_number],

        [product_id],
        [product_number],
        [product_name],
        [description],

        [quantity],
        [unit],

        [unit_price_net],
        [discount_percent],
        [vat_rate],

        [line_net],
        [tax_amount],
        [line_gross],

        [notes]
    )
    VALUES
    (
        @OfferId,
        1,

        NULL,
        N'P1003',
        N'Projektleitung',
        N'Projektmanagement und Koordination',

        5.0000,
        N'STD',

        150.0000,
        0.0000,
        19.0000,

        750.00,
        142.50,
        892.50,

        NULL
    );


    /* ---------------------------------------------------------
       Position 2
       Architektur und Beratung

       8 x 100,00 = 800,00 netto
       19 % USt = 152,00
       952,00 brutto
       --------------------------------------------------------- */

    INSERT INTO [dbo].[offer_item]
    (
        [offer_id],
        [position_number],

        [product_id],
        [product_number],
        [product_name],
        [description],

        [quantity],
        [unit],

        [unit_price_net],
        [discount_percent],
        [vat_rate],

        [line_net],
        [tax_amount],
        [line_gross],

        [notes]
    )
    VALUES
    (
        @OfferId,
        2,

        NULL,
        N'SRV-001',
        N'Architektur- und Beratungsleistungen',
        N'Analyse, Architektur und technisches Softwaredesign',

        8.0000,
        N'STD',

        100.0000,
        0.0000,
        19.0000,

        800.00,
        152.00,
        952.00,

        N'Abrechnung gemäß Angebot.'
    );


    /* ---------------------------------------------------------
       Angebotssummen aktualisieren

       Netto:  750,00 + 800,00 = 1.550,00
       USt:    142,50 + 152,00 =   294,50
       Brutto:                    1.844,50
       --------------------------------------------------------- */

    UPDATE [dbo].[offer]
    SET
        [subtotal_net] = 1550.00,
        [tax_amount] = 294.50,
        [total_gross] = 1844.50
    WHERE [id] = @OfferId;


    COMMIT TRANSACTION;


    /* ---------------------------------------------------------
       Ergebnis
       --------------------------------------------------------- */

    SELECT
        [id],
        [offer_number],
        [offer_date],
        [valid_until],
        [status],
        [customer_number],
        [customer_name],
        [subtotal_net],
        [tax_amount],
        [total_gross],
        [active],
        [archived],
        [archived_at],
        [archive_path]
    FROM [dbo].[offer]
    WHERE [id] = @OfferId;


    SELECT
        [id],
        [offer_id],
        [position_number],
        [product_id],
        [product_number],
        [product_name],
        [description],
        [quantity],
        [unit],
        [unit_price_net],
        [discount_percent],
        [vat_rate],
        [line_net],
        [tax_amount],
        [line_gross],
        [notes]
    FROM [dbo].[offer_item]
    WHERE [offer_id] = @OfferId
    ORDER BY [position_number];

END TRY

BEGIN CATCH

    IF @@TRANCOUNT > 0
        ROLLBACK TRANSACTION;

    THROW;

END CATCH;
GO
```
## 2. Erwartete Summen

Das Angebot sollte danach haben:
```Code
AN-2026-000001

Position 1:
5 × 150,00 EUR
Netto       750,00
USt         142,50
Brutto      892,50

Position 2:
8 × 100,00 EUR
Netto       800,00
USt         152,00
Brutto      952,00

--------------------------------
Nettosumme  1.550,00 EUR
USt           294,50 EUR
Gesamt      1.844,50 EUR
```

Der Datenbankstatus muss zunächst sein:
```Code
archived     = 0
archived_at  = NULL
archive_path = NULL
```
# Optinale Positionen


```Code
ALTER TABLE [dbo].[offer_item]
ADD [optional] BIT NOT NULL
    CONSTRAINT [DF_OFFER_ITEM_OPTIONAL]
    DEFAULT ((0));
GO
```
ä SQL-Testdaten

Das folgende Skript legt AN-2026-000002 mit drei Positionen an:

Position 1: regulär
Position 2: regulär
Position 3: optional
Position 3 hat eigene Preise und Summen, fließt aber nicht in offer.subtotal_net, tax_amount und total_gross ein.

```Sql
USE [Firma];
GO

SET XACT_ABORT ON;
GO

BEGIN TRANSACTION;

BEGIN TRY

    DECLARE @OfferId BIGINT;

    /* ---------------------------------------------------------
       Eventuell vorhandenes Testangebot entfernen
       --------------------------------------------------------- */

    SELECT
        @OfferId = [id]
    FROM [dbo].[offer]
    WHERE [offer_number] = N'AN-2026-000002';

    IF @OfferId IS NOT NULL
    BEGIN
        DELETE FROM [dbo].[offer_item]
        WHERE [offer_id] = @OfferId;

        DELETE FROM [dbo].[offer]
        WHERE [id] = @OfferId;
    END;


    /* ---------------------------------------------------------
       Angebot
       --------------------------------------------------------- */

    INSERT INTO [dbo].[offer]
    (
        [offer_number],
        [offer_date],
        [valid_until],
        [status],

        [company_id],
        [customer_id],

        [customer_number],
        [customer_name],
        [customer_contact_person],
        [customer_department],
        [customer_street],
        [customer_zip_code],
        [customer_city],
        [customer_country],
        [customer_vat_id],

        [currency_code],
        [payment_term_days],

        [subtotal_net],
        [tax_amount],
        [total_gross],

        [introduction_text],
        [closing_text],
        [notes],

        [active],

        [archived],
        [archived_at],
        [archive_path]
    )
    VALUES
    (
        N'AN-2026-000002',
        '2026-10-13',
        '2026-11-13',
        N'CREATED',

        1,
        1,

        N'K1145',
        N'ProTip AG',
        NULL,
        NULL,
        N'Moitzfeld 96',
        N'51429',
        N'Bergisch Gladbach',
        N'Deutschland',
        NULL,

        N'EUR',
        30,

        0.00,
        0.00,
        0.00,

        N'Vielen Dank für Ihre Anfrage. Gerne unterbreiten wir Ihnen folgendes Angebot.',

        N'Optional gekennzeichnete Positionen sind nicht im Gesamtbetrag enthalten.',

        N'Testangebot mit einer optionalen Position.',

        1,

        0,
        NULL,
        NULL
    );

    SET @OfferId = SCOPE_IDENTITY();


    /* =========================================================
       Position 1
       Reguläre Position

       5 x 150,00 = 750,00 netto
       USt. 19 % = 142,50
       Brutto = 892,50
       ========================================================= */

    INSERT INTO [dbo].[offer_item]
    (
        [offer_id],
        [position_number],

        [product_id],
        [product_number],
        [product_name],
        [description],

        [quantity],
        [unit],

        [unit_price_net],
        [discount_percent],
        [vat_rate],

        [line_net],
        [tax_amount],
        [line_gross],

        [optional],

        [notes]
    )
    VALUES
    (
        @OfferId,
        1,

        NULL,
        N'P1003',
        N'Projektleitung',
        N'Projektmanagement und Projektkoordination',

        5.0000,
        N'STD',

        150.0000,
        0.0000,
        19.0000,

        750.00,
        142.50,
        892.50,

        0,

        NULL
    );


    /* =========================================================
       Position 2
       Reguläre Position

       8 x 100,00 = 800,00 netto
       USt. 19 % = 152,00
       Brutto = 952,00
       ========================================================= */

    INSERT INTO [dbo].[offer_item]
    (
        [offer_id],
        [position_number],

        [product_id],
        [product_number],
        [product_name],
        [description],

        [quantity],
        [unit],

        [unit_price_net],
        [discount_percent],
        [vat_rate],

        [line_net],
        [tax_amount],
        [line_gross],

        [optional],

        [notes]
    )
    VALUES
    (
        @OfferId,
        2,

        NULL,
        N'SRV-001',
        N'Architektur- und Beratungsleistungen',
        N'Analyse, Architektur und technisches Softwaredesign',

        8.0000,
        N'STD',

        100.0000,
        0.0000,
        19.0000,

        800.00,
        152.00,
        952.00,

        0,

        NULL
    );


    /* =========================================================
       Position 3
       OPTIONALE POSITION

       5 x 100,00 = 500,00 netto
       USt. 19 % = 95,00
       Brutto = 595,00

       Diese Position darf NICHT in die Angebotssumme
       eingerechnet werden.
       ========================================================= */

    INSERT INTO [dbo].[offer_item]
    (
        [offer_id],
        [position_number],

        [product_id],
        [product_number],
        [product_name],
        [description],

        [quantity],
        [unit],

        [unit_price_net],
        [discount_percent],
        [vat_rate],

        [line_net],
        [tax_amount],
        [line_gross],

        [optional],

        [notes]
    )
    VALUES
    (
        @OfferId,
        3,

        NULL,
        N'OPT-001',
        N'Zusätzliche Beratung',
        N'Optionale zusätzliche Beratungs- und Unterstützungsleistungen',

        5.0000,
        N'STD',

        100.0000,
        0.0000,
        19.0000,

        500.00,
        95.00,
        595.00,

        1,

        N'OPTIONALE POSITION - nur bei zusätzlicher Beauftragung.'
    );


    /* =========================================================
       Angebotssummen

       Nur Position 1 + 2:

       Netto:
       750,00 + 800,00 = 1.550,00

       Steuer:
       142,50 + 152,00 = 294,50

       Brutto:
       1.550,00 + 294,50 = 1.844,50

       Position 3 mit 595,00 EUR brutto wird NICHT
       berücksichtigt.
       ========================================================= */

    UPDATE [dbo].[offer]
    SET
        [subtotal_net] = 1550.00,
        [tax_amount] = 294.50,
        [total_gross] = 1844.50
    WHERE [id] = @OfferId;


    COMMIT TRANSACTION;


    /* =========================================================
       Kontrolle Angebot
       ========================================================= */

    SELECT
        [id],
        [offer_number],
        [offer_date],
        [valid_until],
        [status],
        [customer_number],
        [customer_name],
        [subtotal_net],
        [tax_amount],
        [total_gross],
        [active],
        [archived],
        [archived_at],
        [archive_path]
    FROM [dbo].[offer]
    WHERE [id] = @OfferId;


    /* =========================================================
       Kontrolle Positionen
       ========================================================= */

    SELECT
        [id],
        [offer_id],
        [position_number],
        [product_number],
        [product_name],
        [quantity],
        [unit],
        [unit_price_net],
        [discount_percent],
        [vat_rate],
        [line_net],
        [tax_amount],
        [line_gross],
        [optional],
        [notes]
    FROM [dbo].[offer_item]
    WHERE [offer_id] = @OfferId
    ORDER BY [position_number];

END TRY

BEGIN CATCH

    IF @@TRANCOUNT > 0
        ROLLBACK TRANSACTION;

    THROW;

END CATCH;
GO
```


```Code

```


```Code

```


```Code

```
