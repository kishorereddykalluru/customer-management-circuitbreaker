package com.customermanagement.circuitbreaker.config.feign;

import com.customermanagement.circuitbreaker.exception.BadRequestException;
import com.customermanagement.circuitbreaker.exception.CustomerInternalException;
import com.customermanagement.circuitbreaker.exception.NotAcceptableHeaderException;
import com.customermanagement.circuitbreaker.exception.NotFoundException;
import com.customermanagement.circuitbreaker.exception.domain.ApiError;
import com.customermanagement.circuitbreaker.exception.domain.ApiErrors;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.RetryableException;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class CustomerErrorDecoder implements ErrorDecoder{

    private final int[] retryStatusCodes;

    private final ErrorDecoder defaultErrorDecoder = new Default();
    private final ObjectMapper objectMapper;

    public CustomerErrorDecoder(final int[] retryStatusCodes, final ObjectMapper objectMapper) {
        this.retryStatusCodes = retryStatusCodes;
        this.objectMapper = objectMapper;
    }

    @Override
    public Exception decode(String methodKey, Response response) {
        ApiErrors customerErrors = deserializeClientResponse(response);
        Exception defaultException = defaultErrorDecoder.decode(methodKey, response);
        if(defaultException instanceof RetryableException){
            return defaultException;
        }

        if(ArrayUtils.contains(retryStatusCodes, response.status())){
            log.trace("Retrying...");
            return new RetryableException(response.status(), "Failed Customer call with " + response.status() + ". Retrying...", response.request().httpMethod(), defaultException, null, response.request());
        } else {
            if(response.status() >= 400 && response.status() <= 599){
                if(response.status() == HttpStatus.BAD_REQUEST.value()){
                    log.debug("Bad Request, {}", response.status());
                    return new BadRequestException(processErrorMessage(customerErrors.getApiErrors()));
                } else if(response.status() == HttpStatus.NOT_ACCEPTABLE.value()){
                    log.debug("Invalid Accept header, {}", response.status());
                    return new NotAcceptableHeaderException("Not a matched accept header for Customer service");
                } else if (response.status() == HttpStatus.NOT_FOUND.value()){
                    log.debug("Not found, {}",response.status());
                    return new NotFoundException(processErrorMessage(customerErrors.getApiErrors()));
                } else if(response.status() >= HttpStatus.INTERNAL_SERVER_ERROR.value()){
                    log.debug("Internal error, {}",response.status());
                    return new CustomerInternalException(processErrorMessage(customerErrors.getApiErrors()));
                }
            }
        }
        return defaultException;
    }



    protected ApiErrors deserializeClientResponse(Response response) {
        ApiErrors apiError = new ApiErrors(new ArrayList<>());

        try {
            apiError = objectMapper.readValue(response.body().asInputStream(), ApiErrors.class);
            log.trace("Customer error response body: {}",apiError.toString());
        } catch (IOException e) {
            log.debug("Customer response body exception. {}", ExceptionUtils.getRootCauseMessage(e));
            apiError.setApiErrors(List.of(ApiError.builder().message("Failed to call customer service due to "+response.status()).code(500).build()));
        }
        return apiError;
    }

    protected String processErrorMessage(List<ApiError> errors) {
        return errors.stream().map(ApiError::getMessage).collect(Collectors.joining("&"));
    }
}
