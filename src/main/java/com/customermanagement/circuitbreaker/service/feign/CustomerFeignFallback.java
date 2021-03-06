package com.customermanagement.circuitbreaker.service.feign;

import com.customermanagement.circuitbreaker.domain.CustomerDetails;
import com.customermanagement.circuitbreaker.exception.CustomerInternalException;
import feign.Request;
import feign.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.boot.actuate.health.Status;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class CustomerFeignFallback implements CustomerFeign{

    private final Exception exception;
    private final CacheManager cacheManager;

    public CustomerFeignFallback(Exception exception, CacheManager cacheManager) {
        this.exception = exception;
        this.cacheManager = cacheManager;
    }

    @Override
    public List<CustomerDetails> getAllCustomers() {
        log.debug("::::: In Customer Fall back service");
        log.error(ExceptionUtils.getRootCauseMessage(exception));
        throw new CustomerInternalException(ExceptionUtils.getRootCauseMessage(exception));
    }

    @Override
    public CustomerDetails findByCustomerId(Map<String, String> headers, Long id) {
        log.debug("::::: In Customer Fall back service");
        /*Cache.ValueWrapper valueWrapper = CustomerHelper.getCacheValue(cacheManager, "customer-cb-cache-by-id", id);
        if(Objects.nonNull(valueWrapper)){
            return (CustomerDetails) valueWrapper.get();
        } else {*/
            log.error(ExceptionUtils.getRootCauseMessage(exception));
            throw new CustomerInternalException(ExceptionUtils.getRootCauseMessage(exception));
        //}
    }

    @Override
    public Response healthCheck() {
        return Response.builder()
                .request(Request.create(Request.HttpMethod.GET, "/healthcheck", new HashMap<>(), Request.Body.empty(), null))
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .body(Status.DOWN.getCode(), StandardCharsets.UTF_8)
                .headers(Map.of(HttpHeaders.CONTENT_TYPE,List.of(MediaType.APPLICATION_JSON_VALUE)))
                .build();
    }
}
