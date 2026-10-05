package com.bcs.rechnung.exception;

public class ResourceNotFoundException
    extends RuntimeException {

    public ResourceNotFoundException(
        String message
    ) {
        super(message);
    }
}

