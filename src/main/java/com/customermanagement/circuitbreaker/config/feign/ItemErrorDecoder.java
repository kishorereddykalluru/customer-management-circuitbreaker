package com.customermanagement.circuitbreaker.config.feign;

import com.customermanagement.circuitbreaker.exception.*;
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
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
@Slf4j
public class ItemErrorDecoder implements ErrorDecoder{

    private final int[] retryStatusCodes;

    private final ErrorDecoder defaultErrorDecoder = new Default();
    private final ObjectMapper objectMapper;

    public ItemErrorDecoder(final int[] retryStatusCodes, final ObjectMapper objectMapper) {
        this.retryStatusCodes = retryStatusCodes;
        this.objectMapper = objectMapper;
    }

    @Override
    public Exception decode(String methodKey, Response response) {
        ApiErrors apiErrors = deserializeClientResponse(response);

        Exception defaultException = defaultErrorDecoder.decode(methodKey, response);
        if(defaultException instanceof RetryableException){
            return defaultException;
        }

        if(ArrayUtils.contains(retryStatusCodes, response.status())){
            log.trace("Retrying...");
            return new RetryableException(response.status(), "Failed Customer call with " + response.status() + ". Retrying...", response.request().httpMethod(), defaultException, null, response.request());
        } else {
            if(response.status() == HttpStatus.NOT_FOUND.value()){
                return new NotFoundException(processErrorMessage(apiErrors));
            } else if(response.status() >= 400 && response.status() <= 599){
                return new ServiceException(processErrorMessage(apiErrors));
            }
        }
        return defaultException;
    }



    protected ApiErrors deserializeClientResponse(Response response) {
        ApiErrors apiErrors = new ApiErrors(new ArrayList<>());

        try {
            apiErrors = objectMapper.readValue(response.body().asInputStream(), ApiErrors.class);
            log.trace("Customer error response body: {}",apiErrors.toString());
        } catch (IOException e) {
            log.debug("Customer response body exception. {}", ExceptionUtils.getRootCauseMessage(e));
            apiErrors.setApiErrors(List.of(new ApiError(500, "Failed to call customer service due to "+response.status())));
        }
        return apiErrors;
    }

    protected String processErrorMessage(ApiErrors errors) {
        if(CollectionUtils.isEmpty(errors.getApiErrors())){
            return "";
        } else {
            return errors.getApiErrors().stream().map(ApiError::getMessage).collect(Collectors.joining("&"));
        }
    }
}
