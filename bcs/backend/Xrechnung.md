# Norm
```Code
EN 16931
   │
   └── XRechnung 3.x
          │
          └── CIUS Deutschland
```
# 1. EN 16931 (Europäische Norm)
  
  Das ist die wichtigste Norm.
  
  Sie definiert:
  
* Rechnungsnummer
* Rechnungsdatum
* Verkäufer
* Käufer
* Steuerdaten
* Zahlungsdaten
* Rechnungspositionen
* Summen
  
  Alle modernen E-Rechnungsformate basieren darauf.

# 2. XRechnung 3.x

Die deutsche XRechnung erweitert die EN16931 um deutsche Anforderungen.

Beispiele:
```Code
1. BuyerReference
2. Leitweg-ID
3, BusinessProcessID
4. SpecificationIdentifier
```

# 3. XML-Syntax

Wir werden die XML als
```Code
<rsm:CrossIndustryInvoice>
```
erzeugen.

Das ist das sogenannte:
```Code
UN/CEFACT Cross Industry Invoice (CII)
```
und wird von XRechnung gefordert.

Beginn der XML:
```Code
<rsm:CrossIndustryInvoice
 xmlns:rsm="urn:un:unece:uncefact:data:standard:CrossIndustryInvoice:100"
 xmlns:ram="urn:un:unece:uncefact:data:standard:ReusableAggregateBusinessInformationEntity:100"
 xmlns:udt="urn:un:unece:uncefact:data:standard:UnqualifiedDataType:100">
```
4. Welche XRechnung-Version?

Ich würde aktuell unterstützen:
```Code
XRechnung 3.0.x
```
Deshalb haben wir bereits:
```Code
private String specificationIdentifier =
    "urn:xeinkauf.de:kosit:xrechnung_3.0";
```
eingebaut.

5. Welche Daten aus deinem Modell werden verwendet?
   
InvoiceEntity
```Code
invoiceNumber
invoiceDate
serviceDate
dueDate

buyerReference
orderReference

invoiceTypeCode
paymentMeansCode
currencyCode

subtotalNet
taxAmount
totalGross
```
CustomerEntity
```Code
name
street
zipCode
city
country

leitwegId
electronicAddress
electronicAddressScheme
```
CompanyEntity
```Code
name
street
zipCode
city
country

vatId
taxNumber
```
InvoiceItemEntity

```Code
productNumber
description
quantity
unit

unitPriceNet

vatRate
taxCategoryCode

lineNet
taxAmount
lineGross
```
# 6. Zielbild

Am Ende werden wir einen Endpunkt haben:

```Code
GET /api/invoices/{id}/xrechnung

```
der eine Datei liefert:
```Code
RE-2026-000002.xml
```

mit Inhalt:
```Code
<rsm:CrossIndustryInvoice>

    <ram:ExchangedDocument>

    <ram:SellerTradeParty>

    <ram:BuyerTradeParty>

    <ram:SpecifiedTradeSettlementPaymentMeans>

    <ram:IncludedSupplyChainTradeLineItem>

</rsm:CrossIndustryInvoice>
```
Meine Empfehlung

Der nächste Schritt sollte jetzt sein:
```Code
com.bcs.rechnung.invoice.xrechnung
├── InvoiceXmlGenerator.java
├── InvoiceXmlService.java
├── InvoiceXmlServiceImpl.java
└── InvoiceXmlController.java
```

wobei wir direkt eine echte XRechnung 3.x gemäß EN16931 (CII / CrossIndustryInvoice) erzeugen 
und nicht erst ein vereinfachtes XML-Format. 
Das lohnt sich, weil dein Datenmodell inzwischen vollständig genug dafür ist.

# Projekt-Struktur:
```Code
com.bcs.rechnung.invoice.xrechnung
│
├── InvoiceXmlController.java
├── InvoiceXmlService.java
├── InvoiceXmlServiceImpl.java
├── InvoiceXmlGenerator.java
│
└── dto
    ├── XRechnungDto.java
    ├── XRechnungSellerDto.java
    ├── XRechnungBuyerDto.java
    ├── XRechnungTotalsDto.java
    └── XRechnungItemDto.java
```
Hier ist die vollständige InvoiceXmlServiceImpl.java. Sie lädt die Rechnung transaktional, prüft die wichtigsten XRechnungsdaten, mappt InvoiceEntity auf deine DTO-Struktur und übergibt das Ergebnis an den späteren InvoiceXmlGenerator.

XRechnung 3.x setzt unter anderem den Geschäftsprozesstyp sowie elektronische Adressen von Verkäufer und Käufer voraus. Deine bereits vorgesehenen Felder businessProcessId, customerElectronicAddress und customerElectronicAddressScheme passen dazu. 【1-50f881】【2-a8e149】


Ich habe den vollständigen InvoiceXmlGenerator.java für Java 17 erstellt. Er erzeugt eine XRechnung im UN/CEFACT-CII-Format als CrossIndustryInvoice, passend zu deinen vorhandenen DTOs.  

Der Generator enthält:

* XRechnung-3.0-Guideline-ID
* Geschäftsprozess-ID
* Rechnungsnummer, Typ und Datum
* Verkäufer und Käufer
* Leitweg-ID und elektronische Käuferadresse
* Rechnungspositionen
* Mengen, Einheiten und Nettopreise
* Steuerkategorie und Steuersatz
* Zahlungsart und Zahlungsreferenz
* Nettosumme, Steuerbetrag, Bruttosumme und Zahlbetrag
* UTF-8-XML per XMLStreamWriter

Die offizielle XRechnung unterstützt sowohl UBL als auch UN/CEFACT CII; die KoSIT-Validator-Konfiguration enthält Schema- und Schematron-Regeln für beide Syntaxen.

Wichtig: Vor dem produktiven Versand muss das generierte XML zwingend mit der aktuellen KoSIT-Validator-Konfiguration geprüft werden. Das reine Erzeugen einer syntaktisch korrekten CII-Datei garantiert noch keine vollständige XRechnung-Konformität. Die offizielle Validator-Konfiguration liefert dafür XSD- und Schematron-Regeln.

# Validierunbg
Aktuell erzeugt dein InvoiceXmlGenerator zwar XML, aber noch keine rechtssicher validierte XRechnung. Dafür solltest du die offizielle KoSIT Validator Konfiguration integrieren.

## Empfohlene Architektur

```Code
com.bcs.rechnung.invoice.xrechnung
│
├── InvoiceXmlGenerator.java
├── InvoiceXmlService.java
├── InvoiceXmlServiceImpl.java
├── XRechnungValidator.java
├── XRechnungValidationResult.java
│
└── dto
```

# Vohrgehensweise:
### Schritt 1

Zunächst würde ich diese XML erzeugen:
```Code
<?xml version="1.0"?>

<Invoice>

   <Number>RE-2026-0001</Number>

   <Date>2026-09-29</Date>

   <Customer>
      ...
   </Customer>

   <Items>
      ...
   </Items>

   <Totals>
      ...
   </Totals>

</Invoice>
```
### Schritt 2

Danach echtes XRechnung-Format:
```Code
<rsm:CrossIndustryInvoice>

    <ram:SellerTradeParty>

    <ram:BuyerTradeParty>

    <ram:IncludedSupplyChainTradeLineItem>

</rsm:CrossIndustryInvoice>
```
### Schritt 3

Validierung gegen die offiziellen XSD-Dateien.

Dann kannst du prüfen:
```Code
boolean valid =
        xrechnungValidator.validate(
                xml
        );
```

--Schritt 4

ZUGFeRD

Wir erweitern deinen bestehenden
```Code
InvoicePdfGenerator
```
um:
```Code
invoice.pdf
+
invoice.xml
```
und betten die XML ins PDF ein.

Ergebnis:
```Code
rechnung.pdf
```

Mein aktuelles Fazit

Von einer manuellen XRechnungs-Prüfung her sehe ich aktuell:

✅ XRechnung 3.0 Kennung  
✅ Business Process ID  
✅ BuyerReference  
✅ Leitweg-ID  
✅ Verkäuferkontakt  
✅ Verkäufer-Endpunkt  
✅ Käufer-Endpunkt  
✅ Rechnungspositionen  
✅ Mehrzeilige Beschreibungen  
✅ Steuerblock  
✅ Zahlungsblock  
✅ IBAN/BIC  
✅ Summenblock  
✅ Zahlungsreferenz 

Die XML ist nun deutlich professioneller aufgebaut als die vorherigen Versionen.

Der nächste sinnvolle Schritt ist jetzt tatsächlich ein Lauf gegen den offiziellen KoSIT-Validator. 
Erst dort sieht man, ob noch eine konkrete BR-DE-, CII- oder EN16931-Regel beanstandet wird. 
Rein anhand der XML-Struktur sehe ich momentan keinen offensichtlichen Fehler mehr.

# Validierung mit dem offiziellen KoSIT-Validator.

## 1. auf: [https:/github.com/itplr-kosit](https://github.com/itplr-kosit)
oder suche bing.de mit: GitHub - itplr-kosit/validator: Validates XML documents with XML … 

### 1.1 wähle: validator Public: Validates XML documents with XML Schema and Schematron

Gehe zu Releases, damn Assets

validator-1.6.3-standalone.jar sha256:799e64befca97d4080e03608c80b85dd5a5ecc5f4ae4f35d1116ec2855b9a7c9  10.2 MB Aug 20

Clicke darauf und die Datei wird automatsch heruntergeladen -> Doenloads


2. Gehe zu: 
validator-configuration-xrechnung 
Public
Configuration for validating documents against the German XRechnung standard using the KoSIT validation tool 

### Validator Configuration 2026-08-31 compatible with XRechnung 3.0 2026-08-31


# Validierung starten

```Code
cd C:\XRechnungValidator
```

# ausführen:
``` Powershell
java -jar validator-1.6.3-standalone.jar `
  -s scenarios.xml `
-o output `
input\RE-2026-000002.xml
```
Falls scenarios.xml in einem Unterordner liegt:
```Powershell
java -jar validator-1.6.3-standalone.jar `
  -s configuration\scenarios.xml `
  -o output `
  input\RE-2026-000002.xml
```


[invoice-4.xml](../../xrechnung/invoice-4.xml)

# Nächster professioneller Schritt

Da du inzwischen eine vollständig valide XRechnung erzeugst, würde ich jetzt als Nächstes folgende Erweiterungen angehen:

1. PDF + XRechnung als ZUGFeRD/Factur-X Hybrid-PDF
2. Mehrere Steuersätze (7%, 19%, steuerfrei)
3. Skonto in der XRechnung
4. Rechnungsanhänge
5. automatische Peppol-Vorbereitung
6. Integrationstests, die den KoSIT-Validator automatisch aus Maven aufrufen
```Code
```

