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

        log.info("Inside createPayment method. Request: {}", createPaymentReq);

        // 1️⃣ Validate input
        validationService.isValid(createPaymentReq);

        // 2️⃣ Prepare Stripe HTTP request - Method name now matches Helper exactly
        HttpRequest httpRequest =
                createPaymentHelper.prepareStripeCreateSessionRequest(createPaymentReq);

        // 3️⃣ Call Stripe API
        ResponseEntity<String> httpResponse =
                httpServiceEngine.makeHttpCall(httpRequest);

        log.info("HTTP response status from Stripe: {}", 
                httpResponse != null ? httpResponse.getStatusCode() : "NULL");

        // 4️⃣ Process Stripe response (Handles conversion and error parsing)
        CheckoutSessionResponse checkoutSession =
                processStripeResponse(httpResponse);

        // 5️⃣ Map to final API response
        PaymentResponse paymentResponse =
                mapCheckoutSessionToPaymentResponse(checkoutSession);

        log.info("Returning PaymentResponse: {}", paymentResponse);

        return paymentResponse;
    }

    private CheckoutSessionResponse processStripeResponse(ResponseEntity<String> httpResponse) {
        if (httpResponse == null) {
            log.error("No response received from Stripe.");
            throw new StripeProviderException(
                    ErrorCodeEnum.STRIPE_NO_RESPONSE.getErrorCode(),
                    ErrorCodeEnum.STRIPE_NO_RESPONSE.getErrorMessage(),
                    null
            );
        }

        // Bridge HttpStatusCode to HttpStatus for custom exception compatibility
        HttpStatus status = HttpStatus.valueOf(httpResponse.getStatusCode().value());
        String body = httpResponse.getBody();

        // ✅ SUCCESS PATH (2xx)
        if (status.is2xxSuccessful()) {
            log.info("Stripe API call successful. Status: {}", status);

            CheckoutSessionResponse checkoutSession =
                    jsonUtil.convertJsonToObject(body, CheckoutSessionResponse.class);

            if (checkoutSession == null) {
                log.error("Stripe response conversion failed. Body: {}", body);
                throw new StripeProviderException(
                        ErrorCodeEnum.STRIPE_CONVERSION_FAILED.getErrorCode(),
                        ErrorCodeEnum.STRIPE_CONVERSION_FAILED.getErrorMessage(),
                        status
                );
            }

            log.info("Stripe Session Created. ID={}, URL={}", checkoutSession.getId(), checkoutSession.getUrl());
            return checkoutSession;
        }

        // ❌ ERROR PATH (4xx / 5xx)
        if (status.is4xxClientError() || status.is5xxServerError()) {
            try {
                StripeErrorResponse stripeError =
                        jsonUtil.convertJsonToObject(body, StripeErrorResponse.class);

                if (stripeError != null && stripeError.getError() != null) {
                    log.error("Stripe Error Details: Type={}, Code={}, Msg={}",
                            stripeError.getError().getType(),
                            stripeError.getError().getCode(),
                            stripeError.getError().getMessage());

                    String errorMessage = prepareStripeErrorMessage(stripeError);

                    throw new StripeProviderException(
                            ErrorCodeEnum.STRIPE_API_ERROR.getErrorCode(),
                            errorMessage,
                            status
                    );
                }
            } catch (StripeProviderException spe) {
                throw spe; // Re-throw our custom exception if already caught
            } catch (Exception ex) {
                log.error("Fallback error: Failed to parse Stripe error body: {}", ex.getMessage());
            }

            throw new StripeProviderException(
                    ErrorCodeEnum.STRIPE_API_ERROR.getErrorCode(),
                    "Stripe API call failed: " + body,
                    status
            );
        }

        throw new StripeProviderException(
                ErrorCodeEnum.GENERIC_ERROR.getErrorCode(),
                ErrorCodeEnum.GENERIC_ERROR.getErrorMessage(),
                status
        );
    }

    private String prepareStripeErrorMessage(StripeErrorResponse stripeErrorResponse) {
        return stripeErrorResponse.getError().getType() + " - " +
               stripeErrorResponse.getError().getCode() + " - " +
               stripeErrorResponse.getError().getMessage();
    }

    private PaymentResponse mapCheckoutSessionToPaymentResponse(CheckoutSessionResponse checkoutSession) {
        if (checkoutSession == null) {
            throw new StripeProviderException(
                    ErrorCodeEnum.STRIPE_CONVERSION_FAILED.getErrorCode(),
                    "Internal error during response mapping",
                    null
            );
        }

        PaymentResponse paymentResponse = new PaymentResponse();
        paymentResponse.setStripeSessionId(checkoutSession.getId());
        paymentResponse.setHostedPageUrl(checkoutSession.getUrl());

        return paymentResponse;
    }
}