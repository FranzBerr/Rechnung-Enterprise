# Zentrale Fehlerbehandlung

JFür dein Rechnung-Enterprise-Backend würde ich die Fehlerbehandlung und OpenAPI-Dokumentation zentralisieren, statt in jedem CustomerController, ProductController, CompanyController usw. dieselben 400/404/500-Definitionen einzubauen.

springdoc-openapi unterstützt Fehlerdokumentation über @ControllerAdvice; für automatisch dokumentierte HTTP-Statuscodes sollen die Handler ihre Statuscodes beispielsweise über @ResponseStatus deklarieren.

Ich empfehle folgende Struktur:
```Plain Text
com.bcs.rechnung
│
├── config
│   └── OpenApiConfig.java
│
└── exception
    ├── ApiError.java
    ├── ResourceNotFoundException.java
    ├── BusinessException.java
    └── GlobalExceptionHandler.java
```
## 1. Zentrales Fehlerformat ApiError.java

Damit bekommt das Frontend bei Fehlern immer dieselbe Struktur:
```Java
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
```
## 2. ResourceNotFoundException.java
Eine gemeinsame Exception für Kunde nicht gefunden, Firma nicht gefunden, Produkt nicht gefunden usw.:

```Java
package com.bcs.rechnung.exception;

/**
 * Exception für nicht vorhandene Ressourcen.
 *
 * <p>
 * Kann beispielsweise verwendet werden, wenn ein Kunde,
 * Produkt, eine Firma oder eine Rechnung anhand ihrer ID
 * nicht gefunden wurde.
 * </p>
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
```
Dann kann dein CustomerServiceImpl zum Beispiel schreiben:
```Java
CustomerEntity customer = customerRepository.findById(id)
    .orElseThrow(() ->
        new ResourceNotFoundException(
            "Kunde mit ID " + id + " wurde nicht gefunden."
        )
    );
```
Dasselbe Prinzip funktioniert anschließend bei
```Plain Text
1. ProductEntity
2. CompanyEntity
3. BankEntity
4. InvoiceEntity
5. DeliveryNoteEntity
6. OfferEntity
```
## 3. BusinessException.java

Zusätzlich würde ich fachliche Fehler von technischen Fehlern unterscheiden:
```Java
package com.bcs.rechnung.exception;

/**
 * Exception für fachliche Fehler.
 *
 * <p>
 * Wird verwendet, wenn eine Anfrage technisch gültig ist,
 * aber aufgrund einer Geschäftsregel nicht verarbeitet
 * werden kann.
 * </p>
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }
}
```
Beispiele wären später Geschäftsregeln wie:
```Plain Text
1. Kunde kann nicht gelöscht werden
2. Rechnung ist bereits archiviert
3. Angebot besitzt einen ungültigen Status
4. Firma darf nicht deaktiviert werden
```
## 4. Zentraler GlobalExceptionHandler.java

Das ist der zentrale Teil.
```Java
package com.bcs.rechnung.exception;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Behandelt den Fall, dass eine angeforderte Ressource
     * nicht gefunden wurde.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ApiResponse(
        responseCode = "404",
        description = "Die angeforderte Ressource wurde nicht gefunden"
    )
    public ApiError handleResourceNotFound(
        ResourceNotFoundException exception,
        HttpServletRequest request
    ) {
        return new ApiError(
            LocalDateTime.now(),
            HttpStatus.NOT_FOUND.value(),
            HttpStatus.NOT_FOUND.getReasonPhrase(),
            exception.getMessage(),
            request.getRequestURI(),
            null
        );
    }


    /**
     * Behandelt Validierungsfehler aus @Valid.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ApiResponse(
        responseCode = "400",
        description = "Die übergebenen Daten sind ungültig"
    )
    public ApiError handleValidation(
        MethodArgumentNotValidException exception,
        HttpServletRequest request
    ) {
        Map<String, String> validationErrors =
            new LinkedHashMap<>();

        for (FieldError fieldError :
            exception.getBindingResult().getFieldErrors()) {

            validationErrors.put(
                fieldError.getField(),
                fieldError.getDefaultMessage()
            );
        }

        return new ApiError(
            LocalDateTime.now(),
            HttpStatus.BAD_REQUEST.value(),
            HttpStatus.BAD_REQUEST.getReasonPhrase(),
            "Die übergebenen Daten sind ungültig.",
            request.getRequestURI(),
            validationErrors
        );
    }


    /**
     * Behandelt fachliche Fehler.
     */
    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @ApiResponse(
        responseCode = "409",
        description = "Die Anfrage verletzt eine fachliche Regel"
    )
    public ApiError handleBusinessException(
        BusinessException exception,
        HttpServletRequest request
    ) {
        return new ApiError(
            LocalDateTime.now(),
            HttpStatus.CONFLICT.value(),
            HttpStatus.CONFLICT.getReasonPhrase(),
            exception.getMessage(),
            request.getRequestURI(),
            null
        );
    }


    /**
     * Fallback für nicht explizit behandelte Fehler.
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ApiResponse(
        responseCode = "500",
        description = "Interner Serverfehler"
    )
    public ApiError handleUnexpectedException(
        Exception exception,
        HttpServletRequest request
    ) {
        return new ApiError(
            LocalDateTime.now(),
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
            "Es ist ein unerwarteter interner Fehler aufgetreten.",
            request.getRequestURI(),
            null
        );
    }
}
```

Damit sind die grundlegenden Fehler zentral definiert:
```Plain Text
400  Validierungsfehler
404  Ressource nicht gefunden
409  Fachlicher Konflikt
500  Unerwarteter Serverfehler
```
## 5. Was passiert jetzt mit dem CustomerController?

Hier liegt der große Vorteil.

Du musst nicht mehr überall solche Blöcke duplizieren:
```Java
@ApiResponses({
    @ApiResponse(
        responseCode = "400",
        description = "Ungültige Kundendaten"
    ),
    @ApiResponse(
        responseCode = "404",
        description = "Kunde wurde nicht gefunden"
    )
})
```
Die Exception-Behandlung befindet sich zentral im:
```Plain Text
GlobalExceptionHandler
```
Der Controller kann sich stärker darauf konzentrieren, was der erfolgreiche Endpoint tut.

Zum Beispiel:
```Java
@GetMapping("/{id}")
@Operation(
    summary = "Kunde über ID lesen",
    description = "Liefert einen Kunden anhand seiner eindeutigen Kunden-ID."
)
@ApiResponse(
    responseCode = "200",
    description = "Kunde wurde erfolgreich gelesen"
)
public CustomerDto findById(

    @Parameter(
        description = "Eindeutige ID des Kunden",
        example = "1",
        required = true
    )
    @PathVariable Long id

) {
    return customerService.findById(id);
}
```
Der Controller wird dadurch erheblich übersichtlicher.

## 6. Aber eine wichtige Einschränkung

Hier würde ich nicht versuchen, alles automatisch global zu dokumentieren.

Das Problem ist folgendes:
```Java
GET /api/customers
```
braucht fachlich womöglich gar kein 404.

Dagegen hat:
```Java
GET /api/customers/{id}
```
sehr wohl einen 404.
Und:

```Plain Text
POST /api/customers
```
kann wegen @Valid einen 400 erzeugen.

Eine rein globale OpenAPI-Zuweisung kann daher Responses bei Endpunkten anzeigen, bei denen sie fachlich gar nicht auftreten. Genau dieses Verhalten ist bei globalen Controller-Advice-Responses relevant.

Deshalb würde ich bei Rechnung Enterprise unterscheiden:
```Code
               REST-Anfrage
                    │
                    ▼
             CustomerController
                    │
                    ▼
              CustomerService
                    │
          ┌─────────┴─────────┐
          │                   │
       Erfolg               Fehler
          │                   │
          ▼                   ▼
    CustomerDto       Exception
                              │
                              ▼
                 GlobalExceptionHandler
                              │
               ┌──────────────┼──────────────┐
               ▼              ▼              ▼
              400            404            409
          Validation      NotFound       Business
                              │
                              ▼
                          ApiError
```
Damit ist die Aufgabenverteilung sauber

Controller

Dokumentiert Endpoint, Parameter und erfolgreichen Response:
```Java
@Operation
@Parameter
@ApiResponse(responseCode = "200", ...)
```
Exception Handler

Definiert die tatsächliche zentrale Fehlerbehandlung:
```Java
400
404
409
500
```
ApiError

Definiert ein einheitliches JSON-Fehlerformat für dein React-Frontend.

## 7. Besonders wichtig für dein Frontend

Ein fehlerhafter CustomerRequest könnte beispielsweise konzeptionell so beantwortet werden:
```Json
{
    "timestamp": "2026-10-23T16:30:00",
    "status": 400,
    "error": "Bad Request",
    "message": "Die übergebenen Daten sind ungültig.",
    "path": "/api/customers",
    "validationErrors": {
        "customerNumber": "darf nicht leer sein",
        "name": "darf nicht leer sein"
    }
}
```
Dann können wir später in deiner CustomerFormPage.tsx gezielt schreiben:
```TypeScript
setErrors(error.response.data.validationErrors);
```
und beispielsweise:
```JTSX
<TextField
    label="Kundenname"
    error={Boolean(errors.name)}
    helperText={errors.name}
/>
```
Damit greifen Backend-Validierung und MUI-Formularvalidierung direkt ineinander
```Code
```

```Code
```

```Code
```

