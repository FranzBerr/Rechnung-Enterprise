// =========================================================
// Zentrale Fehlerstruktur der REST-API
// =========================================================

// Repräsentiert eine Fehlerantwort des Spring-Boot-Backends.
//
// Beispiel:
//
// {
//   "status": 404,
//   "error": "Not Found",
//   "message": "Angebot mit ID 999 wurde nicht gefunden.",
//   "timestamp": "2026-10-21T20:00:00"
// }
//
// Die optionalen Felder path und validationErrors ermöglichen
// später eine Erweiterung für Validierungsfehler.

export interface ApiError {
  status: number;

  error: string;

  message: string;

  timestamp: string;

  path?: string;

  validationErrors?: Record<string, string>;
}
