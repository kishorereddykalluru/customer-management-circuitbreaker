package com.customermanagement.circuitbreaker.interceptors;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class ResponseBodyAdviceAdapter implements ResponseBodyAdvice<Object> {


    @Override
    public boolean supports(MethodParameter methodParameter, Class<? extends HttpMessageConverter<?>> aClass) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object o, MethodParameter methodParameter, MediaType mediaType, Class<? extends HttpMessageConverter<?>> aClass, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {
        int status = 0;
        if(serverHttpResponse instanceof ServletServerHttpResponse){
            status = ((ServletServerHttpResponse) serverHttpResponse).getServletResponse().getStatus();
        }
        MDC.put("httpStatus", ""+status);

        if(log.isTraceEnabled() && !serverHttpResponse.getHeaders().isEmpty()){
            log.trace("Response headers: {}", serverHttpResponse.getHeaders().entrySet().stream().map(k -> k.getKey()+ ": "+k.getValue()).collect(Collectors.joining(",")));
        }

        if(status >= 400){
            log.error(""+o);
        }else{
            log.trace(""+o);
        }

        return o;
    }
}
