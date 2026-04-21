package com.project.payments.service.interfaces;

import com.project.payments.pojo.CreatePaymentReq;

public interface PaymentService {

    public String createPayment(CreatePaymentReq createPaymentReq);
}