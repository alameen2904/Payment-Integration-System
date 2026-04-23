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
public class ValidatorRule2 implements BusinessValidator {

    @Override
    public void validate(PaymentRequest paymentRequest) {
        log.info("Executing ValidatorRule2 for paymentRequest: {}", paymentRequest);

        String lastName = paymentRequest.getUser().getLastname();

        if (lastName != null && lastName.toLowerCase().contains("test")) {
            log.warn("Validation failed: lastName contains forbidden word 'test'");
            throw new PaymentValidationException(
                ErrorCodeEnum.LASTNAME_TOO_LONG.getErrorCode(), // Placeholder or add new Enum
                "Lastname contains forbidden content",
                HttpStatus.BAD_REQUEST
            );
        }
    }
}