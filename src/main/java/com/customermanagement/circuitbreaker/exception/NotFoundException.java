package com.customermanagement.circuitbreaker.exception;

public class NotFoundException extends RuntimeException {
    private static final long serialVersionUID = 6250795416174627506L;

    public NotFoundException(String message) {
        super(message);
    }
}
