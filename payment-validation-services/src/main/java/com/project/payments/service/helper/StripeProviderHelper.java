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

    private final ObjectMapper objectMapper; // Spring will provide this

    @Value("${stripe.provider.url:http://localhost:8082/v1/stripe/pay}")
    private String stripeProviderUrl;

    /**
     * Prepares the custom HttpRequest object for our HttpServiceEngine.
     */
    public HttpRequest createHttpRequest(PaymentRequest paymentRequest) {
        log.info("Preparing HttpRequest for Stripe provider URL: {}", stripeProviderUrl);
        
        HttpRequest httpRequest = new HttpRequest();
        httpRequest.setHttpMethod(HttpMethod.POST);
        httpRequest.setUrl(stripeProviderUrl);
        httpRequest.setRequestData(paymentRequest);
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
            return objectMapper.readValue(httpResponse.getBody(), SPPaymentResponse.class);
        } catch (Exception e) {
            log.error("Error while parsing Stripe response: {}", e.getMessage());
            return null;
        }
    }
}