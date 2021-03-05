package com.customermanagement.circuitbreaker.service.health;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface HealthCheckService {

    CompletableFuture<Map<String, Boolean>> customerHealthCheck();
}
