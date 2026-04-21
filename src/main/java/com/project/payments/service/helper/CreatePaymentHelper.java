package com.project.payments.service.helper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.project.payments.constant.Constant;
import com.project.payments.http.HttpRequest;
import com.project.payments.pojo.CreatePaymentReq;
import com.project.payments.pojo.LineItem;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CreatePaymentHelper {

    @Value("${stripe.api.key}")
    private String stripeApiKey;

    @Value("${stripe.create.session.url}")
    private String stripeCreateSessionUrl;

    public HttpRequest prepareStripeCreatedSessionRequest(CreatePaymentReq createPaymentReq) {
        log.info("Preparing Stripe request with dynamic data...");

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBasicAuth(stripeApiKey, ""); 
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        
        MultiValueMap<String, String> formUrlEncodedData = prepareFormUrlEncodedData(createPaymentReq);
log.info("Prepared form URL encoded data for Stripe create-session API: {}", formUrlEncodedData);
        HttpRequest httpRequest = new HttpRequest();
        httpRequest.setUrl(stripeCreateSessionUrl);
        httpRequest.setHttpMethod(HttpMethod.POST);
        httpRequest.setHttpHeaders(httpHeaders);
        httpRequest.setRequestData(formUrlEncodedData);

        log.info("Prepared HttpRequest for Stripe create-session API: {}", httpRequest);
		return httpRequest;
	}

    public static MultiValueMap<String, String> prepareFormUrlEncodedData(CreatePaymentReq request) {

        MultiValueMap<String, String> formUrlEncodedData = new LinkedMultiValueMap<>();

        
        formUrlEncodedData.add(Constant.CREATE_SESSION_MODE, 
    			Constant.CREATE_SESSION_MODE_PAYMENT);
        
        formUrlEncodedData.add(Constant.CREATE_SESSION_SUCCESS_URL, 
        		request.getSuccessUrl());
        
        formUrlEncodedData.add(Constant.CREATE_SESSION_CANCEL_URL, 
        		request.getCancelUrl());

        
        if (request.getLineItems() != null && !request.getLineItems().isEmpty()) {

            for (int i = 0; i < request.getLineItems().size(); i++) {

                LineItem item = request.getLineItems().get(i);

               
                String baseKey = "line_items[" + i + "]";

                formUrlEncodedData.add(baseKey + Constant.OPEN_BRACKET + Constant.QUANTITY + Constant.CLOSE_BRACKET, String.valueOf(item.getQuantity()));
                formUrlEncodedData.add(baseKey + Constant.OPEN_BRACKET + Constant.PRICE_DATA + Constant.CLOSE_BRACKET + Constant.OPEN_BRACKET + Constant.CURRENCY + Constant.CLOSE_BRACKET, item.getCurrency());
                formUrlEncodedData.add(baseKey + Constant.OPEN_BRACKET + Constant.PRICE_DATA + Constant.CLOSE_BRACKET + Constant.OPEN_BRACKET + Constant.UNIT_AMOUNT + Constant.CLOSE_BRACKET, String.valueOf(item.getUnitAmount()));
                formUrlEncodedData.add(baseKey + Constant.OPEN_BRACKET + Constant.PRICE_DATA + Constant.CLOSE_BRACKET + Constant.OPEN_BRACKET + Constant.PRODUCT_DATA + Constant.CLOSE_BRACKET + Constant.OPEN_BRACKET + Constant.NAME + Constant.CLOSE_BRACKET, item.getProductName());
            }
        }

        return formUrlEncodedData;
    }
}