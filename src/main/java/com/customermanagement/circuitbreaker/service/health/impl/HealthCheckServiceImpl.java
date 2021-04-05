package com.customermanagement.circuitbreaker.service.health.impl;

import com.customermanagement.circuitbreaker.service.feign.CustomerFeign;
import com.customermanagement.circuitbreaker.service.health.HealthCheckService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class HealthCheckServiceImpl implements HealthCheckService {

    @Autowired
    private CustomerFeign customerFeign;

    /**
     * check health of customer service
     * @return
     */

    @Override
    public CompletableFuture<Map<String, Boolean>> customerHealthCheck() {
        Map<String, Boolean> result = new HashMap<>();
        Boolean partTypeHealthResult = false;
        try{
            partTypeHealthResult = isHealth(customerFeign.healthCheck().status());
        }catch (Exception e){
            log.error("customer health call failed: {}", ExceptionUtils.getRootCauseMessage(e));
        }
        result.put("Customer", partTypeHealthResult);
        return CompletableFuture.completedFuture(result);
    }

    private Boolean isHealth(int status) {
        return status == HttpStatus.OK.value();
    }
}
