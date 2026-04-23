package com.project.payments.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.project.payments.pojo.PaymentRequestV2;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/v1/payments")
@Slf4j
public class PaymentController {

    @PostMapping
    public String createPayment(@Valid @RequestBody PaymentRequestV2 paymentRequest) {
        log.info("Creating payment...paymentRequest: {}", paymentRequest);
        return "Payment created successfully!\n + paymentRequest: " + paymentRequest;
    }
}