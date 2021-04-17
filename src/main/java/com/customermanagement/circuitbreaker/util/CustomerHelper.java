package com.customermanagement.circuitbreaker.util;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

public class CustomerHelper {
    public static Long getCustomerCacheKey(Long id) {
        return id;
    }

    public static Cache.ValueWrapper getCacheValue(CacheManager cacheManager, String s, Long id) {

        Cache cache = cacheManager.getCache(s);
        return cache.get(id);
    }
}
