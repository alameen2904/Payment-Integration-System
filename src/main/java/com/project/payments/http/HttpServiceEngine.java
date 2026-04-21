package com.project.payments.http;

import org.springframework.beans.factory.annotation.Value; // 1. Ensure this import is here
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class HttpServiceEngine {

    private final RestClient restClient;

    @Value("${stripe.api.key}") // 2. This pulls the key from properties
    private String stripeApiKey;

    public String makeHttpCall() {
        log.info("Making HTTP call to external service...");

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBasicAuth(stripeApiKey, ""); // 3. Uses the variable
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> formUrlEncodedData = new LinkedMultiValueMap<>();
        formUrlEncodedData.add("line_items[0][price_data][currency]", "EUR");
        formUrlEncodedData.add("line_items[0][quantity]", "2");
        formUrlEncodedData.add("mode", "payment");
        formUrlEncodedData.add("success_url", "https://example.com/success");
        formUrlEncodedData.add("line_items[0][price_data][product_data][name]", "Phone xxx");
        formUrlEncodedData.add("line_items[0][price_data][unit_amount]", "100");

        ResponseEntity<String> httpResponse = restClient.method(HttpMethod.POST)
                .uri("https://api.stripe.com/v1/checkout/sessions")
                .headers(restClientHeaders -> restClientHeaders.addAll(httpHeaders))
                .body(formUrlEncodedData)
                .retrieve()
                .toEntity(String.class);

        log.info("HTTP call completed. Status code: {}, Response body: {}", 
                httpResponse.getStatusCode(), httpResponse.getBody());

        return httpResponse.getBody();
    }

    @PostConstruct
    public void init() {
        log.info("HttpServiceEngine initialized with RestClient: {}", restClient);
    }
}