package com.customermanagement.circuitbreaker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class CustomerManagementCircuitBreakerApplication {

	public static void main(String[] args) {
		SpringApplication.run(CustomerManagementCircuitBreakerApplication.class, args);
	}

}
