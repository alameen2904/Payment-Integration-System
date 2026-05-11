package com.project.payments.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.payments.pojo.PaymentRequest;
import com.project.payments.pojo.PaymentResponse;
import com.project.payments.service.interfaces.PaymentService;

import jakarta.annotation.PostConstruct;
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
    
    @Value("${mytestkey:default_value}")
    private String myTestKey;
    
    @PostMapping
    public PaymentResponse createPayment(
            @Valid @RequestBody PaymentRequest paymentRequest) {
        log.info("Creating payment... paymentRequest: {}", paymentRequest);
        
        PaymentResponse serviceResponse = paymentService
        		.validateAndCreatePayment(paymentRequest);
        
        log.info("Payment created successfully: {}", serviceResponse);
		return serviceResponse;
    }
    
    @GetMapping("/status")
    public String getPaymentStatus() {  
    	return "Payment validation service is UP. Test Key from Config: " + myTestKey;
    }
    
    @PostConstruct
    public void init() {   
    	log.info("**** PaymentController initialized. myTestKey value: {}", myTestKey);  
    }
}