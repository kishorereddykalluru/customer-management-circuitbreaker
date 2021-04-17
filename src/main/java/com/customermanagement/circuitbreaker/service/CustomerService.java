package com.customermanagement.circuitbreaker.service;

import com.customermanagement.circuitbreaker.domain.CustomerDetails;
import com.customermanagement.circuitbreaker.exception.CustomerInternalException;
import com.customermanagement.circuitbreaker.exception.InternalServiceException;
import com.customermanagement.circuitbreaker.interceptors.RequestInterceptor;
import com.customermanagement.circuitbreaker.service.client.CustomerClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CustomerService {

    @Autowired
    private CustomerClient customerClient;

    public List<CustomerDetails> getAllCustomers(){
        List<Throwable> serverErrors = new ArrayList<>();
        List<Throwable> clientErrors = new ArrayList<>();

       List<CustomerDetails> collect = customerClient.getAllCustomers().exceptionally(
                e -> {
                    log.error("Customer call failed due to: {}", ExceptionUtils.getRootCauseMessage(e));
                    Throwable ex = ExceptionUtils.getRootCause(e);
                    if (ex instanceof InternalServiceException || ex instanceof IOException) {
                        serverErrors.add(ex);
                    } else {
                        clientErrors.add(ex);
                    }
                    return null;
                }).join();

       if(!CollectionUtils.isEmpty(serverErrors)){
           String errorMessage = serverErrors.stream().map(Throwable::getMessage).collect(Collectors.joining(" & "));
           log.error("Server errors: {}", errorMessage);
           if(CollectionUtils.isEmpty(clientErrors) && CollectionUtils.isEmpty(collect)){
               throw new CustomerInternalException(errorMessage);
           } else if(!CollectionUtils.isEmpty(clientErrors)){
               log.error("Client error: {}", clientErrors.stream().map(Throwable::getMessage).collect(Collectors.joining("&")));
           }
       }

       log.info("{} requests | {} responses | {} client errors | {} server errors",collect.size(), collect.size(), clientErrors.size(), serverErrors.size());
       return collect;
    }

    public CustomerDetails getCustomerById(Long id){
        List<Throwable> serverErrors = new ArrayList<>();
        List<Throwable> clientErrors = new ArrayList<>();

        CustomerDetails collect = customerClient.getCustomerById(MDC.get(RequestInterceptor.HEADER_X_CORRELATION_ID), id).exceptionally(
                e -> {
                    log.error("Customer call failed due to: {}", ExceptionUtils.getRootCauseMessage(e));
                    Throwable ex = ExceptionUtils.getRootCause(e);
                    if (ex instanceof InternalServiceException || ex instanceof IOException) {
                        serverErrors.add(ex);
                    } else {
                        clientErrors.add(ex);
                    }
                    return null;
                }).join();

        if(!CollectionUtils.isEmpty(serverErrors)){
            String errorMessage = serverErrors.stream().map(Throwable::getMessage).collect(Collectors.joining(" & "));
            log.error("Server errors: {}", errorMessage);
            if(CollectionUtils.isEmpty(clientErrors) && Objects.isNull(collect)){
                throw new CustomerInternalException(errorMessage);
            } else if(!CollectionUtils.isEmpty(clientErrors)){
                log.error("Client error: {}", clientErrors.stream().map(Throwable::getMessage).collect(Collectors.joining("&")));
            }
        }

        log.info("{} client errors | {} server errors", clientErrors.size(), serverErrors.size());
        return collect;
    }
}
