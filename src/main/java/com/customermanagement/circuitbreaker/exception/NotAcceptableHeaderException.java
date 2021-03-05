package com.customermanagement.circuitbreaker.exception;

public class NotAcceptableHeaderException extends RuntimeException {
    private static final long serialVersionUID = -3025780642835111564L;

    public NotAcceptableHeaderException(String message) {
        super(message);
    }
}
