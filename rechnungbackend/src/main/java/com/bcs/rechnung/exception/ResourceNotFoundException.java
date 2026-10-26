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
