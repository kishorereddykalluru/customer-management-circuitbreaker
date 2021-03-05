package com.customermanagement.circuitbreaker.service.health.impl;

import com.customermanagement.circuitbreaker.service.health.HealthCheckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Health.Builder;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.health.Status;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Component
public class UpStreamServicesHealth implements HealthIndicator {

    @Autowired
    private HealthCheckService healthcheck;

    @Override
    public Health health() {
        List<CompletableFuture<Map<String, Boolean>>> resultList = new ArrayList<>();
        resultList.add(healthcheck.customerHealthCheck());

        Map<String, Boolean> result = resultList.stream().map(CompletableFuture::join).flatMap(map -> map.entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        Builder builder = Health.up();
        result.forEach((key, value) -> builder.withDetail(key, Boolean.TRUE.equals(value) ? Status.UP.getCode() : Status.DOWN.getCode()));
        return builder.build();
    }
}
