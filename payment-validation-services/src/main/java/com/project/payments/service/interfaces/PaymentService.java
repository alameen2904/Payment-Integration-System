package com.project.payments.service.interfaces;

import com.project.payments.pojo.PaymentRequest;
import com.project.payments.pojo.PaymentResponse; // Add this import

public interface PaymentService {
    
    
    PaymentResponse validateAndCreatePayment(PaymentRequest paymentRequest);
}