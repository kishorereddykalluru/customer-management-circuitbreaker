package com.customermanagement.circuitbreaker.service.client;

import com.customermanagement.circuitbreaker.exception.ServiceException;
import com.customermanagement.circuitbreaker.service.feign.ItemFeign;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.IllegalStateTransitionException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ItemClient {

    @Value("${customer.version:application/json}")
    private String acceptHeader;

    @Autowired
    private ItemFeign itemFeign;

    public String getItem(){
        String item = null;
        try {
            item = itemFeign.getItem();
            if(log.isTraceEnabled()) log.trace("Fetched {} response", item);
        } catch(CallNotPermittedException | IllegalStateTransitionException e){
            log.error("Item service call failed with: {}", ExceptionUtils.getRootCauseMessage(e));
            throw new ServiceException(ExceptionUtils.getRootCauseMessage(e));
        }

        return item;
    }
}
