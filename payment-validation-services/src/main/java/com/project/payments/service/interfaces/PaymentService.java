package com.project.payments.service.interfaces;

import com.project.payments.pojo.PaymentRequest;

public interface PaymentService {
    String validateAndCreatePayment(PaymentRequest paymentRequest);
}