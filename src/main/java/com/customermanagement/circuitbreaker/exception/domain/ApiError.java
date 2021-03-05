package com.customermanagement.circuitbreaker.exception.domain;

import lombok.*;

import java.io.Serializable;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiError implements Serializable {
    private static final long serialVersionUID = 7364568571761449485L;

    @NonNull
    private int code;
    @NonNull
    private String message;
}
