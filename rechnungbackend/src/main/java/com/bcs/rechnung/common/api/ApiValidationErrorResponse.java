package com.bcs.rechnung.common.api;

import java.time.LocalDateTime;
import java.util.Map;

public record ApiValidationErrorResponse(

    int status,

    String error,

    String message,

    Map<String, String> fieldErrors,

    LocalDateTime timestamp

) {
}
