package com.bcs.rechnung.exception;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Zentrale Fehlerbehandlung für die REST-API.
 *
 * <p>
 * Der Handler verarbeitet:
 * </p>
 *
 * <ul>
 *     <li>Validierungsfehler</li>
 *     <li>Nicht gefundene Ressourcen</li>
 *     <li>Nicht vorhandene REST-Endpunkte</li>
 *     <li>Fachliche Konflikte</li>
 *     <li>Ungültige Argumente</li>
 *     <li>Unerwartete technische Fehler</li>
 * </ul>
 *
 * <p>
 * Sämtliche Fehler werden in das einheitliche
 * Antwortformat {@link ApiError} umgewandelt.
 * </p>
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Behandelt den Fall, dass eine fachliche Ressource
     * nicht gefunden wurde.
     *
     * <p>Beispiele:</p>
     *
     * <ul>
     *     <li>Kunde wurde nicht gefunden</li>
     *     <li>Firma wurde nicht gefunden</li>
     *     <li>Produkt wurde nicht gefunden</li>
     *     <li>Rechnung wurde nicht gefunden</li>
     * </ul>
     *
     * @param exception ausgelöste Exception
     * @param request aktuelle HTTP-Anfrage
     * @return standardisierte Fehlerantwort
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ApiResponse(
        responseCode = "404",
        description = "Die angeforderte Ressource wurde nicht gefunden",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(
                implementation = ApiError.class
            )
        )
    )
    public ApiError handleResourceNotFound(
        ResourceNotFoundException exception,
        HttpServletRequest request
    ) {

        log.warn(
            "Ressource nicht gefunden bei {} {}: {}",
            request.getMethod(),
            request.getRequestURI(),
            exception.getMessage()
        );

        return createApiError(
            HttpStatus.NOT_FOUND,
            exception.getMessage(),
            request,
            null
        );
    }


    /**
     * Behandelt den Aufruf eines nicht vorhandenen
     * REST-Endpunkts oder einer nicht vorhandenen
     * statischen Ressource.
     *
     * <p>
     * Beispiel:
     * </p>
     *
     * <pre>
     * GET /api/api/customers
     * </pre>
     *
     * <p>
     * Dieser Fall wird als HTTP 404 NOT FOUND behandelt
     * und nicht als interner Serverfehler.
     * </p>
     *
     * @param exception ausgelöste Exception
     * @param request aktuelle HTTP-Anfrage
     * @return standardisierte Fehlerantwort
     */
    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ApiResponse(
        responseCode = "404",
        description = "Der angeforderte Endpunkt wurde nicht gefunden",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(
                implementation = ApiError.class
            )
        )
    )
    public ApiError handleNoResourceFound(
        NoResourceFoundException exception,
        HttpServletRequest request
    ) {

        log.warn(
            "Endpunkt nicht gefunden bei {} {}",
            request.getMethod(),
            request.getRequestURI()
        );

        return createApiError(
            HttpStatus.NOT_FOUND,
            "Der angeforderte Endpunkt wurde nicht gefunden.",
            request,
            null
        );
    }


    /**
     * Behandelt Validierungsfehler aus {@code @Valid}.
     *
     * <p>
     * Die einzelnen Validierungsfehler werden anhand
     * ihrer Feldnamen gesammelt und im Feld
     * {@code validationErrors} des ApiError zurückgegeben.
     * </p>
     *
     * @param exception ausgelöste Validierungsexception
     * @param request aktuelle HTTP-Anfrage
     * @return standardisierte Fehlerantwort mit Feldfehlern
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ApiResponse(
        responseCode = "400",
        description = "Die übergebenen Daten sind ungültig",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(
                implementation = ApiError.class
            )
        )
    )
    public ApiError handleValidation(
        MethodArgumentNotValidException exception,
        HttpServletRequest request
    ) {

        Map<String, String> validationErrors =
            new LinkedHashMap<>();

        for (
            FieldError fieldError :
            exception
                .getBindingResult()
                .getFieldErrors()
        ) {

            /*
             * Falls ein Feld mehrere Validierungsfehler
             * besitzt, wird die zuerst ermittelte
             * Fehlermeldung beibehalten.
             */
            validationErrors.putIfAbsent(
                fieldError.getField(),
                fieldError.getDefaultMessage()
            );
        }

        log.warn(
            "Validierungsfehler bei {} {}: {}",
            request.getMethod(),
            request.getRequestURI(),
            validationErrors
        );

        return createApiError(
            HttpStatus.BAD_REQUEST,
            "Die übergebenen Daten sind ungültig.",
            request,
            validationErrors
        );
    }


    /**
     * Behandelt fachliche Fehler und Verletzungen
     * von Geschäftsregeln.
     *
     * <p>Beispiele:</p>
     *
     * <ul>
     *     <li>Kundennummer existiert bereits</li>
     *     <li>Datensatz darf nicht gelöscht werden</li>
     *     <li>Rechnung wurde bereits archiviert</li>
     *     <li>Statuswechsel ist fachlich nicht zulässig</li>
     * </ul>
     *
     * @param exception ausgelöste fachliche Exception
     * @param request aktuelle HTTP-Anfrage
     * @return standardisierte Fehlerantwort
     */
    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @ApiResponse(
        responseCode = "409",
        description = "Die Anfrage verletzt eine fachliche Regel",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(
                implementation = ApiError.class
            )
        )
    )
    public ApiError handleBusinessException(
        BusinessException exception,
        HttpServletRequest request
    ) {

        log.warn(
            "Fachlicher Konflikt bei {} {}: {}",
            request.getMethod(),
            request.getRequestURI(),
            exception.getMessage()
        );

        return createApiError(
            HttpStatus.CONFLICT,
            exception.getMessage(),
            request,
            null
        );
    }


    /**
     * Behandelt ungültige Methodenargumente.
     *
     * <p>
     * Dieser Handler kann insbesondere für bestehenden
     * Code verwendet werden, der noch eine
     * {@link IllegalArgumentException} auslöst.
     * </p>
     *
     * @param exception ausgelöste Exception
     * @param request aktuelle HTTP-Anfrage
     * @return standardisierte Fehlerantwort
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ApiResponse(
        responseCode = "400",
        description = "Die Anfrage enthält ein ungültiges Argument",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(
                implementation = ApiError.class
            )
        )
    )
    public ApiError handleIllegalArgument(
        IllegalArgumentException exception,
        HttpServletRequest request
    ) {

        log.warn(
            "Ungültiges Argument bei {} {}: {}",
            request.getMethod(),
            request.getRequestURI(),
            exception.getMessage()
        );

        return createApiError(
            HttpStatus.BAD_REQUEST,
            exception.getMessage(),
            request,
            null
        );
    }


    /**
     * Fallback für alle nicht explizit behandelten Fehler.
     *
     * <p>
     * Die vollständige Exception wird im Backend protokolliert.
     * Interne technische Details werden jedoch nicht an das
     * Frontend zurückgegeben.
     * </p>
     *
     * @param exception unerwartete Exception
     * @param request aktuelle HTTP-Anfrage
     * @return standardisierte Fehlerantwort
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ApiResponse(
        responseCode = "500",
        description = "Interner Serverfehler",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(
                implementation = ApiError.class
            )
        )
    )
    public ApiError handleUnexpectedException(
        Exception exception,
        HttpServletRequest request
    ) {

        log.error(
            "Unerwarteter Fehler bei {} {}",
            request.getMethod(),
            request.getRequestURI(),
            exception
        );

        return createApiError(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "Es ist ein unerwarteter interner Fehler aufgetreten.",
            request,
            null
        );
    }


    /**
     * Erzeugt eine einheitliche ApiError-Antwort.
     *
     * <p>
     * Diese Methode verhindert, dass der Aufbau der
     * Fehlerantwort in jedem ExceptionHandler wiederholt
     * werden muss.
     * </p>
     *
     * @param status HTTP-Status
     * @param message Fehlermeldung
     * @param request aktuelle HTTP-Anfrage
     * @param validationErrors optionale Feldfehler
     * @return erzeugter ApiError
     */
    private ApiError createApiError(
        HttpStatus status,
        String message,
        HttpServletRequest request,
        Map<String, String> validationErrors
    ) {

        return new ApiError(
            LocalDateTime.now(),
            status.value(),
            status.getReasonPhrase(),
            message,
            request.getRequestURI(),
            validationErrors
        );
    }
}
