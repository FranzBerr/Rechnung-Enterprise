# Offer-Modul

Wegen deiner gerade eingebauten optionalen Positionen ist das Modul etwas umfangreicher:
```Code
*src/
├── api/
│   ├── axiosInstance.ts
│   ├── offerApi.ts
│   ├── companyApi.ts
│   ├── customerApi.ts
│   └── productApi.ts
│
├── dto/
│   ├── offer/
│   │   ├── OfferDto.ts
│   │   ├── OfferItemDto.ts
│   │   ├── OfferRequest.ts
│   │   ├── OfferItemRequest.ts
│   │   └── OfferStatus.ts
│   │
│   ├── company/
│   │   └── CompanyDto.ts
│   │
│   ├── customer/
│   │   └── CustomerDto.ts
│   │
│   └── product/
│       └── ProductDto.ts
│
├── pages/
│   └── offers/
│       ├── OfferListPage.tsx
│       └── OfferFormPage.tsx
│
├── components/
│   └── offers/
│       ├── OfferHeaderForm.tsx
│       ├── OfferCustomerForm.tsx
│       ├── OfferItemsTable.tsx
│       ├── OfferItemDialog.tsx
│       ├── OfferTextsForm.tsx
│       └── OfferTotals.tsx
│
├── routes/
│   └── AppRoutes.tsx
│
└── App.tsx
```
Ich würde jetzt mit OfferFormPage.tsx weitermachen

Dabei sollten wir die Seite direkt richtig auf deine vorhandene Architektur abstimmen.

Die Maske bekommt diese Bereiche:

## Angebotskopf

Angebotsnummer
Angebotsdatum
gültig bis
Status
Firma
Währung
Zahlungsziel
aktiv

## Kunde

Kunden-Auswahl
Name
Ansprechpartner
Abteilung
Straße
PLZ
Ort
Land
USt-ID

## Angebotspositionen

Position
Produkt
Produktnummer
Bezeichnung
Beschreibung
Menge
Einheit
Einzelpreis
Rabatt
MwSt.
optional
Nettobetrag

# Summen

Netto
Steuer
Brutto
optionale Positionen werden nicht eingerechnet

# Texte

Einleitung
Schlusstext
interne Notizen

# Aktionen

Speichern
Abbrechen

# Fehlerbehandlung

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
