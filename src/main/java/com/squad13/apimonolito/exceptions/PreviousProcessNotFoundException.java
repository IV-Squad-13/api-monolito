package com.squad13.apimonolito.exceptions;

public class PreviousProcessNotFoundException extends RuntimeException {
    public PreviousProcessNotFoundException(String message) {
        super(message);
    }
}
