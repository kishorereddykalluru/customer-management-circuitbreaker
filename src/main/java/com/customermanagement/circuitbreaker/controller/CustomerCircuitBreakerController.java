package com.customermanagement.circuitbreaker.controller;

import com.customermanagement.circuitbreaker.domain.CustomerDetails;
import com.customermanagement.circuitbreaker.exception.NotFoundException;
import com.customermanagement.circuitbreaker.exception.ServiceException;
import com.customermanagement.circuitbreaker.service.CustomerService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;

@RestController
@Slf4j
public class CustomerCircuitBreakerController implements ErrorController {

    private static final String ERROR_URL = "${server.error.path:${error.path:/error}}";

    @Autowired
    private CustomerService customerService;

    @GetMapping("/customer/getAll")
    public List<CustomerDetails> getCustomerDetails(){
       return customerService.getAllCustomers();
    }

    @GetMapping("/customer/{id}")
    public CustomerDetails getCustomerById(@PathVariable("id") Long id){
        return customerService.getCustomerById(id);
    }

    @GetMapping(value = ERROR_URL)
    //@Operation(description = "error", hidden = true)
    public void handleError(HttpServletRequest request){
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        if(Objects.nonNull(status)){
            Object objErrorMessage = request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
            String errorMessage = StringUtils.defaultString((String) objErrorMessage);

            log.error("Status: {}. Error: {}", status, errorMessage);

            int statusCode = Integer.parseInt(status.toString());

            if(statusCode == HttpStatus.NOT_FOUND.value()){
                throw new NotFoundException(errorMessage);
            } else if(statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()){
                throw new ServiceException(errorMessage);
            } else {
                throw new ServiceException("Status: " + status + " Error: " + StringUtils.defaultString(errorMessage));
            }
        }
    }

    @Override
    public String getErrorPath() {
        return ERROR_URL;
    }
}
