package com.customermanagement.circuitbreaker.config;

import com.hazelcast.config.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;

import java.util.Map;

@Configuration
@Slf4j
public class HazelcastLifecycleCache {

    @Bean
    @Description("Only used for local testing")
    @ConditionalOnMissingBean
    public JoinConfig tcpJoinConfig(){
        JoinConfig joinConfig = new JoinConfig();
        joinConfig.setMulticastConfig(new MulticastConfig().setEnabled(false));
        joinConfig.setTcpIpConfig(new TcpIpConfig().setEnabled(true).addMember("localhost"));

        return joinConfig;
    }

    @Bean
    public Config hzConfig(JoinConfig joinConfig, @Qualifier("hzCaches") Map<String, MapConfig> mapConfigs){
        Config config = new Config().setInstanceName("customer-cb-hz")
                .setNetworkConfig(new NetworkConfig()
                .setRestApiConfig(new RestApiConfig().setEnabled(true).enableGroups(RestEndpointGroup.HEALTH_CHECK)))

                .setProperty("hazelcast.logging.type", "slf4j")
                .setProperty("hazelcast.jmx", "true")

                .setMapConfigs(mapConfigs);

        return config;
    }
}
