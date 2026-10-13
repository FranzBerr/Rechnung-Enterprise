package com.bcs.rechnung.common.api;

import com.bcs.rechnung.exception.ResourceNotFoundException;
import com.bcs.rechnung.invoice.document.InvalidXRechnungException;
import com.bcs.rechnung.invoice.document.InvoiceAlreadyArchivedException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(
        ResourceNotFoundException.class
    )
    public ResponseEntity<ApiErrorResponse>
    handleResourceNotFound(
        ResourceNotFoundException exception
    ) {

        return ResponseEntity
            .status(
                HttpStatus.NOT_FOUND
            )
            .body(
                new ApiErrorResponse(
                    HttpStatus.NOT_FOUND.value(),
                    HttpStatus.NOT_FOUND.getReasonPhrase(),
                    exception.getMessage(),
                    LocalDateTime.now()
                )
            );
    }

    @ExceptionHandler(
        InvoiceAlreadyArchivedException.class
    )
    public ResponseEntity<ApiErrorResponse>
    handleInvoiceAlreadyArchived(
        InvoiceAlreadyArchivedException exception
    ) {

        return ResponseEntity
            .status(
                HttpStatus.CONFLICT
            )
            .body(
                new ApiErrorResponse(
                    HttpStatus.CONFLICT.value(),
                    HttpStatus.CONFLICT.getReasonPhrase(),
                    exception.getMessage(),
                    LocalDateTime.now()
                )
            );
    }

    @ExceptionHandler(
        InvalidXRechnungException.class
    )
    public ResponseEntity<ApiXRechnungValidationErrorResponse>
    handleInvalidXRechnung(
        InvalidXRechnungException exception
    ) {

        String validationReport = null;

        if (exception.getReportFile() != null) {
            validationReport =
                exception.getReportFile()
                    .toString();
        }

        return ResponseEntity
            .status(
                HttpStatus.UNPROCESSABLE_ENTITY
            )
            .body(
                new ApiXRechnungValidationErrorResponse(
                    HttpStatus.UNPROCESSABLE_ENTITY.value(),
                    HttpStatus.UNPROCESSABLE_ENTITY.getReasonPhrase(),
                    "Die XRechnung ist nicht gültig.",
                    exception.getInvoiceNumber(),
                    exception.getErrors(),
                    exception.getWarnings(),
                    validationReport,
                    LocalDateTime.now()
                )
            );
    }

    @ExceptionHandler(
        IllegalArgumentException.class
    )
    public ResponseEntity<ApiErrorResponse>
    handleIllegalArgument(
        IllegalArgumentException exception
    ) {

        return ResponseEntity
            .status(
                HttpStatus.BAD_REQUEST
            )
            .body(
                new ApiErrorResponse(
                    HttpStatus.BAD_REQUEST.value(),
                    HttpStatus.BAD_REQUEST.getReasonPhrase(),
                    exception.getMessage(),
                    LocalDateTime.now()
                )
            );
    }

    @ExceptionHandler(
        ConstraintViolationException.class
    )
    public ResponseEntity<ApiErrorResponse>
    handleConstraintViolation(
        ConstraintViolationException exception
    ) {

        return ResponseEntity
            .status(
                HttpStatus.BAD_REQUEST
            )
            .body(
                new ApiErrorResponse(
                    HttpStatus.BAD_REQUEST.value(),
                    HttpStatus.BAD_REQUEST.getReasonPhrase(),
                    exception.getMessage(),
                    LocalDateTime.now()
                )
            );
    }

    @ExceptionHandler(
        MethodArgumentNotValidException.class
    )
    public ResponseEntity<ApiValidationErrorResponse>
    handleMethodArgumentNotValid(
        MethodArgumentNotValidException exception
    ) {

        Map<String, String> fieldErrors =
            new LinkedHashMap<>();

        for (
            FieldError fieldError :
            exception
                .getBindingResult()
                .getFieldErrors()
        ) {

            fieldErrors.put(
                fieldError.getField(),
                fieldError.getDefaultMessage()
            );
        }

        return ResponseEntity
            .status(
                HttpStatus.BAD_REQUEST
            )
            .body(
                new ApiValidationErrorResponse(
                    HttpStatus.BAD_REQUEST.value(),
                    HttpStatus.BAD_REQUEST.getReasonPhrase(),
                    "Validierungsfehler",
                    fieldErrors,
                    LocalDateTime.now()
                )
            );
    }

    @ExceptionHandler(
        IllegalStateException.class
    )
    public ResponseEntity<ApiErrorResponse>
    handleIllegalState(
        IllegalStateException exception
    ) {

        return ResponseEntity
            .status(
                HttpStatus.CONFLICT
            )
            .body(
                new ApiErrorResponse(
                    HttpStatus.CONFLICT.value(),
                    HttpStatus.CONFLICT.getReasonPhrase(),
                    exception.getMessage(),
                    LocalDateTime.now()
                )
            );
    }

    @ExceptionHandler(
        Exception.class
    )
    public ResponseEntity<ApiErrorResponse>
    handleUnexpectedException(
        Exception exception
    ) {

        return ResponseEntity
            .status(
                HttpStatus.INTERNAL_SERVER_ERROR
            )
            .body(
                new ApiErrorResponse(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                    exception.getMessage(),
                    LocalDateTime.now()
                )
            );
    }
}
