package com.project.payments.service.helper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.project.payments.http.HttpRequest;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CreatePaymentHelper {
	
	
	 @Value("${stripe.api.key}")
	    private String stripeApiKey;
	
	@Value("${stripe.create.session.url}")
	  private String stripeCreateSessionUrl;

    public HttpRequest prepareStripeCreatedSessionRequest() {
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
      
		httpRequest.setUrl(stripeCreateSessionUrl);
        httpRequest.setHttpMethod(HttpMethod.POST);
        httpRequest.setHttpHeaders(httpHeaders);
        httpRequest.setRequestData(formUrlEncodedData);
        
        return httpRequest;
    }
}