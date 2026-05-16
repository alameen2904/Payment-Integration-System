package com.project.payments.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.project.payments.cache.ValidatorRuleCacheV2;
import com.project.payments.constant.ErrorCodeEnum;
import com.project.payments.constant.ValidatorRuleEnum;
import com.project.payments.exception.PaymentValidationException;
import com.project.payments.http.HttpRequest; // CUSTOM IMPORT - VERY IMPORTANT
import com.project.payments.http.HttpServiceEngine;
import com.project.payments.pojo.PaymentRequest;
import com.project.payments.pojo.PaymentResponse;
import com.project.payments.service.helper.StripeProviderHelper;
import com.project.payments.service.interfaces.BusinessValidator;
import com.project.payments.service.interfaces.PaymentService;
import com.project.payments.stripeprovider.SPPaymentResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final ApplicationContext applicationContext;
    private final ValidatorRuleCacheV2 validatorRuleCache;
    private final StripeProviderHelper stripeProviderHelper;
    private final HttpServiceEngine httpServiceEngine;

    @Override
    public PaymentResponse validateAndCreatePayment(PaymentRequest paymentRequest) {
        log.info("Validating and creating payment for request: {}", paymentRequest);

        // --- Step 1: Validation Logic ---
        List<String> validatorRules = validatorRuleCache.getValidatorRules();
        if (validatorRules == null || validatorRules.isEmpty()) {
            throw new PaymentValidationException(
                    ErrorCodeEnum.NO_VALIDATION_RULES_CONFIGURED.getErrorCode(),
                    ErrorCodeEnum.NO_VALIDATION_RULES_CONFIGURED.getErrorMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        for (String rule : validatorRules) {
            Optional<Class<? extends BusinessValidator>> validatorClass = 
                    ValidatorRuleEnum.getValidatorClassByRule(rule.trim());
            
            if (validatorClass.isPresent()) {
                BusinessValidator validator = applicationContext.getBean(validatorClass.get());
                validator.validate(paymentRequest);
            }
        }

        log.info("All validations passed. Invoking Stripe via HttpServiceEngine...");

        // --- Step 2: HTTP Engine Call ---
        // Prepare request using helper
        HttpRequest httpRequest = stripeProviderHelper.createHttpRequest(paymentRequest);
        
        // Make the call via Engine (Circuit Breaker is active here)
        ResponseEntity<String> httpResponse = httpServiceEngine.makeHttpCall(httpRequest);
        
        // Process response using helper
        SPPaymentResponse stripeResponse = stripeProviderHelper.processResponse(httpResponse);

        if (stripeResponse == null) {
            throw new PaymentValidationException(
                    "STRIPE_ERROR",
                    "Failed to process response from Stripe provider",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // --- Step 3: Map to Final Response ---
        PaymentResponse finalResponse = new PaymentResponse();

        finalResponse.setStripeSessionId(stripeResponse.getStripeSessionId());
        finalResponse.setHostedPageUrl(stripeResponse.getHostedPageUrl());

        log.info("Final PaymentResponse prepared. SessionId: {}, URL: {}",
              finalResponse.getStripeSessionId(),
              finalResponse.getHostedPageUrl());

        return finalResponse;
    }
}
