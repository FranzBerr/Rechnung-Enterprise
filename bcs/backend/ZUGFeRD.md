# Ich möchte ZUGFeRD

Ja. ZUGFeRD ist für Rechnung-Enterprise der logische nächste Schritt, gerade weil deine XRechnung-Erzeugung und KoSIT-Validierung jetzt stabil laufen.

Wichtig ist nur: Ich würde nicht mehr mit ZUGFeRD 2.4 neu anfangen. Nach aktuellem Stand ist inzwischen ZUGFeRD 2.5.2 / Factur-X 1.09.2 veröffentlicht; 2.4 war der vorherige Stand.

## Mein Vorschlag für Rechnung-Enterprise

Wir erweitern den bestehenden Ablauf:

```Plain Text
InvoiceEntity
     |
     +----> PdfInvoiceGenerator
     |             |
     |             +----> normale Rechnung.pdf
     |
     +----> XRechnungXmlGenerator
                   |
                   +----> XRechnung.xml

NEU:

InvoiceEntity
     |
     +----> ZugferdXmlGenerator
     |             |
     |             +----> factur-x.xml
     |
     +----> PdfInvoiceGenerator
                   |
                   +----> PDF/A-3
                              |
                              + eingebettet: factur-x.xml
                              |
                              +----> ZUGFeRD-Rechnung.pdf
```

## Warum?

ZUGFeRD/Factur-X besteht genau aus diesen beiden Komponenten: einem menschenlesbaren PDF und einer darin eingebetteten strukturierten XML-Rechnung. FeRD beschreibt ZUGFeRD/Factur-X entsprechend als hybrides elektronisches Rechnungsformat aus PDF und XML.

Wir müssen deine funktionierende Rechnungslogik daher nicht neu entwickeln.

#1. Profilentscheidung

Für Rechnung-Enterprise würde ich zunächst das EN-16931-Profil umsetzen.

Nicht MINIMUM und nicht BASIC.

Damit haben wir für deine typischen Rechnungsdaten eine gute Grundlage:

* Verkäufer
* Kunde
* Rechnungsnummer
* Rechnungsdatum
* Leistungszeitraum
* Positionen
* Mengen
* Preise
* Rabatte
* 7 % und 19 %
* Steueraufteilung
* Zahlungsinformationen
* IBAN/BIC
* Bestellreferenz
* Gesamtsummen

Die aktuelle ZUGFeRD-Version bleibt EN-16931-orientiert; FeRD beschreibt außerdem verschiedene Profile und die Möglichkeit, auch die CIUS XRechnung als Referenzprofil einzubinden.

# 2. Ganz wichtig: UBL nicht einfach übernehmen

Hier liegt der größte technische Unterschied.

Deine aktuelle XRechnung ist:
```Plain Text
UBL 2.1
```
ZUGFeRD verwendet dagegen UN/CEFACT Cross Industry Invoice (CII).

Das heißt:

```Plain Text
XRechnungXmlGenerator
        |
        +---- UBL XML

ZugferdXmlGenerator
        |
        +---- CII XML
```
Wir sollten deshalb nicht versuchen, deinen funktionierenden UBL-Generator in einen ZUGFeRD-Generator umzubauen.

Der bestehende:
```Plain Text
XRechnungXmlGenerator
```
bleibt erhalten.

Wir ergänzen beispielsweise:
```Plain Text
ZugferdXmlGenerator
```
Damit trennen wir die beiden Syntaxen sauber.

# 3. Noch besser: gemeinsame Rechnungsdaten vorbereiten

Langfristig würde ich sogar eine gemeinsame Zwischenschicht einführen:
```Plain Text
InvoiceEntity
      |
      v
ElectronicInvoiceData
      |
      +---------------------+
      |                     |
      v                     v
XRechnungXmlGenerator   ZugferdXmlGenerator
      |                     |
      v                     v
    UBL 2.1              CII XML
```
Zum Beispiel:
```Java
public class ElectronicInvoiceData {

    private String invoiceNumber;
    private LocalDate invoiceDate;
    private LocalDate dueDate;

    private SellerData seller;
    private BuyerData buyer;

    private List<ElectronicInvoiceItemData> items;

    private List<TaxData> taxes;

    private BigDecimal netAmount;
    private BigDecimal taxAmount;
    private BigDecimal grossAmount;
    private BigDecimal payableAmount;

    private PaymentData payment;
}
```
# 4. Deine RE-2026-000074 wird unser erster ZUGFeRD-Test

Und genau deshalb war deine umfangreiche Testrechnung so wertvoll.

Wir nehmen:

RE-2026-000074

mit:
* 20 Positionen
* HUR
* C62
* 7 %
* 19 %
* Positionsrabatt
* BuyerReference
* OrderReference
* IBAN
* BIC
* mehrzeiligen Beschreibungen
* unterschiedlichen Artikeln


und erzeugen daraus zusätzlich:


```Plain Text
RE-2026-000074.pdf
```
als ZUGFeRD-PDF.

Im PDF befindet sich dann die strukturierte XML.

# 5. PDFBox können wir weiterverwenden

Das ist für dein Projekt der besonders interessante Teil.

Wir haben den vorhandenen PDFBox-Generator bereits.

Der Ablauf wird also ungefähr:
```Plain Text
InvoicePdfGenerator
        |
        v
temporäres PDF
        |
        v
ZugferdPdfService
        |
        +-- PDF/A-3 Anforderungen
        |
        +-- CII XML einbetten
        |
        +-- Metadaten
        |
        v
RE-2026-000074.pdf
```
er wesentliche neue Baustein wäre damit beispielsweise:
```Plain Text
invoice/
   pdf/
      InvoicePdfGenerator.java

   xrechnung/
      XRechnungXmlGenerator.java
      XRechnungValidationService.java

   zugferd/
      ZugferdXmlGenerator.java
      ZugferdPdfService.java
      ZugferdValidationService.java

   einvoice/
      ElectronicInvoiceData.java
      ElectronicInvoiceItemData.java
      TaxData.java
      PaymentData.java
      ElectronicInvoiceMapper.java
```
Damit bleibt deine derzeitige Architektur sauber erweiterbar.

6. Auch das Archiv können wir elegant erweitern

Du hast bereits deine Dokumentarchivierung.

Ich würde daraus später beispielsweise machen:

Plain Text
```Plain Text
archive/
└── 2026/
    └── invoices/
        └── RE-2026-000074/
            ├── RE-2026-000074.pdf
            ├── RE-2026-000074-xrechnung.xml
            ├── RE-2026-000074-validation-report.xml
            └── RE-2026-000074-zugferd.pdf
```
So können wir unterschiedliche elektronische Ausgabeformate aus derselben InvoiceEntity erzeugen.

# 7. Ich würde keine externe ZUGFeRD-Komplettbibliothek als Kern verwenden

Da du PDFBox bereits kontrolliert einsetzt und wir deine bestehende Rechnungslogik inzwischen gut kennen, würde ich die Geschäftslogik nicht komplett an eine externe ZUGFeRD-Bibliothek abgeben.

Stattdessen:

Unsere Daten und Berechnungen bleiben führend.

Neue Komponenten übernehmen lediglich:

1. CII-XML-Erzeugung
2. PDF/A-Aufbereitung
3. XML-Einbettung
4. Metadaten
5. Validierung


Das passt besser zu deiner bisherigen Architektur, bei der InvoiceEntity, DTOs, Services und deine eigene PDF-/XRechnung-Erzeugung erhalten bleiben.

### Zielbild

Am Ende könnte Rechnung-Enterprise bei einer Rechnung beispielsweise drei Ausgabearten anbieten:
```Plain Text
Rechnung erstellen

[ PDF ]
[ XRechnung ]
[ ZUGFeRD ]
```
oder später:

```Plain Text
Elektronisches Rechnungsformat

○ PDF
○ XRechnung
● ZUGFeRD
```

``Intern:`Plain Text
                    Invoice
                       |
             ElectronicInvoiceData
               /       |        \
              /        |         \
             v         v          v
           PDF     XRechnung    ZUGFeRD
                     UBL          CII
                                   |
                                   v
                                PDF/A-3
                              + factur-x.xml
```
Mein konkreter nächster Schritt

Ich würde jetzt noch nicht mit dem PDF beginnen.

Wir sollten zuerst diese Reihenfolge nehmen:

Schritt 1: ElectronicInvoiceData definieren
 Schritt 2: ElectronicInvoiceMapper aus deiner vorhandenen InvoiceEntity bauen
 Schritt 3: ZugferdXmlGenerator implementieren
 Schritt 4: RE-2026-000074 als CII erzeugen und validieren
 Schritt 5: erst danach ZugferdPdfService mit PDFBox und PDF/A-3 aufbauen
 Schritt 6: factur-x.xml in das PDF einbetten
 Schritt 7: vollständige ZUGFeRD-Datei validieren
 Schritt 8: Frontend und Archiv erweitern

So vermeiden wir, XML-, PDF/A- und Einbettungsprobleme gleichzeitig debuggen zu müssen.

Ich würde daher als Nächstes mit ElectronicInvoiceData.java und den zugehörigen Klassen anfangen. Das schafft uns gleichzeitig eine saubere Basis, auf der anschließend sowohl die bestehende XRechnung als auch ZUGFeRD aufsetzen könne
