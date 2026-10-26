import axios from "axios";

import type {
  ApiError,
} from "./ApiError";


// =========================================================
// ApiError erkennen
// =========================================================

export function isApiError(
  value: unknown,
): value is ApiError {

  if (
    typeof value !== "object" ||
    value === null
  ) {
    return false;
  }

  const apiError =
    value as Partial<ApiError>;

  return (
    typeof apiError.status === "number" &&
    typeof apiError.message === "string"
  );
}


// =========================================================
// ApiError aus Axios-Fehler ermitteln
// =========================================================

export function getApiError(
  error: unknown,
): ApiError | null {

  if (!axios.isAxiosError(error)) {
    return null;
  }

  const data =
    error.response?.data;

  if (isApiError(data)) {
    return data;
  }

  return null;
}


// =========================================================
// Benutzerfreundliche Fehlermeldung erzeugen
// =========================================================

export function getApiErrorMessage(
  error: unknown,
  fallbackMessage =
  "Es ist ein unerwarteter Fehler aufgetreten.",
): string {

  // Zuerst prüfen, ob das Backend bereits
  // eine strukturierte Fehlermeldung geliefert hat.

  const apiError =
    getApiError(error);

  if (apiError?.message) {
    return apiError.message;
  }


  // -------------------------------------------------------
  // Axios-Fehler behandeln
  // -------------------------------------------------------

  if (axios.isAxiosError(error)) {

    // Timeout

    if (
      error.code === "ECONNABORTED" ||
      error.code === "ETIMEDOUT"
    ) {
      return (
        "Die Anfrage hat zu lange gedauert. " +
        "Bitte versuchen Sie es erneut."
      );
    }


    // Netzwerkfehler beziehungsweise
    // keine Antwort vom Backend

    if (!error.response) {
      return (
        "Das Backend konnte nicht erreicht werden. " +
        "Bitte prüfen Sie die Verbindung zum Server."
      );
    }


    // HTTP-Statuscode auswerten

    switch (error.response.status) {

      case 400:
        return (
          "Die übermittelten Daten sind ungültig."
        );

      case 401:
        return (
          "Sie sind nicht angemeldet."
        );

      case 403:
        return (
          "Sie besitzen keine Berechtigung " +
          "für diese Aktion."
        );

      case 404:
        return (
          "Der angeforderte Datensatz " +
          "wurde nicht gefunden."
        );

      case 409:
        return (
          "Die Aktion konnte aufgrund eines " +
          "Datenkonflikts nicht ausgeführt werden."
        );

      case 422:
        return (
          "Die übermittelten Daten konnten " +
          "nicht verarbeitet werden."
        );

      case 500:
        return (
          "Im Backend ist ein interner Fehler aufgetreten."
        );

      case 502:
        return (
          "Das Backend ist momentan nicht erreichbar."
        );

      case 503:
        return (
          "Der Dienst ist momentan nicht verfügbar."
        );

      default:
        return fallbackMessage;
    }
  }


  // -------------------------------------------------------
  // Normaler JavaScript-Fehler
  // -------------------------------------------------------

  if (error instanceof Error) {
    return error.message;
  }


  // -------------------------------------------------------
  // Unbekannter Fehler
  // -------------------------------------------------------

  return fallbackMessage;
}
