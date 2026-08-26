package com.project.payments.controller;

import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.payments.pojo.PaymentRequest;
import com.project.payments.pojo.PaymentResponse;
import com.project.payments.service.interfaces.PaymentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/v1/payments")
@Slf4j
@RequiredArgsConstructor
@RefreshScope
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public PaymentResponse createPayment(
            @Valid @RequestBody PaymentRequest paymentRequest) {
        log.info("Creating payment... paymentRequest: {}", paymentRequest);
        
        PaymentResponse serviceResponse = paymentService
        		.validateAndCreatePayment(paymentRequest);
        
        log.info("Payment created successfully: {}", serviceResponse);
		return serviceResponse;
    }
}