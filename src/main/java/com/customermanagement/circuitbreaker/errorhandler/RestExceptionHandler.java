package com.customermanagement.circuitbreaker.errorhandler;

import com.customermanagement.circuitbreaker.exception.ServiceException;
import com.customermanagement.circuitbreaker.exception.domain.ApiError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
@Slf4j
public class RestExceptionHandler extends CustomerExceptionHandler{

    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<Object> handleInternalServerError(ServiceException e, WebRequest request){
        return handleAllExceptions(e, HttpStatus.INTERNAL_SERVER_ERROR, request,
                ApiError.builder().message(resolveMessageSource("severe.error", e.getMessage())).build());
    }


}
