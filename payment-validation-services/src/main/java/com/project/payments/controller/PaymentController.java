package com.project.payments.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.project.payments.pojo.PaymentRequest;
import com.project.payments.service.interfaces.PaymentService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/v1/payments")
@Slf4j
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public String createPayment(@Valid @RequestBody PaymentRequest paymentRequest) {
        log.info("Received request at controller level: {}", paymentRequest);
        String serviceResponse = paymentService.validateAndCreatePayment(paymentRequest);
		return serviceResponse;
    }
}