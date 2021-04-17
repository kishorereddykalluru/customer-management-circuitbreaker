package com.customermanagement.circuitbreaker.util;

import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Component("customerKeyGenerator")
public class CustomerKeyGenerator implements KeyGenerator {
    @Override
    public Object generate(Object o, Method method, Object... objects) {
        Long id = (Long) objects[1];
        return id;
    }
}
