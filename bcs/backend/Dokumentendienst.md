# Dokumentendienst


Für dein aktuelles Design würde ich den Package-Baum so aufbauen:

```Plain Text
com.bcs.rechnung.invoice.document
│
├── ArchivedInvoiceDocuments.java
├── InvalidXRechnungException.java
│
├── InvoiceDocumentProperties.java
│
├── InvoiceDocumentController.java
├── InvoiceDocumentService.java
├── InvoiceDocumentServiceImpl.java
│
├── KositValidationResult.java
├── KositValidationService.java
├── KositValidationServiceImpl.java
│
├── InvoiceDocumentArchiveEntity.java
├── InvoiceDocumentArchiveRepository.java
├── InvoiceDocumentArchiveController.java
├── InvoiceDocumentArchiveService.java
├── InvoiceDocumentArchiveServiceImpl.java
│
├── mapper
│   └── InvoiceDocumentArchiveMapper.java
│
└── dto
    └── ArchivedDocumentDto.java
```

Archivierungsworkflow
Der eigentliche Workflow besteht aus genau diesen Schritte
```Plain Text
POST /api/invoices/{invoiceId}/documents/save

1. PDF erzeugen
2. XRechnung erzeugen
3. temporär speichern
4. KoSIT validieren
5. valid = true ?
6. Archivordner anlegen
7. PDF archivieren
8. XML archivieren
9. Prüfbericht archivieren
10. Validator-Log archivieren
11. Datenbankeintrag anlegen
12. Ergebnis zurückgeben
```

## Zielstruktur
```Code
C:\
└── PolyRackDummy
    └── Rechning gesamt
        └── Rechnung-Enterprise-formatiert
            └── Rechnung-Enterprise
                │
                ├── archive
                │   └── 2026
                │       └── RE-2026-000002
                │           ├── RE-2026-000002.pdf
                │           ├── RE-2026-000002.xml
                │           └── RE-2026-000002-validation-report.xml
                │
                ├── temp
                │
                ├── XRechnungValidator
                │   ├── validator-1.6.3-standalone.jar
                │   └── configuration
                │       ├── scenarios.xml
                │       └── ...
                │
                └── Rechnung-Enterprise.jar
```
Der neue Endpoint:
```HTTP
POST /api/invoices/4/documents/save
```
Ablauf:
```Code
PDF erzeugen
    ↓
XRechnung erzeugen
    ↓
XML in temporäres Verzeichnis schreiben
    ↓
KoSIT-Validierung ausführen
    ↓
Prüfbericht auswerten
    ↓
nur bei valid="true" archivieren
```
application.yml
```YAML
invoice-documents:
  archive-directory: "C:/PolyRackDummy/Rechning gesamt/Rechnung-Enterprise-formatiert/Rechnung-Enterprise/archive"

  temp-directory: "C:/PolyRackDummy/Rechning gesamt/Rechnung-Enterprise-formatiert/Rechnung-Enterprise/temp"

  validator-jar: "C:/PolyRackDummy/Rechning gesamt/Rechnung-Enterprise-formatiert/Rechnung-Enterprise/XRechnungValidator/validator-1.6.3-standalone.jar"

  validator-scenario: "C:/PolyRackDummy/Rechning gesamt/Rechnung-Enterprise-formatiert/Rechnung-Enterprise/XRechnungValidator/configuration/scenarios.xml"

  validator-repository: "C:/PolyRackDummy/Rechning gesamt/Rechnung-Enterprise-formatiert/Rechnung-Enterprise/XRechnungValidator/configuration"

  validator-timeout-seconds: 60
```
Mein Vorschlag für die weitere Umsetzung
## Phase 1

Zuerst diese Klassen anlegen:
```Plain Text
InvoiceDocumentProperties
GeneratedInvoicePdf
GeneratedXRechnung
ArchivedInvoiceDocuments
InvoiceDocumentService
InvoiceDocumentServiceImpl
InvoiceDocumentController
```
## Phase 2

Dann den Endpoint:
```HTTP
POST /api/invoices/{invoiceId}/documents/save
```
implementieren.

Er soll:
```Plain Text
PDF erzeugen
↓
XML erzeugen
↓
temporär speichern
↓
KoSIT Validator starten
↓
valid=true ?
```
Wenn:
```Plain Text
valid=true
```
dann
```Code
archive/2026/RE-2026-000002/
    RE-2026-000002.pdf
    RE-2026-000002.xml
    RE-2026-000002-validation-report.xml
```
speichern.

## Phase 3

Bei Fehler:
```Plain Text
XR-Fehler
BR-DE-Fehler
Schematron-Fehler

JSON
```
soll der Endpoint sofort abbrechen:


```JSON
{
  "valid": false,
  "invoiceNumber": "RE-2026-000002",
  "errors": [
    "BR-DE-21 ..."
  ]
}
```
und nichts archivieren.

## Phase 4

Später zusätzlich:
```HTTP
GET /api/invoices/{invoiceId}/documents/download
```
mit
```Code
RE-2026-000002.pdf
RE-2026-000002.xml
RE-2026-000002-validation-report.xml
```
Meine Empfehlung: Als nächstes solltest du tatsächlich mit InvoiceDocumentProperties, ArchivedInvoiceDocuments und InvoiceDocumentController beginnen. 
Danach bauen wir den InvoiceDocumentServiceImpl, der PDF-Erzeugung, XRechnung-Erzeugung und KoSIT-Validierung in einem einzigen Workflow zusammenführt.

Unten findest du die vollständige Basis für den neuen Workflow: PDF und XML werden temporär erzeugt, die XML wird mit dem lokalen KoSIT Validator geprüft 
und nur bei valid="true" gemeinsam mit dem Prüfbericht archiviert. 
Der Validator unterstützt den Standalone-Betrieb per CLI, benötigt eine Szenariokonfiguration und erzeugt einen XML-Prüfbericht.

## Zielstruktur
```Code
com.bcs.rechnung.invoice.document 
├── ArchivedInvoiceDocuments.java 
├── InvoiceDocumentController.java 
├── InvoiceDocumentProperties.java 
├── InvoiceDocumentService.java 
├── InvoiceDocumentServiceImpl.java 
├── InvalidXRechnungException.java 
├── KositValidationResult.java 
├── KositValidationService.java
└── KositValidationServiceImpl.java
```

## HTTP
```Code
POST /api/invoices/4/documents/save
```
Bei Erfolg:
```JSON
{
  "invoiceNumber": "RE-2026-000002",
  "archiveDirectory": "C:\\PolyRackDummy\\...\\archive\\2026\\RE-2026-000002",
  "pdfFile": "C:\\PolyRackDummy\\...\\RE-2026-000002.pdf",
  "xmlFile": "C:\\PolyRackDummy\\...\\RE-2026-000002.xml",
  "validationReport": "C:\\PolyRackDummy\\...\\RE-2026-000002-validation-report.xml",
  "valid": true,
  "warnings": []
}
```
Im Archiv:
```Plain Text
archive
└── 2026
    └── RE-2026-000002
        ├── RE-2026-000002.pdf
        ├── RE-2026-000002.xml
        └── RE-2026-000002-validation-report.xml
```
Bei einer ungültigen XML wird nichts archiviert. Das temporäre Arbeitsverzeichnis bleibt in diesem Fall erhalten, damit du den KoSIT-Prüfbericht untersuchen kannst.


alles funktiuoniert

# Was ich als Nächstes machen würde
## 1. Archivdatenbank

Aktuell liegt das Archiv nur im Dateisystem.

Ergänze eine Tabelle:
```Code
CREATE TABLE invoice_document_archive
(
    id BIGINT IDENTITY(1,1) NOT NULL,

    invoice_id BIGINT NOT NULL,

    invoice_number VARCHAR(50) NOT NULL,

    archive_directory VARCHAR(1000) NOT NULL,

    pdf_file VARCHAR(1000) NOT NULL,

    xml_file VARCHAR(1000) NOT NULL,

    validation_report VARCHAR(1000),

    validator_log VARCHAR(1000),

    valid BIT NOT NULL,

    archived_at DATETIME2 NOT NULL,

    CONSTRAINT PK_invoice_document_archive
        PRIMARY KEY (id)
);
GO

CREATE UNIQUE INDEX UX_invoice_document_archive_invoice_id
ON invoice_document_archive(invoice_id);
GO

CREATE UNIQUE INDEX UX_invoice_document_archive_invoice_number
ON invoice_document_archive(invoice_number);
GO
```
Damit kannst du später:
```Plain Text
Suche
Filter
Downloads
Historie
``
```
realisieren.

## 4. Komplettes empfohlenes Skript

Vor dem Anlegen prüft dieses Skript, ob die jeweiligen Indizes bereits existieren:
```Code
USE [Firma];
GO


/* ============================================================
   INVOICE
   ============================================================ */

IF NOT EXISTS
(
    SELECT 1
    FROM sys.indexes
    WHERE name = N'IX_INVOICE_INVOICE_DATE'
      AND object_id = OBJECT_ID(N'dbo.invoice')
)
BEGIN
    CREATE INDEX [IX_INVOICE_INVOICE_DATE]
    ON [dbo].[invoice] ([invoice_date]);
END;
GO


IF NOT EXISTS
(
    SELECT 1
    FROM sys.indexes
    WHERE name = N'IX_INVOICE_CUSTOMER_ID'
      AND object_id = OBJECT_ID(N'dbo.invoice')
)
BEGIN
    CREATE INDEX [IX_INVOICE_CUSTOMER_ID]
    ON [dbo].[invoice] ([customer_id]);
END;
GO


IF NOT EXISTS
(
    SELECT 1
    FROM sys.indexes
    WHERE name = N'IX_INVOICE_COMPANY_ID'
      AND object_id = OBJECT_ID(N'dbo.invoice')
)
BEGIN
    CREATE INDEX [IX_INVOICE_COMPANY_ID]
    ON [dbo].[invoice] ([company_id]);
END;
GO


IF NOT EXISTS
(
    SELECT 1
    FROM sys.indexes
    WHERE name = N'IX_INVOICE_STATUS'
      AND object_id = OBJECT_ID(N'dbo.invoice')
)
BEGIN
    CREATE INDEX [IX_INVOICE_STATUS]
    ON [dbo].[invoice] ([status]);
END;
GO


IF NOT EXISTS
(
    SELECT 1
    FROM sys.indexes
    WHERE name = N'IX_INVOICE_ARCHIVED_INVOICE_DATE'
      AND object_id = OBJECT_ID(N'dbo.invoice')
)
BEGIN
    CREATE INDEX [IX_INVOICE_ARCHIVED_INVOICE_DATE]
    ON [dbo].[invoice]
    (
        [archived],
        [invoice_date]
    )
    INCLUDE
    (
        [invoice_number],
        [archived_at],
        [archive_path]
    );
END;
GO


/* ============================================================
   INVOICE_ITEM
   ============================================================ */

IF NOT EXISTS
(
    SELECT 1
    FROM sys.indexes
    WHERE name = N'IX_INVOICE_ITEM_INVOICE_POSITION'
      AND object_id = OBJECT_ID(N'dbo.invoice_item')
)
BEGIN
    CREATE INDEX [IX_INVOICE_ITEM_INVOICE_POSITION]
    ON [dbo].[invoice_item]
    (
        [invoice_id],
        [position_number]
    );
END;
GO


IF NOT EXISTS
(
    SELECT 1
    FROM sys.indexes
    WHERE name = N'IX_INVOICE_ITEM_PRODUCT_ID'
      AND object_id = OBJECT_ID(N'dbo.invoice_item')
)
BEGIN
    CREATE INDEX [IX_INVOICE_ITEM_PRODUCT_ID]
    ON [dbo].[invoice_item] ([product_id]);
END;
GO


/* ============================================================
   INVOICE_DOCUMENT_ARCHIVE
   ============================================================ */

IF NOT EXISTS
(
    SELECT 1
    FROM sys.indexes
    WHERE name = N'UX_INVOICE_DOCUMENT_ARCHIVE_INVOICE_ID'
      AND object_id =
          OBJECT_ID(N'dbo.invoice_document_archive')
)
BEGIN
    CREATE UNIQUE INDEX
        [UX_INVOICE_DOCUMENT_ARCHIVE_INVOICE_ID]
    ON [dbo].[invoice_document_archive]
        ([invoice_id]);
END;
GO


IF NOT EXISTS
(
    SELECT 1
    FROM sys.indexes
    WHERE name =
        N'UX_INVOICE_DOCUMENT_ARCHIVE_INVOICE_NUMBER'
      AND object_id =
          OBJECT_ID(N'dbo.invoice_document_archive')
)
BEGIN
    CREATE UNIQUE INDEX
        [UX_INVOICE_DOCUMENT_ARCHIVE_INVOICE_NUMBER]
    ON [dbo].[invoice_document_archive]
        ([invoice_number]);
END;
GO


IF NOT EXISTS
(
    SELECT 1
    FROM sys.indexes
    WHERE name =
        N'IX_INVOICE_DOCUMENT_ARCHIVE_ARCHIVED_AT'
      AND object_id =
          OBJECT_ID(N'dbo.invoice_document_archive')
)
BEGIN
    CREATE INDEX
        [IX_INVOICE_DOCUMENT_ARCHIVE_ARCHIVED_AT]
    ON [dbo].[invoice_document_archive]
        ([archived_at] DESC);
END;
GO
```


```Code
```


```Code
```


```Code
```


```Code
```

```Code
```


```Code
```


```Code
```

