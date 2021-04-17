package com.customermanagement.circuitbreaker.config;

import com.hazelcast.config.EvictionConfig;
import com.hazelcast.config.EvictionPolicy;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.MaxSizePolicy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class HazelcastCacheConfig {

    @Bean
    public Map<String, MapConfig> hzCaches(){
        Map<String, MapConfig> mapConfigs = new HashMap<>();
        mapConfigs.put(customerCache().getName(), customerCache());
        mapConfigs.put(customerCacheById().getName(), customerCacheById());
        return mapConfigs;
    }

    /**
     *
     * Key: customerId
     * Value: Customer
     */
    private MapConfig customerCache() {
        return new MapConfig().setName("customer-cb-cache")
                .setBackupCount(1)
                .setStatisticsEnabled(true)
                .setEvictionConfig(new EvictionConfig()
                        .setMaxSizePolicy(MaxSizePolicy.USED_HEAP_SIZE).setEvictionPolicy(EvictionPolicy.LFU))
                .setTimeToLiveSeconds(100);
    }

    /**
     *
     * Key: customerId
     * Value: Customer
     */
    private MapConfig customerCacheById() {
        return new MapConfig().setName("customer-cb-cache-by-id")
                .setBackupCount(1)
                .setStatisticsEnabled(true)
                .setEvictionConfig(new EvictionConfig()
                        .setMaxSizePolicy(MaxSizePolicy.USED_HEAP_SIZE).setEvictionPolicy(EvictionPolicy.LFU))
                .setTimeToLiveSeconds(100);
    }
}
