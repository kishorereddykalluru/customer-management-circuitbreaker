package com.customermanagement.circuitbreaker.service.feign;

import com.customermanagement.circuitbreaker.config.feign.ItemFeignConfiguration;
import feign.Response;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = ItemFeign.SERVICE, url = "${customer-feign.base.url}", configuration = ItemFeignConfiguration.class)
public interface ItemFeign {

    String SERVICE = "orderService";

    @CircuitBreaker(name = SERVICE)
    @GetMapping("${customer-feign.item.url}")
    String getItem();

    @GetMapping("${customer-feign.health.url}")
    Response healthCheck();
}
