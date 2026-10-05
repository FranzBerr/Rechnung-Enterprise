# Wichtige Anmerkung

Diese 3 Teile ergeben eine einfache, kompilierbare Basisversion für PDFBox 3.x und dein aktuelles Datenmodell.

Für den produktiven Einsatz würde ich als nächsten Schritt noch ergänzen:

* automatischer Seitenumbruch ok
* Tabellenkopf auf Folgeseiten
* Seitenzahlen
* Firmenlogo
* bessere Spaltenausrichtung (rechtsbündige Beträge)
* PDF/A-3
* ZUGFeRD/XRechnung-Einbettung
* mehrzeilige Positionsbeschreibungen

Das wäre dann die professionelle Version der Rechnungs-PDF.


# ich habe Wesentliche Verbesserungen gemacnt:
* Seitenumbruch vor einer Position, wenn deren berechnete Zeilenhöhe nicht mehr passt
* Tabellenkopf auf jeder neuen Positionsseite
* variabler Zeilenumbruch für lange Artikelbeschreibungen
* neue Seite vor Summen und Zahlungsinformationen, wenn der Platz nicht reicht
* mehrseitige Anmerkungen
* Seitenzahlen im Format Seite 1 von 3
* kompatibel mit productId statt einer ProductEntity-Beziehung
* PDFBox-3-Schriftenerzeugung über Standard14Fonts
* Java-17-kompatibler Code
* lokaler NumberFormat, dadurch keine gemeinsam genutzte, nicht threadsichere Formatter-Instanz
