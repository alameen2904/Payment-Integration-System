package com.project.payments.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.project.payments.http.HttpRequest;
import com.project.payments.http.HttpServiceEngine;
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
    public String createPayment() {
        log.info("Processing payment creation logic...");

        HttpRequest httpRequest = createPaymentHelper.prepareStripeCreatedSessionRequest();

        return httpServiceEngine.makeHttpCall(httpRequest); 
    }
}