package com.customermanagement.circuitbreaker.config.feign;

import com.customermanagement.circuitbreaker.service.feign.ItemFeign;
import com.customermanagement.circuitbreaker.service.feign.ItemFeignFallback;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.ExceptionPropagationPolicy;
import feign.Feign;
import feign.FeignException;
import feign.Retryer;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.feign.FeignDecorator;
import io.github.resilience4j.feign.FeignDecorators;
import io.github.resilience4j.feign.Resilience4jFeign;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;

import java.util.concurrent.TimeUnit;

@Slf4j
public class ItemFeignConfiguration {

    @Value("${customer-feign.retry.status.codes:500,502,503}")
    private int[] retryStatusCodes;

    @Value("${customer-feign.retry.period.ms:2000}")
    private long periodMillis;

    @Value("${customer-feign.retry.maxPeriod.sec:60}")
    private long maxPeriodSec;

    @Value("${customer-feign.retry.maxAttempts:5}")
    private int maxAttempts;

    @Bean
    @Scope("prototype")
    public Feign.Builder itemFeignBuilder(CircuitBreakerRegistry circuitBreakerRegistry, ObjectMapper objectMapper, Retryer itemFeignRetryer){
        return Resilience4jFeign.builder(itemCircuitBreakerConfig(circuitBreakerRegistry))
                .errorDecoder(new ItemErrorDecoder(retryStatusCodes, objectMapper))
                .exceptionPropagationPolicy(ExceptionPropagationPolicy.UNWRAP)
                .retryer(itemFeignRetryer);
    }

    @Bean
    public Retryer itemFeignRetryer() {
        return new Retryer.Default(periodMillis, TimeUnit.SECONDS.toMillis(maxPeriodSec), maxAttempts);
    }

    private FeignDecorator itemCircuitBreakerConfig(CircuitBreakerRegistry circuitBreakerRegistry) {
        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker(ItemFeign.SERVICE);
        circuitBreaker.getEventPublisher()
                .onStateTransition(event -> log.info("Circuit breaker state: " + event.getStateTransition().toString()))
                .onEvent(event -> log.trace("Circuit breaker event: " + event.getEventType().toString()));

        return FeignDecorators.builder()
                .withCircuitBreaker(circuitBreaker)
                .withFallbackFactory(ItemFeignFallback::new, FeignException.class)
                .build();

    }
}
