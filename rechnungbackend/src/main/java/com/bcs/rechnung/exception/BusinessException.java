package com.bcs.rechnung.exception;

/**
 * Exception für fachliche Fehler bzw. Verletzungen
 * von Geschäftsregeln.
 *
 * <p>
 * Diese Exception wird verwendet, wenn eine Anfrage
 * technisch korrekt ist, aber aufgrund einer fachlichen
 * Regel nicht ausgeführt werden kann.
 * </p>
 *
 * <p>Beispiele:</p>
 *
 * <ul>
 *     <li>Kundennummer existiert bereits</li>
 *     <li>Rechnung wurde bereits archiviert</li>
 *     <li>Ein Datensatz darf aufgrund bestehender
 *         Abhängigkeiten nicht gelöscht werden</li>
 * </ul>
 *
 * <p>
 * Die Exception wird zentral vom GlobalExceptionHandler
 * behandelt und dort in HTTP 409 CONFLICT umgesetzt.
 * </p>
 */
public class BusinessException extends RuntimeException {

    /**
     * Erstellt eine neue BusinessException.
     *
     * @param message fachliche Fehlermeldung
     */
    public BusinessException(String message) {
        super(message);
    }
}
