package com.project.payments.stripeprovider;

import lombok.Data;

@Data
public class SPErrorResponse {
    private String errorCode;
    private String errorMessage;
}
