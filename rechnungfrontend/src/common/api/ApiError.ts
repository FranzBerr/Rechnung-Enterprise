/**
 * Einheitliche Fehlerantwort des Backends.
 *
 * Entspricht:
 *
 * com.bcs.rechnung.exception.ApiError
 *
 * Dieser Typ wird für Fehler verwendet, die vom
 * GlobalExceptionHandler des Backends zurückgegeben werden.
 *
 * Beispiele:
 *
 * 400 BAD REQUEST
 *     Validierungsfehler
 *
 * 404 NOT FOUND
 *     Ressource wurde nicht gefunden
 *
 * 409 CONFLICT
 *     Fachlicher Konflikt
 *
 * 500 INTERNAL SERVER ERROR
 *     Unerwarteter interner Fehler
 */
export interface ApiError {

  /**
   * Zeitpunkt, zu dem der Fehler im Backend aufgetreten ist.
   *
   * Beispiel:
   * "2026-10-24T14:30:00.123"
   */
  timestamp: string;

  /**
   * Numerischer HTTP-Statuscode.
   *
   * Beispiele:
   * 400
   * 404
   * 409
   * 500
   */
  status: number;

  /**
   * Bezeichnung des HTTP-Status.
   *
   * Beispiele:
   * "Bad Request"
   * "Not Found"
   * "Conflict"
   * "Internal Server Error"
   */
  error: string;

  /**
   * Vom Backend gelieferte Fehlermeldung.
   *
   * Beispiel:
   * "Die Kundennummer K100000 existiert bereits."
   */
  message: string;

  /**
   * REST-Endpunkt, bei dessen Verarbeitung
   * der Fehler entstanden ist.
   *
   * Beispiel:
   * "/api/customers"
   */
  path: string;

  /**
   * Feldbezogene Validierungsfehler.
   *
   * Das Objekt wird insbesondere bei HTTP 400 durch
   * Jakarta Bean Validation zurückgegeben.
   *
   * Beispiel:
   *
   * {
   *     customerNumber: "darf nicht leer sein",
   *     name: "darf nicht leer sein"
   * }
   *
   * Bei Fehlern ohne Feldvalidierung, beispielsweise
   * HTTP 404 oder HTTP 409, liefert das Backend null.
   */
  validationErrors: Record<string, string> | null;
}
