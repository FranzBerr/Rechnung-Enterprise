package com.bcs.rechnung.common.api;

import java.time.LocalDateTime;

public record ApiErrorResponse(

    int status,

    String error,

    String message,

    LocalDateTime timestamp

) {
}
