package com.customermanagement.circuitbreaker.service.client;

import com.customermanagement.circuitbreaker.domain.CustomerDetails;
import com.customermanagement.circuitbreaker.exception.CustomerInternalException;
import com.customermanagement.circuitbreaker.interceptors.RequestInterceptor;
import com.customermanagement.circuitbreaker.service.feign.CustomerFeign;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.IllegalStateTransitionException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class CustomerClient {

    @Value("${customer-feign.getAllCustomers.version:application/json}")
    private String acceptHeader;

    @Autowired
    private CustomerFeign customerFeign;

    public CompletableFuture<List<CustomerDetails>> getAllCustomers(){

        List<CustomerDetails> customerDetails = null;
        try{
            Map<String, String> clientHeaders = new HashMap<>();
            clientHeaders.put(HttpHeaders.ACCEPT, acceptHeader);
            log.debug("getAllCustomers");
            customerDetails = customerFeign.getAllCustomers();
        } catch (CallNotPermittedException | IllegalStateTransitionException e){
            log.error("Customer Service call failed with: {}", ExceptionUtils.getRootCauseMessage(e));
            throw new CustomerInternalException(ExceptionUtils.getRootCauseMessage(e));
        }
        return CompletableFuture.completedFuture(customerDetails);
    }

    public CompletableFuture<CustomerDetails> getCustomerById(String xCorrelationId, Long id){

        CustomerDetails customerDetail = null;
        try{
            Map<String, String> clientHeaders = new HashMap<>();
            clientHeaders.put(HttpHeaders.ACCEPT, acceptHeader);
            clientHeaders.put(RequestInterceptor.HEADER_X_CORRELATION_ID, xCorrelationId);
            log.info("get customer by id");
            customerDetail = customerFeign.findByCustomerId(clientHeaders, id);
        } catch (CallNotPermittedException | IllegalStateTransitionException e){
            log.error("Customer Service call failed with: {}", ExceptionUtils.getRootCauseMessage(e));
            throw new CustomerInternalException(ExceptionUtils.getRootCauseMessage(e));
        }
        return CompletableFuture.completedFuture(customerDetail);
    }
}
