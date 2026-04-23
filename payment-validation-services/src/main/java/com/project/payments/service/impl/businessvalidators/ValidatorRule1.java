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
public class ValidatorRule1 implements BusinessValidator {

    @Override
    public void validate(PaymentRequest paymentRequest) {
        log.info("Executing ValidatorRule1 for paymentRequest: {}", paymentRequest);

        String firstName = paymentRequest.getUser().getFirstname();

        if (firstName != null && firstName.toLowerCase().contains("hello")) {
            log.warn("Validation failed: firstName contains 'hello'");
            throw new PaymentValidationException(
                ErrorCodeEnum.FIRSTNAME_CONTAINS_HELLO.getErrorCode(),
                ErrorCodeEnum.FIRSTNAME_CONTAINS_HELLO.getErrorMessage(),
                HttpStatus.BAD_REQUEST
            );
        }
    }
}