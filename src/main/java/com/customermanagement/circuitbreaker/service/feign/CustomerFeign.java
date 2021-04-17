package com.customermanagement.circuitbreaker.service.feign;

import com.customermanagement.circuitbreaker.config.feign.CustomerFeignConfiguration;
import com.customermanagement.circuitbreaker.domain.CustomerDetails;
import feign.Response;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;
import java.util.Map;

@FeignClient(name = CustomerFeign.SERVICE, url = "${customer-feign.base.url}", configuration = CustomerFeignConfiguration.class)
public interface CustomerFeign {

    String SERVICE = "customer-management";

    @CircuitBreaker(name = SERVICE)
    @GetMapping(value = "${customer-feign.getAllCustomers.url}")
    List<CustomerDetails> getAllCustomers();

    @CircuitBreaker(name = SERVICE)
    @GetMapping(value = "${customer-feign.getCustomerById.url}")
    CustomerDetails findByCustomerId(@RequestHeader Map<String, String> headers,
                                     @PathVariable("id") Long id);

    @GetMapping("${customer-feign.health.url}")
    Response healthCheck();

}
