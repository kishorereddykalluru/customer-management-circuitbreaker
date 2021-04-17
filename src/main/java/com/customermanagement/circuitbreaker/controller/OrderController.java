package com.customermanagement.circuitbreaker.controller;

import com.customermanagement.circuitbreaker.service.client.ItemClient;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class OrderController {

    @Autowired
    private ItemClient itemClient;

    //private static final String ORDER_SERVICE = "orderService";

    /*@Autowired
    private RestTemplate restTemplate;

    @Bean
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }*/

    /**
     * Basic Rest Template Circuit breaker
     */
    /*@GetMapping("/order")
    @CircuitBreaker(name=ORDER_SERVICE, fallbackMethod = "orderFallback")
    public ResponseEntity<String> createOrder(){
        String response = restTemplate.getForObject("http://localhost:9090/item", String.class);
        return new ResponseEntity<String>(response, HttpStatus.OK);
    }
    public ResponseEntity<String> orderFallback(Exception e){
        return new ResponseEntity<String>("Item service is down", HttpStatus.OK);

    }*/

    @GetMapping("/order")
    public String createOrder(){
        return itemClient.getItem();
    }

}
