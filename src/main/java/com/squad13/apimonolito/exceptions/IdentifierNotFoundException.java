package com.squad13.apimonolito.exceptions;

public class IdentifierNotFoundException extends RuntimeException {
    public IdentifierNotFoundException(String message) {
        super(message);
    }
}
