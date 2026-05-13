package com.project.payments.service.impl;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.project.payments.constant.ErrorCodeEnum;
import com.project.payments.exception.StripeProviderException;
import com.project.payments.http.HttpRequest;
import com.project.payments.http.HttpServiceEngine;
import com.project.payments.pojo.CreatePaymentReq;
import com.project.payments.pojo.PaymentResponse;
import com.project.payments.service.ValidationService;
import com.project.payments.service.helper.CreatePaymentHelper;
import com.project.payments.service.interfaces.PaymentService;
import com.project.payments.stripe.CheckoutSessionResponse;
import com.project.payments.stripe.StripeErrorResponse;
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
        log.info("Inside createPayment method. Received Request: {}", createPaymentReq);
        
        // 1. Validation (Checks if successUrl, etc., are present)
        validationService.isValid(createPaymentReq);

        // 2. Prepare call to Stripe API
        HttpRequest httpRequest = createPaymentHelper.prepareStripeCreateSessionRequest(createPaymentReq);
        ResponseEntity<String> httpResponse = httpServiceEngine.makeHttpCall(httpRequest);

        // 3. Process the response from Stripe's Servers
        CheckoutSessionResponse checkoutSession = processStripeResponse(httpResponse);

        // 4. Map Stripe's format (id, url) to our Internal format (stripeSessionId, hostedPageUrl)
        PaymentResponse paymentResponse = mapCheckoutSessionToPaymentResponse(checkoutSession);

        log.info("Final PaymentResponse prepared and returning to Validation Service: {}", paymentResponse);
        return paymentResponse;
    }

    private CheckoutSessionResponse processStripeResponse(ResponseEntity<String> httpResponse) {
        if (httpResponse == null) throw new StripeProviderException(ErrorCodeEnum.STRIPE_NO_RESPONSE.getErrorCode(), "No response from Stripe", null);

        HttpStatus status = HttpStatus.valueOf(httpResponse.getStatusCode().value());
        String body = httpResponse.getBody();

        if (status.is2xxSuccessful()) {
            CheckoutSessionResponse checkoutSession = jsonUtil.convertJsonToObject(body, CheckoutSessionResponse.class);
            if (checkoutSession == null || checkoutSession.getUrl() == null) {
                log.error("Stripe URL missing in response body: {}", body);
                throw new StripeProviderException(ErrorCodeEnum.STRIPE_CONVERSION_FAILED.getErrorCode(), "URL missing from Stripe JSON", status);
            }
            log.info("Stripe Session Data Extracted: ID={}, URL={}", checkoutSession.getId(), checkoutSession.getUrl());
            return checkoutSession;
        }
        
        log.error("Stripe API Error. Status: {}, Body: {}", status, body);
        throw new StripeProviderException(ErrorCodeEnum.STRIPE_API_ERROR.getErrorCode(), "Stripe call failed", status);
    }

    private PaymentResponse mapCheckoutSessionToPaymentResponse(CheckoutSessionResponse checkoutSession) {
        PaymentResponse paymentResponse = new PaymentResponse();
        paymentResponse.setStripeSessionId(checkoutSession.getId());
        paymentResponse.setHostedPageUrl(checkoutSession.getUrl());
        return paymentResponse;
    }
}