# Lieferscheine

## 1. Empfohlene Backend-Struktur
```Code
com.bcs.rechnung
│
├── company
├── customer
├── product
├── invoice
│
└── deliverynote
    ├── entity
    │   ├── DeliveryNote.java
    │   └── DeliveryNoteItem.java
    │
    ├── dto
    ├── mapper
    ├── repository
    ├── service
    ├── controller
    └── document
```
Passend zu den gerade erstellten DeliveryNoteEntity und DeliveryNoteItemEntity sowie deinen vorhandenen Tabellen customer und product würde ich die beiden SQL-Server-Tabellen so anlegen
## 1. Tabelle delivery_note
```Sql
USE [Firma]
GO

CREATE TABLE [dbo].[delivery_note]
(
    [id] BIGINT IDENTITY(1,1) NOT NULL,

    [delivery_note_number] NVARCHAR(30) NOT NULL,

    [customer_id] BIGINT NOT NULL,

    [delivery_date] DATE NOT NULL,

    -- Lieferanschrift als Beleg-Snapshot
    [delivery_name] NVARCHAR(200) NOT NULL,
    [delivery_contact_person] NVARCHAR(200) NULL,
    [delivery_department] NVARCHAR(200) NULL,
    [delivery_street] NVARCHAR(200) NULL,
    [delivery_zip_code] NVARCHAR(20) NULL,
    [delivery_city] NVARCHAR(100) NULL,
    [delivery_country] NVARCHAR(100) NULL,

    -- Auftrags- und Versandinformationen
    [order_reference] NVARCHAR(100) NULL,
    [shipping_method] NVARCHAR(100) NULL,
    [tracking_number] NVARCHAR(100) NULL,

    [notes] NVARCHAR(2000) NULL,

    [active] BIT NOT NULL,

    CONSTRAINT [PK_DELIVERY_NOTE]
        PRIMARY KEY CLUSTERED
        (
            [id] ASC
        ),

    CONSTRAINT [UK_DELIVERY_NOTE_NUMBER]
        UNIQUE
        (
            [delivery_note_number]
        )
)
GO
```
Default für active
```Sql
ALTER TABLE [dbo].[delivery_note]
ADD CONSTRAINT [DF_DELIVERY_NOTE_ACTIVE]
DEFAULT ((1)) FOR [active]
GO
```
Foreign Key zum Kunden
```Code
ALTER TABLE [dbo].[delivery_note]
WITH CHECK ADD CONSTRAINT [FK_DELIVERY_NOTE_CUSTOMER]
FOREIGN KEY ([customer_id])
REFERENCES [dbo].[customer] ([id])
GO

ALTER TABLE [dbo].[delivery_note]
CHECK CONSTRAINT [FK_DELIVERY_NOTE_CUSTOMER]
GO
```
## 2. Tabelle delivery_note_item
```Sql
USE [Firma]
GO

CREATE TABLE [dbo].[delivery_note_item]
(
    [id] BIGINT IDENTITY(1,1) NOT NULL,

    [delivery_note_id] BIGINT NOT NULL,

    [position_number] INT NOT NULL,

    -- Produktreferenz ist absichtlich optional
    [product_id] BIGINT NULL,

    -- Produktdaten als Beleg-Snapshot
    [product_number] NVARCHAR(30) NULL,

    [name] NVARCHAR(200) NOT NULL,

    [description] NVARCHAR(1000) NULL,

    [quantity] DECIMAL(19,4) NOT NULL,

    [unit] NVARCHAR(30) NOT NULL,

    [notes] NVARCHAR(1000) NULL,

    CONSTRAINT [PK_DELIVERY_NOTE_ITEM]
        PRIMARY KEY CLUSTERED
        (
            [id] ASC
        )
)
GO
```
## 3. Foreign Key zum Lieferschein
```Sql
ALTER TABLE [dbo].[delivery_note_item]
WITH CHECK ADD CONSTRAINT [FK_DELIVERY_NOTE_ITEM_DELIVERY_NOTE]
FOREIGN KEY ([delivery_note_id])
REFERENCES [dbo].[delivery_note] ([id])
GO

ALTER TABLE [dbo].[delivery_note_item]
CHECK CONSTRAINT [FK_DELIVERY_NOTE_ITEM_DELIVERY_NOTE]
GO
```
4. Foreign Key zum Produkt

## Deine vorhandene Produkttabelle ist:
```Code
dbo.product
```
Deshalb:
```Sql
ALTER TABLE [dbo].[delivery_note_item]
WITH CHECK ADD CONSTRAINT [FK_DELIVERY_NOTE_ITEM_PRODUCT]
FOREIGN KEY ([product_id])
REFERENCES [dbo].[product] ([id])
GO

ALTER TABLE [dbo].[delivery_note_item]
CHECK CONSTRAINT [FK_DELIVERY_NOTE_ITEM_PRODUCT]
GO
```
## 5. Sinnvolle Indizes

Zusätzlich würde ich folgende Indizes anlegen:
```Code
CREATE INDEX [IX_DELIVERY_NOTE_CUSTOMER_ID]
ON [dbo].[delivery_note]
(
    [customer_id]
)
GO


CREATE INDEX [IX_DELIVERY_NOTE_DELIVERY_DATE]
ON [dbo].[delivery_note]
(
    [delivery_date]
)
GO


CREATE INDEX [IX_DELIVERY_NOTE_ITEM_DELIVERY_NOTE_ID]
ON [dbo].[delivery_note_item]
(
    [delivery_note_id]
)
GO


CREATE INDEX [IX_DELIVERY_NOTE_ITEM_PRODUCT_ID]
ON [dbo].[delivery_note_item]
(
    [product_id]
)
GO
```
## 6. Position innerhalb eines Lieferscheins eindeutig machen

Ich würde zusätzlich verhindern, dass ein Lieferschein beispielsweise zweimal die Position 1 bekommt.

Dafür ist ein kombinierter Unique Constraint sinnvoll:

```Code
ALTER TABLE [dbo].[delivery_note_item]
ADD CONSTRAINT [UK_DELIVERY_NOTE_ITEM_POSITION]
UNIQUE
(
    [delivery_note_id],
    [position_number]
)
GO
```
Damit ist erlaubt:
```Code
LS 1    Position 1
LS 1    Position 2
LS 1    Position 3

LS 2    Position 1
LS 2    Position 2
```
aber nicht:
```Code
LS 1    Position 1
LS 1    Position 1   <-- Fehler
```
## 7. Beziehung der Tabellen

Damit haben wir jetzt:
```Code
company
   │
   ├──────────── customer
   │                 │
   │                 │ customer_id
   │                 ▼
   │           delivery_note
   │                 │
   │                 │ delivery_note_id
   │                 ▼
   │          delivery_note_item
   │                 │
   │                 │ product_id
   │                 ▼
   └──────────── product
```
Dabei werden wichtige Informationen bewusst auf dem Lieferschein gespeichert:
```Code
customer
   │
   │ Snapshot bei Erstellung
   ▼
delivery_note
   delivery_name
   delivery_contact_person
   delivery_department
   delivery_street
   delivery_zip_code
   delivery_city
   delivery_country
```
und entsprechend beim Produkt:
```Code
product
   │
   │ Snapshot bei Erstellung
   ▼
delivery_note_item
   product_number
   name
   description
   unit
```


Dadurch bleiben bereits erstellte Lieferscheine fachlich stabil, auch wenn später beispielsweise Produktbezeichnung oder Kundenadresse geändert werden.
package com.bcs.rechnung.deliverynote;

### DeliveryNoteRepository
```Code
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DeliveryNoteRepository
extends JpaRepository<DeliveryNoteEntity, Long> {

    Optional<DeliveryNoteEntity> findByDeliveryNoteNumber(
        String deliveryNoteNumber
    );

    boolean existsByDeliveryNoteNumber(
        String deliveryNoteNumber
    );

    List<DeliveryNoteEntity> findByCustomerId(
        Long customerId
    );

    List<DeliveryNoteEntity>
    findByCustomerIdOrderByDeliveryDateDesc(
        Long customerId
    );

    List<DeliveryNoteEntity>
    findAllByOrderByDeliveryDateDesc();
}
```
Damit können wir später beispielsweise

einen Lieferschein über seine ID laden:
```Code
deliveryNoteRepository.findById(id);
```
über die Lieferscheinnummer:
```Code
deliveryNoteRepository
    .findByDeliveryNoteNumber(
        "LS-2026-000073"
    );
```
oder alle Lieferscheine eines Kunden:
```Code
deliveryNoteRepository
    .findByCustomerId(customerId);
```
### 2. DeliveryNoteItemRepository.java
```Java
package com.bcs.rechnung.deliverynote;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DeliveryNoteItemRepository
    extends JpaRepository<DeliveryNoteItemEntity, Long> {

    List<DeliveryNoteItemEntity>
    findByDeliveryNoteIdOrderByPositionNumber(
        Long deliveryNoteId
    );

    List<DeliveryNoteItemEntity>
    findByProductId(
        Long productId
    );

    void deleteByDeliveryNoteId(
        Long deliveryNoteId
    );
}
```
### 3. Brauchen wir das ItemRepository überhaupt?

Technisch könnten wir sogar zunächst ohne DeliveryNoteItemRepository arbeiten.

Der Grund ist diese Beziehung in DeliveryNoteEntity:
```Java
@OneToMany(
    mappedBy = "deliveryNote",
    cascade = CascadeType.ALL,
    orphanRemoval = true
)
@OrderBy("positionNumber ASC")
private List<DeliveryNoteItemEntity> items =
    new ArrayList<>();
```
Dadurch können wir Positionen über den Lieferschein verwalten:
```Java
deliveryNote.addItem(item);

deliveryNoteRepository.save(deliveryNote);
```
Durch:
```Java
cascade = CascadeType.ALL
```
wird die neue Position zusammen mit dem Lieferschein persistiert.

Auch das Löschen:
```Java
deliveryNote.removeItem(item);

deliveryNoteRepository.save(deliveryNote);
```
passt zu:
```Java
orphanRemoval = true
```
Für die eigentliche Geschäftslogik würde ich deshalb bevorzugt über DeliveryNoteEntity arbeiten.

Das DeliveryNoteItemRepository ist trotzdem nützlich, sobald wir Positionen unabhängig suchen oder auswerten wollen.

### 4. Package-Struktur

Momentan arbeiten wir mit:
```Code
com.bcs.rechnung.deliverynote
│
├── DeliveryNoteEntity.java
├── DeliveryNoteItemEntity.java
├── DeliveryNoteRepository.java
└── DeliveryNoteItemRepository.java
```
Das ist für den jetzigen Umfang völlig in Ordnung.

Später können wir das Modul bei Bedarf weiter strukturieren:
```Code
com.bcs.rechnung.deliverynote
│
├── controller
├── dto
├── mapper
├── repository
├── service
└── pdf
```
Entsprechend:
```Code
src/main/java/com/bcs/rechnung/deliverynote/
│
├── DeliveryNoteEntity.java
├── DeliveryNoteItemEntity.java
│
├── controller/
│   └── DeliveryNoteController.java
│
├── dto/
│   ├── DeliveryNoteDto.java
│   ├── DeliveryNoteItemDto.java
│   └── DeliveryNoteRequest.java
│
├── mapper/
│   └── DeliveryNoteMapper.java
│
├── repository/
│   ├── DeliveryNoteRepository.java
│   └── DeliveryNoteItemRepository.java
│
├── service/
│   ├── DeliveryNoteService.java
│   └── DeliveryNoteServiceImpl.java
│
└── pdf/
    └── DeliveryNotePdfService.java
```
Abhängigkeiten

Die Struktur wird dann klar:
```Code
DeliveryNoteController
          │
          ▼
DeliveryNoteService
          │
          ▼
DeliveryNoteServiceImpl
          │
          ├──────────────► DeliveryNoteRepository
          │
          ├──────────────► DeliveryNoteItemRepository
          │
          └──────────────► DeliveryNoteMapper
                                  │
                                  ▼
                         DTOs <-> Entities
```
Die PDF-Erzeugung bleibt ebenfalls getrennt:
```Code
DeliveryNotePdfService
          │
          ▼
   DeliveryNoteDto
          │
          ▼
       PDF-Datei
```
Mit „alternative PDF-Variante“ meinte ich keine andere Bibliothek. Ich würde bei deinem Projekt klar bei Apache PDFBox bleiben.

Gemeint war nur eine alternative Gestaltung bzw. Struktur des Lieferscheins.

## Variante 1: Rechnungsdesign übernehmen

Das ist der Weg, den wir gerade gewählt haben:

Plain Text
```Code
Berr Computersysteme GmbH                     LIEFERSCHEIN
Sägewerkstraße 1
82205 Gilching
Germany
────────────────────────────────────────────────────────

Lieferempfänger                      Lieferscheinnummer: LS-...
ProTip AG                            Lieferdatum:        ...
Moitzfeld 96                         Kundennummer:       ...
51429 Bergisch Gladbach              Auftragsreferenz:   ...
Deutschland                          Versandart:         ...

Pos.    Beschreibung                         Menge Einheit
────────────────────────────────────────────────────────
1       P1000 - IT Beratung WinDev             16  STD
        Projektvertrag ...
```
Das entspricht optisch weitgehend deiner Rechnung.

Variante 2: Klassischer Lieferschein

Alternativ könnte der Lieferschein stärker auf Logistik ausgerichtet werden:

```Code
LIEFERSCHEIN LS-2026-000073

Lieferadresse                  Versandinformationen
ProTip AG                      Versandart: ...
Moitzfeld 96                   Tracking: ...
51429 Bergisch Gladbach        Auftrag: ...

Pos.  Artikel    Bezeichnung          Menge  Einheit
1     P1000      IT Beratung WinDev      16  STD

Bemerkungen

Ausgeliefert durch: __________
Empfangen durch:    __________
Datum:               __________
Unterschrift:        __________
```
Ich würde daher keine alternative Variante einführen, sondern unseren aktuellen DeliveryNotePdfGeneratorImpl weiter exakt an dein Rechnungsdesign angleichen.


Mit Unetschriften Feldern:
```Code
Auslieferung                         Warenempfang

_______________________              _______________________
Datum                                Datum


_______________________              _______________________
Name                                 Name


_______________________              _______________________
Unterschrift                         Unterschrift
```

Die Seitenumbruchlogik konzentriert sich jetzt im Wesentlichen auf:
```Code
hasSpace(...)
newPage(...)
newPageIfNeeded(...)
```
Erstelle Testdaten für Customer:
Testdaten für Customer:

id	company_id	customer_number	name	contact_person	street	zip_code	city	country	phone	email	vat_id	active	leitweg_id	electronic_address	electronic_address_scheme	business_process_id	buyer_reference	department
1	1	K100000	Muster GmbH	Max Mustermann	Hauptstrasse 10	80331	München	DE	089123456	info@muster.de	DE111111111	1	NULL	NULL	NULL	NULL	NULL	NULL
6	1	K1146	POLYRACK TECH-GROUP	Frau Giebelhäuser	Heinrich-Hertz-Str. 26	75334	Straubenhardt	Straubenhardt	+49 (0)7082 7919-0	info@polyrack.com	NULL	1	NULL	NULL	NULL	NULL	NULL	NULL
und Produkte:
4	1	P1003	Projektleitung	Projektmanagement	STD	150.0000	1
5	1	SRV-001	Architektur- und Beratungsleistungen	Beratung, Architektur und Softwaredesign	HUR	100.0000	1


```Sql
DECLARE @DeliveryNoteId1 BIGINT;

INSERT INTO [dbo].[delivery_note]
(
    [delivery_note_number],
    [customer_id],
    [delivery_date],
    [delivery_name],
    [delivery_contact_person],
    [delivery_department],
    [delivery_street],
    [delivery_zip_code],
    [delivery_city],
    [delivery_country],
    [order_reference],
    [shipping_method],
    [tracking_number],
    [notes],
    [active]
)
VALUES
(
    N'LS-2026-000002',
    1,
    '2026-10-21',
    N'Muster GmbH',
    N'Max Mustermann',
    NULL,
    N'Hauptstrasse 10',
    N'80331',
    N'München',
    N'DE',
    N'AUF-2026-0002',
    N'Persönliche Übergabe',
    NULL,
    N'Test-Lieferschein Muster GmbH',
    1
);

SET @DeliveryNoteId1 = SCOPE_IDENTITY();


INSERT INTO [dbo].[delivery_note_item]
(
    [delivery_note_id],
    [position_number],
    [product_id],
    [product_number],
    [name],
    [description],
    [quantity],
    [unit],
    [notes]
)
VALUES
(
    @DeliveryNoteId1,
    1,
    4,
    N'P1003',
    N'Projektleitung',
    N'Projektmanagement',
    8.0000,
    N'STD',
    N'Projektleitung vor Ort'
);


INSERT INTO [dbo].[delivery_note_item]
(
    [delivery_note_id],
    [position_number],
    [product_id],
    [product_number],
    [name],
    [description],
    [quantity],
    [unit],
    [notes]
)
VALUES
(
    @DeliveryNoteId1,
    2,
    5,
    N'SRV-001',
    N'Architektur- und Beratungsleistungen',
    N'Beratung, Architektur und Softwaredesign',
    16.0000,
    N'HUR',
    N'Architektur- und Beratungsleistungen'
);

SELECT
    @DeliveryNoteId1 AS DeliveryNoteId;
GO
```
## Zweiter Lieferschein für POLYRACK TECH-GROUP

```Code
DECLARE @DeliveryNoteId2 BIGINT;

INSERT INTO [dbo].[delivery_note]
(
    [delivery_note_number],
    [customer_id],
    [delivery_date],
    [delivery_name],
    [delivery_contact_person],
    [delivery_department],
    [delivery_street],
    [delivery_zip_code],
    [delivery_city],
    [delivery_country],
    [order_reference],
    [shipping_method],
    [tracking_number],
    [notes],
    [active]
)
VALUES
(
    N'LS-2026-000003',
    6,
    '2026-10-21',
    N'POLYRACK TECH-GROUP',
    N'Frau Giebelhäuser',
    NULL,
    N'Heinrich-Hertz-Str. 26',
    N'75334',
    N'Straubenhardt',
    N'Straubenhardt',
    N'47352-30490',
    N'Persönliche Übergabe',
    N'TEST-TRACKING-0001',
    N'Projektleistungen gemäß Auftrag 47352-30490.',
    1
);

SET @DeliveryNoteId2 = SCOPE_IDENTITY();


INSERT INTO [dbo].[delivery_note_item]
(
    [delivery_note_id],
    [position_number],
    [product_id],
    [product_number],
    [name],
    [description],
    [quantity],
    [unit],
    [notes]
)
VALUES
(
    @DeliveryNoteId2,
    1,
    4,
    N'P1003',
    N'Projektleitung',
    N'Projektmanagement',
    8.0000,
    N'STD',
    N'Projektleitung'
);


INSERT INTO [dbo].[delivery_note_item]
(
    [delivery_note_id],
    [position_number],
    [product_id],
    [product_number],
    [name],
    [description],
    [quantity],
    [unit],
    [notes]
)
VALUES
(
    @DeliveryNoteId2,
    2,
    5,
    N'SRV-001',
    N'Architektur- und Beratungsleistungen',
    N'Beratung, Architektur und Softwaredesign',
    16.0000,
    N'HUR',
    N'Beratungsleistung'
);

SELECT
    @DeliveryNoteId2 AS DeliveryNoteId;
GO
```

Auslieferung und Warenempfang nicht nur als leere Unterschriftsfelder im PDF, sondern dauerhaft in SQL Server speichern möchtest, würde ich delivery_note um sechs Felder erweiter

```Code
USE [Firma]
GO

ALTER TABLE [dbo].[delivery_note]
ADD
    [delivery_date_signed] DATE NULL,
    [delivery_person] NVARCHAR(200) NULL,
    [delivery_signature] VARBINARY(MAX) NULL,

    [receipt_date_signed] DATE NULL,
    [receipt_person] NVARCHAR(200) NULL,
    [receipt_signature] VARBINARY(MAX) NULL;
GO
```
## Archivierung:

1. Datenbank um Archivinformationen ergänzen

Falls noch nicht vorhanden:
```Sql
USE [Firma]
GO

ALTER TABLE [dbo].[delivery_note]
ADD
    [archived] BIT NOT NULL
        CONSTRAINT [DF_DELIVERY_NOTE_ARCHIVED]
        DEFAULT ((0)),

    [archived_at] DATETIME2 NULL,

    [archive_path] NVARCHAR(1000) NULL;
GO
```
## 2. DeliveryNoteEntity ergänzen

Zusätzlicher Import:

```Code

```


```Code

```


```Code

```


```Code

```
