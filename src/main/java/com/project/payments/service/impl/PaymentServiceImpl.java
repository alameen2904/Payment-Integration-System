package com.project.payments.service.impl;

import org.springframework.stereotype.Service;

import com.project.payments.http.HttpRequest;
import com.project.payments.http.HttpServiceEngine;
import com.project.payments.pojo.CreatePaymentReq; // Add this import
import com.project.payments.service.helper.CreatePaymentHelper;
import com.project.payments.service.interfaces.PaymentService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    
    private final HttpServiceEngine httpServiceEngine;
    private final CreatePaymentHelper createPaymentHelper;

    @Override
    public String createPayment(CreatePaymentReq createPaymentReq) { 
        log.info("Processing payment creation logic...createPaymentReq: {}", createPaymentReq);

        // FIX: Pass the object to the helper
        HttpRequest httpRequest = createPaymentHelper.prepareStripeCreatedSessionRequest(createPaymentReq);

        return httpServiceEngine.makeHttpCall(httpRequest); 
    }
}