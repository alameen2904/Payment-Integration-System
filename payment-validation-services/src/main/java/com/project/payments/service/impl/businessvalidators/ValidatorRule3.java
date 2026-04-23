package com.project.payments.service.impl.businessvalidators;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import com.project.payments.constant.ErrorCodeEnum;
import com.project.payments.exception.PaymentValidationException;
import com.project.payments.pojo.PaymentRequest;
import com.project.payments.service.interfaces.BusinessValidator;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ValidatorRule3 implements BusinessValidator {

    @Override
    public void validate(PaymentRequest paymentRequest) {
        log.info("Executing ValidatorRule3 for paymentRequest: {}", paymentRequest);

        Integer amount = paymentRequest.getPayment().getAmount();

        if (amount != null && amount <= 5) {
            log.warn("Validation failed: amount {} is too low", amount);
            throw new PaymentValidationException(
                ErrorCodeEnum.AMOUNT_INVALID.getErrorCode(),
                ErrorCodeEnum.AMOUNT_INVALID.getErrorMessage(),
                HttpStatus.BAD_REQUEST
            );
        }
    }
}