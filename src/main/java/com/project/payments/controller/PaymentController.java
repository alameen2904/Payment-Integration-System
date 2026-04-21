package com.project.payments.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.project.payments.pojo.CreatePaymentReq;
import com.project.payments.pojo.PaymentResponse;
import com.project.payments.service.interfaces.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/v1/payments")
@Slf4j
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;
    
    @PostMapping
    public PaymentResponse createPayment(
			@RequestBody CreatePaymentReq createPaymentReq) {
		log.info("Creating payment... createPaymentReq: {}", createPaymentReq);

		PaymentResponse paymentResponse = paymentService.createPayment(createPaymentReq);
		log.info("Payment created: {}", paymentResponse);

		return paymentResponse;
	}

}