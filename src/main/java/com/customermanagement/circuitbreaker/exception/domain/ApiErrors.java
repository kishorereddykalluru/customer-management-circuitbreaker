package com.customermanagement.circuitbreaker.exception.domain;

import lombok.*;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApiErrors implements Serializable {

    private static final long serialVersionUID = -7113667471401129817L;

    @NonNull
    private List<ApiError> apiErrors;
}
