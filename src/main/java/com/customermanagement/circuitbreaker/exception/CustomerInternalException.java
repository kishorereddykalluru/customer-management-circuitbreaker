package com.customermanagement.circuitbreaker.exception;

public class CustomerInternalException extends InternalServiceException {

    private static final String DEFAULT_MESSAGE = "Caught service exception in Customer call";
    private static final long serialVersionUID = -2707052566726920494L;

    /**
     * Caught service exception in Customer call
     * @param message
     */
    public CustomerInternalException(String message) {
        super(DEFAULT_MESSAGE + ": "+message);
    }
}
