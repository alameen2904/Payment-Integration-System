package com.project.payments.service.helper;

import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.project.payments.constant.Constant;
import com.project.payments.constant.ErrorCodeEnum;
import com.project.payments.exception.StripeProviderException;
import com.project.payments.http.HttpRequest;
import com.project.payments.pojo.CreatePaymentReq;
import com.project.payments.pojo.LineItem;
import com.project.payments.stripe.CheckoutSessionResponse;
import com.project.payments.stripe.StripeError;
import com.project.payments.stripe.StripeErrorResponse;
import com.project.payments.util.JsonUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class CreatePaymentHelper {

    @Value("${stripe.api.key}")
    private String stripeApiKey;
    
    @Value("${stripe.create.session.url}")
    private String stripeCreateSessionUrl;
    
    private final JsonUtil jsonUtil;

    public HttpRequest prepareStripeCreateSessionRequest(CreatePaymentReq createPaymentReq) {
        
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBasicAuth(stripeApiKey, "");
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // Form body
        MultiValueMap<String, String> formUrlEncodedData = prepareFormUrlEncodedData(createPaymentReq);
        log.info("Prepared form URL encoded data for Stripe create-session API: {}", formUrlEncodedData);
        
        HttpRequest httpRequest = new HttpRequest();
        httpRequest.setHttpMethod(HttpMethod.POST);
        httpRequest.setUrl(stripeCreateSessionUrl);
        httpRequest.setHttpHeaders(httpHeaders);
        httpRequest.setRequestData(formUrlEncodedData);
        
        log.info("Prepared HttpRequest for Stripe create-session API: {}", httpRequest);
        return httpRequest;
    }
    
    public static MultiValueMap<String, String> prepareFormUrlEncodedData(CreatePaymentReq request) {

        MultiValueMap<String, String> formUrlEncodedData = new LinkedMultiValueMap<>();

        // Mandatory fields
        formUrlEncodedData.add(Constant.CREATE_SESSION_MODE, Constant.CREATE_SESSION_MODE_PAYMENT);
        formUrlEncodedData.add(Constant.CREATE_SESSION_SUCCESS_URL, request.getSuccessUrl());
        formUrlEncodedData.add(Constant.CREATE_SESSION_CANCEL_URL, request.getCancelUrl());

        // Line items mapping
        if (request.getLineItems() != null && !request.getLineItems().isEmpty()) {
            for (int i = 0; i < request.getLineItems().size(); i++) {
                LineItem item = request.getLineItems().get(i);
                String baseKey = Constant.LINE_ITEMS + Constant.OPEN_BRACKET + i + Constant.CLOSE_BRACKET;

                formUrlEncodedData.add(baseKey + Constant.OPEN_BRACKET + Constant.QUANTITY + Constant.CLOSE_BRACKET, String.valueOf(item.getQuantity()));
                formUrlEncodedData.add(baseKey + Constant.OPEN_BRACKET + Constant.PRICE_DATA + Constant.CLOSE_BRACKET + Constant.OPEN_BRACKET + Constant.CURRENCY + Constant.CLOSE_BRACKET, item.getCurrency());
                formUrlEncodedData.add(baseKey + Constant.OPEN_BRACKET + Constant.PRICE_DATA + Constant.CLOSE_BRACKET + Constant.OPEN_BRACKET + Constant.UNIT_AMOUNT + Constant.CLOSE_BRACKET, String.valueOf(item.getUnitAmount()));
                formUrlEncodedData.add(baseKey + Constant.OPEN_BRACKET + Constant.PRICE_DATA + Constant.CLOSE_BRACKET + Constant.OPEN_BRACKET + Constant.PRODUCT_DATA + Constant.CLOSE_BRACKET + Constant.OPEN_BRACKET + Constant.NAME + Constant.CLOSE_BRACKET, item.getProductName());
            }
        }
        return formUrlEncodedData;
    }
    
    public CheckoutSessionResponse processStripeResponse(ResponseEntity<String> httpResponse) {

        if (httpResponse.getStatusCode().is2xxSuccessful()) {
            log.info("Stripe API call successful. Status: {}", httpResponse.getStatusCode());

            CheckoutSessionResponse checkoutSession = jsonUtil.convertJsonToObject(httpResponse.getBody(), CheckoutSessionResponse.class);

            if (checkoutSession != null && checkoutSession.getUrl() != null) {
                log.info("Stripe session created. ID: {}", checkoutSession.getId());
                return checkoutSession;
            } 
            log.error("Invalid 2xx response from Stripe: {}", httpResponse.getBody());
        }
        
        if (httpResponse.getStatusCode().is4xxClientError() || httpResponse.getStatusCode().is5xxServerError()) {
            StripeErrorResponse stripeError = jsonUtil.convertJsonToObject(httpResponse.getBody(), StripeErrorResponse.class);
            
            if (stripeError != null && stripeError.getError() != null) {
                String errorMsg = prepareStripeErrorMessage(stripeError);
                log.error("Stripe Error: {}", errorMsg);
                
                throw new StripeProviderException(
                        ErrorCodeEnum.STRIPE_API_ERROR.getErrorCode(),
                        errorMsg,
                        HttpStatus.valueOf(httpResponse.getStatusCode().value()));
            }
        }

        throw new StripeProviderException(
                ErrorCodeEnum.INVALID_STRIPE_RESPONSE.getErrorCode(),
                ErrorCodeEnum.INVALID_STRIPE_RESPONSE.getErrorMessage(),
                HttpStatus.BAD_GATEWAY);
    }
    
    private String prepareStripeErrorMessage(StripeErrorResponse stripeErrorResponse) {
        StripeError error = stripeErrorResponse.getError();
        return Stream.of(error.getType(), error.getMessage(), error.getParam(), error.getCode())
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.joining(" | "));
    }
}