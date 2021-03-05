package com.customermanagement.circuitbreaker.exception;

public class BadRequestException extends RuntimeException {

    private static final long serialVersionUID = -6023666318213437245L;

    public BadRequestException(String message) {
        super(message);
    }
}
