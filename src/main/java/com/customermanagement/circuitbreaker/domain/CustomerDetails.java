package com.customermanagement.circuitbreaker.domain;

import lombok.*;
import org.springframework.hateoas.RepresentationModel;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerDetails extends RepresentationModel<CustomerDetails> implements Serializable {

    private static final long serialVersionUID = 724260375523516547L;

    private long id;
    private String customerName;
    private String contactName;
    private String city;
    private String country;
}
