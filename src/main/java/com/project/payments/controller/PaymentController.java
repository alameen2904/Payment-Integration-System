package com.project.payments.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/v1/payments")
@Slf4j
public class PaymentController {
	@PostMapping
	public String createPayment() {
		log.info("Creating payment...");
		return "Payment created successfully!";
	}
	

}
