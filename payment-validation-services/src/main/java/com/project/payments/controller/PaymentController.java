package com.project.payments.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.payments.pojo.PaymentRequest;
import com.project.payments.service.interfaces.PaymentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/v1/payments")
@Slf4j
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    
    @PostMapping
    public String createPayment(
            @Valid @RequestBody 
            PaymentRequest paymentRequest) {
        log.info("Creating payment... paymentRequest: {}", paymentRequest);
        
        String serviceResponse = paymentService
        		.validateAndCreatePayment(
        				paymentRequest);
        
        log.info("Payment creation response: {}", serviceResponse);
		return serviceResponse;
    }
    
    @GetMapping
    public String getPaymentStatus() {
    	log.info("Getting payment status... This endpoint is under construction.");
    	return "Payment status endpoint is under construction.";
    }

}