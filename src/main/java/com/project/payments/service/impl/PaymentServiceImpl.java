package com.project.payments.service.impl;

import org.springframework.stereotype.Service;

import com.project.payments.http.HttpServiceEngine;
import com.project.payments.service.interfaces.PaymentService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
	private final HttpServiceEngine httpServiceEngine;

	@Override
	public String createPayment() {
	    log.info("Processing payment creation logic...");
	    
	    // This captures "HTTP call from HttpServiceEngine successful!"
	    String httpResponse = httpServiceEngine.makeHttpCall(); 
	    
	    log.info("Received response from HttpServiceEngine: {}", httpResponse);
	    
	    // Change this line to return the actual httpResponse
	    return httpResponse; 
	}

}
