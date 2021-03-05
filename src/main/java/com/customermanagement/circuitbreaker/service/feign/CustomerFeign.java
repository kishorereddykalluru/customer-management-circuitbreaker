package com.customermanagement.circuitbreaker.service.feign;

import com.customermanagement.circuitbreaker.config.feign.CustomerFeignConfiguration;
import com.customermanagement.circuitbreaker.domain.CustomerDetails;
import feign.Response;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = CustomerFeign.SERVICE, url = "${customer-feign.base.url}", configuration = CustomerFeignConfiguration.class)
public interface CustomerFeign {

    String SERVICE = "customer-management";

    @CircuitBreaker(name = SERVICE)
    @GetMapping(value = "${customer-feign.getAllCustomers.url}")
    List<CustomerDetails> getAllCustomers();

    @GetMapping("${customer-feign.health.url}")
    Response healthCheck();

}
