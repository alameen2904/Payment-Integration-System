package com.project.payments.service.impl.businessvalidators;

import org.springframework.http.HttpStatus; 
import org.springframework.stereotype.Service;
import com.project.payments.constant.ErrorCodeEnum;
import com.project.payments.exception.PaymentValidationException;
import com.project.payments.pojo.PaymentRequest;
import com.project.payments.repository.interfaces.MerchantPaymentRequestRepository;
import com.project.payments.service.interfaces.BusinessValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentAttemptThresholdValidator implements BusinessValidator {

    private final MerchantPaymentRequestRepository merchantReqRepo;

    @Override
    public void validate(PaymentRequest paymentRequest) {
        log.info("Validating payment attempt threshold for payment request: {}", paymentRequest);
        
        int movingWindowMinutes = 10;
        int maxAllowedAttempts = 5;
        
       
        int count = merchantReqRepo.countRequestsForUserInLastMinutes(
                paymentRequest.getUser().getEndUserID(), 
                movingWindowMinutes
        );
        
        log.info("Count of payment attempts for user {} in last {} minutes: {}", 
                paymentRequest.getUser().getEndUserID(), movingWindowMinutes, count);

        if (count < maxAllowedAttempts) {
            log.info("Payment request is valid, attempt count {} is within threshold {}", count, maxAllowedAttempts);
            return;
        } 

        log.error("Payment request exceeds attempt threshold. Attempt count: {}, Threshold: {}", count, maxAllowedAttempts);
        
        
        throw new PaymentValidationException(
                ErrorCodeEnum.PAYMENT_ATTEMPT_THRESHOLD_EXCEEDED.getErrorCode(),
                ErrorCodeEnum.PAYMENT_ATTEMPT_THRESHOLD_EXCEEDED.getErrorMessage(), 
                HttpStatus.TOO_MANY_REQUESTS
        );
    }
}