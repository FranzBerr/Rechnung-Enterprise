import axios from "axios";

import type { ApiError } from "./ApiError";

/**
 * Allgemeine Ersatzmeldung für Fehler, die nicht eindeutig
 * als Backend-ApiError erkannt werden können.
 */
const DEFAULT_ERROR_MESSAGE =
  "Bei der Verarbeitung der Anfrage ist ein Fehler aufgetreten.";

/**
 * Meldung für den Fall, dass das Backend nicht erreichbar ist
 * oder keine HTTP-Antwort zurückgegeben hat.
 */
const NETWORK_ERROR_MESSAGE =
  "Das Backend ist nicht erreichbar. Bitte prüfe die Verbindung und versuche es erneut.";

/**
 * Prüft, ob ein unbekannter Wert ein normales Objekt ist.
 *
 * Arrays und null werden nicht als gültige Objekte akzeptiert.
 *
 * @param value zu prüfender Wert
 * @returns true, wenn value ein Objekt ist
 */
const isRecord = (
  value: unknown
): value is Record<string, unknown> => {

  return (
    typeof value === "object"
    && value !== null
    && !Array.isArray(value)
  );
};


/**
 * Prüft, ob ein unbekannter Wert die Struktur des
 * zentralen Backend-Typs ApiError besitzt.
 *
 * Diese Laufzeitprüfung ist notwendig, weil TypeScript-Typen
 * nach dem Kompilieren nicht mehr vorhanden sind.
 *
 * @param value zu prüfender Wert
 * @returns true, wenn value wie ein ApiError aufgebaut ist
 */
export const isApiError = (
  value: unknown
): value is ApiError => {

  if (!isRecord(value)) {
    return false;
  }

  const validationErrors = value.validationErrors;

  const hasValidValidationErrors =
    validationErrors === null
    || isStringRecord(validationErrors);

  return (
    typeof value.timestamp === "string"
    && typeof value.status === "number"
    && typeof value.error === "string"
    && typeof value.message === "string"
    && typeof value.path === "string"
    && hasValidValidationErrors
  );
};


/**
 * Prüft, ob ein unbekanntes Objekt ausschließlich
 * Zeichenketten als Werte enthält.
 *
 * Diese Prüfung wird für validationErrors verwendet.
 *
 * Beispiel:
 *
 * {
 *     customerNumber: "darf nicht leer sein",
 *     name: "darf nicht leer sein"
 * }
 *
 * @param value zu prüfender Wert
 * @returns true, wenn alle Werte Zeichenketten sind
 */
const isStringRecord = (
  value: unknown
): value is Record<string, string> => {

  if (!isRecord(value)) {
    return false;
  }

  return Object.values(value).every(
    (entry) => typeof entry === "string"
  );
};


/**
 * Liest einen ApiError aus einem unbekannten Fehlerwert aus.
 *
 * Die Methode unterstützt:
 *
 * 1. Axios-Fehler mit Backend-Antwort
 * 2. Direkt übergebene ApiError-Objekte
 * 3. Alle anderen unbekannten Fehler
 *
 * @param error Fehler aus einem catch-Block
 * @returns ApiError oder null
 */
export const getApiError = (
  error: unknown
): ApiError | null => {

  /*
   * Normaler Fall:
   *
   * Das Backend hat geantwortet und Axios stellt die
   * Fehlerantwort unter error.response.data bereit.
   */
  if (axios.isAxiosError(error)) {

    const responseData: unknown = error.response?.data;

    if (isApiError(responseData)) {
      return responseData;
    }

    return null;
  }

  /*
   * Unterstützt den Fall, dass bereits direkt ein
   * ApiError-Objekt an den Helper übergeben wurde.
   */
  if (isApiError(error)) {
    return error;
  }

  return null;
};


/**
 * Ermittelt eine benutzerfreundliche Fehlermeldung.
 *
 * Priorität:
 *
 * 1. Meldung aus dem Backend-ApiError
 * 2. Netzwerkfehler ohne Backend-Antwort
 * 3. Meldung eines normalen JavaScript-Errors
 * 4. Allgemeine Ersatzmeldung
 *
 * @param error Fehler aus einem catch-Block
 * @param fallbackMessage optionale individuelle Ersatzmeldung
 * @returns verständliche Fehlermeldung
 */
export const getErrorMessage = (
  error: unknown,
  fallbackMessage: string = DEFAULT_ERROR_MESSAGE
): string => {

  const apiError = getApiError(error);

  if (apiError?.message.trim()) {
    return apiError.message;
  }

  /*
   * Bei einem Axios-Fehler ohne response konnte keine
   * HTTP-Antwort vom Backend empfangen werden.
   */
  if (
    axios.isAxiosError(error)
    && error.response === undefined
  ) {
    return NETWORK_ERROR_MESSAGE;
  }

  /*
   * Unterstützt lokale JavaScript- oder TypeScript-Fehler.
   */
  if (
    error instanceof Error
    && error.message.trim()
  ) {
    return error.message;
  }

  return fallbackMessage;
};


/**
 * Liest die feldbezogenen Validierungsfehler
 * aus einem Backend-ApiError aus.
 *
 * Wenn keine Feldfehler vorhanden sind, wird ein
 * leeres Objekt zurückgegeben. Dadurch kann das Ergebnis
 * direkt in einen React-State übernommen werden.
 *
 * @param error Fehler aus einem catch-Block
 * @returns Validierungsfehler nach Feldnamen
 */
export const getValidationErrors = (
  error: unknown
): Record<string, string> => {

  const apiError = getApiError(error);

  return apiError?.validationErrors ?? {};
};


/**
 * Liefert die Validierungsmeldung eines bestimmten Feldes.
 *
 * Beispiel:
 *
 * getFieldError(error, "customerNumber")
 *
 * @param error Fehler aus einem catch-Block
 * @param fieldName Name des Formularfeldes
 * @returns Feldmeldung oder undefined
 */
export const getFieldError = (
  error: unknown,
  fieldName: string
): string | undefined => {

  const validationErrors =
    getValidationErrors(error);

  return validationErrors[fieldName];
};


/**
 * Prüft, ob der Fehler einen bestimmten HTTP-Statuscode besitzt.
 *
 * Beispiele:
 *
 * isHttpStatus(error, 400)
 * isHttpStatus(error, 404)
 * isHttpStatus(error, 409)
 *
 * @param error Fehler aus einem catch-Block
 * @param status erwarteter HTTP-Statuscode
 * @returns true, wenn der Statuscode übereinstimmt
 */
export const isHttpStatus = (
  error: unknown,
  status: number
): boolean => {

  const apiError = getApiError(error);

  if (apiError !== null) {
    return apiError.status === status;
  }

  if (axios.isAxiosError(error)) {
    return error.response?.status === status;
  }

  return false;
};
