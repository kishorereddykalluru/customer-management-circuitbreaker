package com.customermanagement.circuitbreaker.exception;

public class ServiceException extends RuntimeException{
    private static final long serialVersionUID = 1505286068841058538L;

    public ServiceException(String message){
        super(message);
    }
}
