package com.customermanagement.circuitbreaker.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerDetails {

    private long id;
    private String customerName;
    private String contactName;
    private String city;
    private String country;
}
