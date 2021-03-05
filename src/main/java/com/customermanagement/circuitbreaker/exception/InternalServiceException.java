package com.customermanagement.circuitbreaker.exception;

public class InternalServiceException extends RuntimeException{
    private static final long serialVersionUID = -4888839984543345706L;

    public InternalServiceException(String message){
        super(message);
    }
}
