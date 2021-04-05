package com.customermanagement.circuitbreaker.controller;

import com.customermanagement.circuitbreaker.domain.CustomerDetails;
import com.customermanagement.circuitbreaker.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CustomerCircuitBreakerController {

    @Autowired
    private CustomerService customerService;

    @GetMapping("/customer/getAll")
    public List<CustomerDetails> getCustomerDetails(){
       return customerService.getAllCustomers();
    }
}
