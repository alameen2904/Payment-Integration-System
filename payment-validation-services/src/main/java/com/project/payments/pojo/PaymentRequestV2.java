package com.project.payments.pojo;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PaymentRequestV2 {
    
    @NotNull(message = "USER_NULL")
    @Valid
    private User user;

    @NotNull(message = "PAYMENT_NULL")
    @Valid
    private Payment payment;
}