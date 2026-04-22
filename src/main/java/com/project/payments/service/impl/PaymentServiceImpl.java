package com.project.payments.service.impl;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.project.payments.exception.StripeProviderException;
import com.project.payments.http.HttpRequest;
import com.project.payments.http.HttpServiceEngine;
import com.project.payments.pojo.CreatePaymentReq; // Add this import
import com.project.payments.pojo.PaymentResponse;
import com.project.payments.service.ValidationService;
import com.project.payments.service.helper.CreatePaymentHelper;
import com.project.payments.service.interfaces.PaymentService;
import com.project.payments.stripe.CheckoutSessionResponse;
import com.project.payments.util.JsonUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    
    private final HttpServiceEngine httpServiceEngine;
    private final CreatePaymentHelper createPaymentHelper;
    private final JsonUtil jsonUtil;
    private final ValidationService validationService;

    @Override
    public PaymentResponse createPayment(CreatePaymentReq createPaymentReq) { 
        log.info("Processing payment creation logic...createPaymentReq: {}", createPaymentReq);

        // Validate request using ValidationService
        validationService.isValid(createPaymentReq);

        HttpRequest httpRequest = createPaymentHelper.prepareStripeCreatedSessionRequest(createPaymentReq);

        ResponseEntity<String>httpResponse = httpServiceEngine.makeHttpCall(httpRequest);
        log.info("Received response from HttpServiceEngine: {}", httpResponse);
        CheckoutSessionResponse checkoutSession = jsonUtil.convertJsonToObject(httpResponse.getBody(), CheckoutSessionResponse.class);
        log.info("Converted CheckoutSessionResponse: {}", checkoutSession);
        PaymentResponse paymentResponse = mapCheckoutSessionToPaymentResponse(checkoutSession);
		log.info("Mapped PaymentResponse: {}", paymentResponse);

		return paymentResponse;
    }
    public PaymentResponse mapCheckoutSessionToPaymentResponse(
			CheckoutSessionResponse checkoutSession) {

		if (checkoutSession == null) {
			log.warn("mapCheckoutSessionToPaymentResponse called with null checkoutSession");
			return null;
		}

		PaymentResponse paymentResponse = new PaymentResponse();
		paymentResponse.setStripeSessionId(checkoutSession.getId());
		paymentResponse.setHostedPageUrl(checkoutSession.getUrl());

		log.info("Mapped CheckoutSessionResponse to PaymentResponse: {}", paymentResponse);
		return paymentResponse;
	}
}