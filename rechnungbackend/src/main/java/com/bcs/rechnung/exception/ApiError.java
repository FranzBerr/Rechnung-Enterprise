package com.bcs.rechnung.exception;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.Map;

@Schema(
    name = "ApiError",
    description = "Einheitliche Fehlerantwort der REST-API"
)
public record ApiError(

    @Schema(
        description = "Zeitpunkt des Fehlers",
        example = "2026-10-23T16:30:00"
    )
    LocalDateTime timestamp,

    @Schema(
        description = "HTTP-Statuscode",
        example = "400"
    )
    int status,

    @Schema(
        description = "HTTP-Fehlerbezeichnung",
        example = "Bad Request"
    )
    String error,

    @Schema(
        description = "Fehlermeldung",
        example = "Die übergebenen Daten sind ungültig."
    )
    String message,

    @Schema(
        description = "Aufgerufener API-Pfad",
        example = "/api/customers"
    )
    String path,

    @Schema(
        description = "Validierungsfehler je Feld"
    )
    Map<String, String> validationErrors

) {
}
