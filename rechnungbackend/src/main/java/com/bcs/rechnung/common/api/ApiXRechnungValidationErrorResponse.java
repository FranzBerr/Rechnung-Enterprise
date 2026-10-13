package com.bcs.rechnung.common.api;

import java.time.LocalDateTime;
import java.util.List;

public record ApiXRechnungValidationErrorResponse(

    int status,

    String error,

    String message,

    String invoiceNumber,

    List<String> errors,

    List<String> warnings,

    String validationReport,

    LocalDateTime timestamp

) {
}
