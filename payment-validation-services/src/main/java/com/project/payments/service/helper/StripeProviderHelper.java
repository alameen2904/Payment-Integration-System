package com.project.payments.service.helper;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.payments.http.HttpRequest;
import com.project.payments.pojo.PaymentRequest;
import com.project.payments.stripeprovider.SPPaymentResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Value;

@Service
@Slf4j
@RequiredArgsConstructor
public class StripeProviderHelper {

    private final ObjectMapper objectMapper;

    @Value("${stripe.provider.createPaymentUrl}")
    private String stripeProviderUrl;

    /**
     * Prepares the custom HttpRequest object for our HttpServiceEngine.
     */
    public HttpRequest createHttpRequest(PaymentRequest paymentRequest) {
        log.info("Preparing HttpRequest for Stripe provider URL: {}", stripeProviderUrl);
        
        // --- DEBUG LOGGING START ---
        try {
            String jsonPayload = objectMapper.writeValueAsString(paymentRequest);
            log.info("FINAL JSON payload being sent to Stripe Provider: {}", jsonPayload);
        } catch (Exception e) {
            log.error("Failed to log JSON payload: {}", e.getMessage());
        }
        // --- DEBUG LOGGING END ---
        
        HttpRequest httpRequest = new HttpRequest();
        httpRequest.setHttpMethod(HttpMethod.POST);
        httpRequest.setUrl(stripeProviderUrl);
        httpRequest.setRequestData(paymentRequest.getPayment());
        httpRequest.setHttpHeaders(new HttpHeaders());
        
        return httpRequest;
    }

    /**
     * Converts the String response from HttpServiceEngine into SPPaymentResponse.
     */
    public SPPaymentResponse processResponse(ResponseEntity<String> httpResponse) {
        if (httpResponse == null || httpResponse.getBody() == null) {
            log.error("Invalid response received from Stripe Provider Engine");
            return null;
        }

        try {
            log.info("Parsing String response to SPPaymentResponse...");
            // If the response contains an error (like 400 Bad Request), this might return null 
            // if the fields don't match SPPaymentResponse.
            return objectMapper.readValue(httpResponse.getBody(), SPPaymentResponse.class);
        } catch (Exception e) {
            log.error("Error while parsing Stripe response: {}", e.getMessage());
            return null;
        }
    }
}