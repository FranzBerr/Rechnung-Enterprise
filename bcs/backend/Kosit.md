# Ergebnis Kosit
a, das ist jetzt ein sehr gutes Ergebnis. Für RE-2026-000074 ist die XRechnung-Validierung vollständig erfolgreich durchgelaufen. Besonders wichtig ist, dass nicht nur das XML-Schema akzeptiert wird, sondern auch beide Schematron-Prüfungen und die abschließende Acceptance-Prüfung.

Prüfung	ErgebnisKoSIT Validator	1.6.3
Konfiguration	XRechnung 3.0.2
Dokumenttyp	EN16931 XRechnung (UBL Invoice)
UBL	2.1
Rechnungsnummer	RE-2026-000074
Rechnungsdatum	27.10.2026
XML Schema	gültig
EN16931 Schematron	gültig
XRechnung Schematron	gültig
Fehler	0
Warnungen	0
Acceptance	ACCEPTABLE
Gesamtergebnis	VALID / angenommen

Der entscheidende Satz aus dem Prüfbericht ist:
```Code
„Das geprüfte Dokument enthält weder Fehler noch Warnungen. Es ist konform zu den formalen Vorgaben.“
```
Und die Validator-Konsole bestätigt zusätzlich:
```Code
Acceptable: 1 Rejected: 0
Validation successful!
```
Damit haben wir den entscheidenden Punkt erreicht: Der von Rechnung-Enterprise erzeugte XRechnung-UBL-Datensatz RE-2026-000074 wird von KoSIT ohne Beanstandung akzeptiert.

Auch die drei einzelnen Validierungsschritte sind sau

Im Report stehen:

* val-xsd valid="true"
* val-sch.1 valid="true"
* val-sch.2 valid="true"
* zusätzlich val-xml valid="true"

Das ist wichtig, weil damit unterschiedliche Ebenen erfolgreich geprüft wurden:

1. UBL-2.1 XML-Schema
2.EN16931-Regeln
3. CIUS XRechnung 3.0.2
4. abschließende XML-/Dokumentenprüfung

Es handelt sich also nicht nur um „wohlgeformtes XML“.

# Besonders positiv: die fachlichen Konstrukte funktionieren

Deine Testrechnung ist für einen Integrationstest ziemlich gut gewählt, weil sie mehrere Fälle gleichzeitig enthält.

# 1. Mehrere Umsatzsteuersätze

Der Validator erkennt:

7 %

* steuerpflichtiger Betrag: 599,80 EUR
* Umsatzsteuer: 41,99 EUR

19 %

* steuerpflichtiger Betrag: 6.897,50 EUR
* Umsatzsteuer: 1.310,53 EUR

Gesamtsteuer:

1.352,52 EUR

Die Rechnung stimmt:

41,99 + 1.310,53 = 1.352,52 EUR

Damit funktioniert deine Gruppierung nach Steuersatz.

#2. 2. Die Gesamtsummen stimmen

Der Report enthält:



|Wert      | Betrag       |
|----------------|----------------|
| LineExtensionAmount | 7.497,30 € |
| TaxExclusiveAmount | 7.497,30 € |
| TaxAmount | 1.352,52 € |
| TaxInclusiveAmount | 8.849,82 € |
| PayableAmount | 8.849,82 €|

Auch hier ist die Rechnung korrekt:

7.497,30 + 1.352,52 = 8.849,82 EUR

Das ist für unseren XRechnungXmlGenerator besonders wichtig, weil hier früher leicht Rundungs- oder Aggregationsfehler auftreten konnten.
# 3. Der Positionsrabatt funktioniert


Position 5 ist für mich einer der wichtigsten Tests des gesamten Dokuments.

Ausgangswert:

3 × 95,00 € = 285,00 €

Rabatt:

10 % von 285,00 € = 28,50 €

Positionsnetto:

285,00 - 28,50 = 256,50 €

Und genau das steht im XML-Bericht:
* ChargeIndicator = false
* * AllowanceChargeReason = Positionsrabatt
MultiplierFactorNumeric = 10
* Amount = 28.50
* BaseAmount = 285.00
* LineExtensionAmount = 256.50

Damit ist auch die AllowanceCharge-Implementierung erfolgreich validiert.

Das ist ein wichtiger Meilenstein, weil Rechnung-Enterprise damit nicht nur einfache Rechnungspositionen, sondern auch Positionsrabatte XRechnung-konform darstellen kann.

# 4. Mengeneinheiten funktionieren

Du hast verschiedene Einheiten im Dokument:

## Stunden

HUR

beispielsweise:

8 × 88,00 EUR

für „Beratung WinDev“.

## Stück

C62

beispielsweise:

2 × 49,90 EUR

für „Fachbuch Softwarearchitektur“.

Auch diese Mischung akzeptiert KoSIT ohne Warnung.
# 5. Die Zahlungsdaten werden akzeptiert

Enthalten sind unter anderem:

* PaymentMeansCode = 58
* PaymentID = RE-2026-000074
* IBAN
* BIC
* Kontoinhaber

Auch dieser Bereich verursacht keine XRechnung-Warnung und keinen Fehler.

# 6. Seller und Buyer sind vollständig genug

Der Rechnungssteller wird richtig erkannt:

Berr Computersysteme GmbH

Der Käufer:

ProTip AG

Auch die elektronische Adressierung über

EndpointID mit schemeID="EM"

wird auf beiden Seiten akzeptiert.

Ebenso sind beim Seller unter anderem vorhanden:

* Anschrift
* Land DE
* Umsatzsteuer-ID
* Steuernummer
* Firmenname
* Ansprechpartner
* Telefon
* E-Mail

Das zeigt, dass unsere Party-Struktur grundsätzlich sauber aufgebaut ist.

# 7. BuyerReference und OrderReference

Interessant ist ebenfalls:

BuyerReference = 47352-30490

und

OrderReference/ID = 47352-30490

Beide werden akzeptiert.

Damit funktionieren auch diese für B2B- und insbesondere XRechnung-Prozesse wichtigen Referenzen.

# 8. Alle 20 Positionen wurden verarbeitet

Das ist ebenfalls ein guter Belastungstest.

Der Report enthält tatsächlich die InvoiceLines

1 ... 20.

Dabei kommen unterschiedliche Fälle vor:

* Dienstleistungen
* Stundenabrechnung
* Stückartikel
* 7 % MwSt.
* 19 % MwSt.
* Positionsrabatt
* mehrzeilige Beschreibungen
* unterschiedliche Artikelnummern
* unterschiedliche Preise und Mengen

Und trotzdem endet die Prüfung mit:

0 Fehlern und 0 Warnungen.

# Ein Detail würde ich trotzdem noch technisch prüfen

Im Rabatt steht:

MultiplierFactorNumeric = 10

KoSIT akzeptiert diesen Wert offensichtlich vollständig und erzeugt weder Fehler noch Warnung.

Trotzdem würde ich beim nächsten Schritt einmal gezielt unseren Generator ansehen, insbesondere die interne Behandlung von:
```Plain Text
discountPercent
MultiplierFactorNumeric
AllowanceCharge
Amount
BaseAmount
```

Nicht weil die Rechnung momentan ungültig wäre. Sie ist nach dem vorliegenden KoSIT-Ergebnis gültig. Sondern weil wir sicherstellen sollten, dass die interne Semantik unserer Prozentwerte konsistent bleibt, beispielsweise zwischen Frontend, Datenbank, PDF und XML.

# Mein Fazit für Rechnung-Enterprise

Ich würde den aktuellen XRechnung-Stand jetzt tatsächlich als erfolgreich integriert betrachten:

Rechnung-Enterprise

→ Rechnung mit 20 Positionen  
 → unterschiedliche Einheiten  
 → 7 % und 19 % Umsatzsteuer  
 → Positionsrabatt  
 → UBL 2.1  
 → EN16931  
 → XRechnung 3.0.2  
 → KoSIT Validator 1.6.3  
 → Schema Y  
 → Schematron Y 
 → ACCEPTABLE  
 → 0 Fehler  
 → 0 Warnungen  

Das ist wesentlich aussagekräftiger als unsere früheren Minimaltests.
# Was ich als Nächstes machen würde

Ich würde am XML-Generator jetzt zunächst nichts Grundsätzliches mehr umbauen. Stattdessen würde ich den erfolgreichen Stand absichern:

* RE-2026-000074 als XRechnung-Referenztest verwenden.
* Einen automatisierten Integrationstest für genau diese Rechenfälle erstellen.
* Netto, Steuergruppen, Rabatt und Zahlbetrag in Java nochmals unabhängig berechnen und gegen die erwarteten Werte prüfen.
* Anschließend den PDF-/Archiv-Workflow mit derselben Rechnung testen.
* Danach erst weitere Sonderfälle angehen, beispielsweise 0-%-Positionen, steuerbefreite Rechnungen, Gutschrift sowie Dokumentrabatte/-zuschläge.

Für unseren bisherigen Rechnung-Enterprise-XRechnung-Workflow ist RE-2026-000074 damit ein sehr guter „Golden Test Case“. Der wichtigste nächste Schritt wäre aus meiner Sicht deshalb nicht mehr die Reparatur des Generators, sondern ein automatisierter Regressionstest, der verhindert, dass spätere Änderungen an InvoiceService, DTOs oder XRechnungXmlGenerator diesen jetzt erreichten Zustand wieder kaputtmachen.
