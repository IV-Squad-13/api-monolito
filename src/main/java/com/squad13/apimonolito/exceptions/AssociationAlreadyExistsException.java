package com.squad13.apimonolito.exceptions;

public class AssociationAlreadyExistsException extends RuntimeException {
    public AssociationAlreadyExistsException(String message) {
        super(message);
    }
}
