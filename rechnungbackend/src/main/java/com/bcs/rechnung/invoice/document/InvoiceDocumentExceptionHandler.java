package com.bcs.rechnung.invoice.document;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class InvoiceDocumentExceptionHandler {

    @ExceptionHandler(
        InvoiceAlreadyArchivedException.class
    )
    public ResponseEntity<InvoiceAlreadyArchivedResponse>
    handleInvoiceAlreadyArchived(
        InvoiceAlreadyArchivedException exception
    ) {
        InvoiceAlreadyArchivedResponse response =
            new InvoiceAlreadyArchivedResponse(
                HttpStatus.CONFLICT.value(),
                HttpStatus.CONFLICT.getReasonPhrase(),
                exception.getMessage(),
                exception.getInvoiceId(),
                exception.getInvoiceNumber(),
                exception.getArchivedAt(),
                LocalDateTime.now()
            );

        return ResponseEntity
            .status(
                HttpStatus.CONFLICT
            )
            .body(
                response
            );
    }

    public record InvoiceAlreadyArchivedResponse(

        int status,

        String error,

        String message,

        Long invoiceId,

        String invoiceNumber,

        LocalDateTime archivedAt,

        LocalDateTime timestamp

    ) {
    }
}
