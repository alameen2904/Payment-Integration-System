package com.project.payments.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.project.payments.http.HttpRequest;
import com.project.payments.http.HttpServiceEngine;
import com.project.payments.service.interfaces.PaymentService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final HttpServiceEngine httpServiceEngine;

    // You need the key here if you are building the headers in this layer
    @Value("${stripe.api.key}")
    private String stripeApiKey;

    @Override
    public String createPayment() {
        log.info("Processing payment creation logic...");

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBasicAuth(stripeApiKey, ""); 
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> formUrlEncodedData = new LinkedMultiValueMap<>();
        formUrlEncodedData.add("line_items[0][price_data][currency]", "EUR");
        formUrlEncodedData.add("line_items[0][quantity]", "2");
        formUrlEncodedData.add("mode", "payment");
        formUrlEncodedData.add("success_url", "https://example.com/success");
        formUrlEncodedData.add("line_items[0][price_data][product_data][name]", "Phone xxx");
        formUrlEncodedData.add("line_items[0][price_data][unit_amount]", "100");

        HttpRequest httpRequest = new HttpRequest();
        httpRequest.setUrl("https://api.stripe.com/v1/checkout/sessions");
        httpRequest.setHttpMethod(HttpMethod.POST);
        httpRequest.setHttpHeaders(httpHeaders);
        httpRequest.setRequestData(formUrlEncodedData);

        return httpServiceEngine.makeHttpCall(httpRequest); 
	}

}
