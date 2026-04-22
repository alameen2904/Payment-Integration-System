package com.project.payments.service.interfaces;

import com.project.payments.pojo.CreatePaymentReq;
import com.project.payments.pojo.PaymentResponse;

public interface PaymentService {

    public PaymentResponse createPayment(CreatePaymentReq createPaymentReq);
}