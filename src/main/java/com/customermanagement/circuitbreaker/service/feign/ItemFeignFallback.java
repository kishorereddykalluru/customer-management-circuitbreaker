package com.customermanagement.circuitbreaker.service.feign;

import com.customermanagement.circuitbreaker.exception.ServiceException;
import feign.Request;
import feign.RequestTemplate;
import feign.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.http.HttpHeaders;
import org.springframework.boot.actuate.health.Status;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class ItemFeignFallback implements ItemFeign{

    private final Exception exception;

    public ItemFeignFallback(Exception exception){
        this.exception = exception;
    }
    @Override
    public String getItem() {
        log.error(ExceptionUtils.getRootCauseMessage(exception));
        throw new ServiceException(ExceptionUtils.getRootCauseMessage(exception));
    }

    @Override
    public Response healthCheck() {
        return Response.builder()
                .request(Request.create(Request.HttpMethod.GET, "/actuator/health", new HashMap<>(), Request.Body.empty(), new RequestTemplate()))
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .body(Status.DOWN.getCode(), StandardCharsets.UTF_8)
                .headers(Map.of(HttpHeaders.CONTENT_TYPE, List.of(MediaType.APPLICATION_JSON_VALUE)))
                .build();
    }
}
