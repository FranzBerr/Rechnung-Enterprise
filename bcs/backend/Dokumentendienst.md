# Dokumentendienst

## Zielstruktur
```Code
C:\PolyRackDummy\Rechnungsarchiv
└── 2026
    └── RE-2026-000002
        ├── RE-2026-000002.pdf
        ├── RE-2026-000002.xml
        └── RE-2026-000002-validation-report.xml
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

```Code

```

```Code

```

