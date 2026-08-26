package com.project.payments.service.impl.businessvalidators;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import com.project.payments.cache.ValidatorRuleCache;
import com.project.payments.constant.ErrorCodeEnum;
import com.project.payments.constant.ValidatorRuleEnum;
import com.project.payments.exception.PaymentValidationException;
import com.project.payments.pojo.PaymentRequest;
import com.project.payments.repository.interfaces.MerchantPaymentRequestRepository;
import com.project.payments.service.data.TestDataBuilder;

/**
 * Unit tests for {@link PaymentAttemptThresholdValidator}.
 *
 * The threshold and time window are configurable at runtime (loaded from the
 * validator rule cache rather than hardcoded), so these tests exercise the
 * comparison logic against mocked rule parameters instead of a real cache.
 */
@ExtendWith(MockitoExtension.class)
class PaymentAttemptThresholdValidatorTest {

    private static final String DURATION_IN_MINS = "10";
    private static final String MAX_THRESHOLD = "3";

    @Mock
    private MerchantPaymentRequestRepository merchantReqRepo;

    @Mock
    private ValidatorRuleCache validatorRuleCache;

    private PaymentAttemptThresholdValidator validator;

    @BeforeEach
    void setUp() {
        validator = new PaymentAttemptThresholdValidator(merchantReqRepo, validatorRuleCache);
    }

    private void mockRuleParams(String durationInMins, String maxThreshold) {
        Map<String, String> params = new HashMap<>();
        params.put("durationInMins", durationInMins);
        params.put("maxPaymentThreshold", maxThreshold);
        when(validatorRuleCache.getValidatorParamsForRule(
                ValidatorRuleEnum.PAYMENT_ATTEMPT_THRESHOLD_RULE.getRuleName()))
                .thenReturn(params);
    }

    @Test
    void validate_allowsRequest_whenAttemptCountIsBelowThreshold() {
        PaymentRequest paymentRequest = TestDataBuilder.buildPaymentRequest();
        mockRuleParams(DURATION_IN_MINS, MAX_THRESHOLD);
        when(merchantReqRepo.countRequestsForUserInLastMinutes(
                paymentRequest.getUser().getEndUserID(), 10))
                .thenReturn(1);

        assertDoesNotThrow(() -> validator.validate(paymentRequest));
    }

    @Test
    void validate_allowsRequest_whenAttemptCountEqualsThreshold() {
        // Current implementation treats count == threshold as still allowed (count <= max).
        PaymentRequest paymentRequest = TestDataBuilder.buildPaymentRequest();
        mockRuleParams(DURATION_IN_MINS, MAX_THRESHOLD);
        when(merchantReqRepo.countRequestsForUserInLastMinutes(
                paymentRequest.getUser().getEndUserID(), 10))
                .thenReturn(3);

        assertDoesNotThrow(() -> validator.validate(paymentRequest));
    }

    @Test
    void validate_throwsThresholdExceededException_whenAttemptCountExceedsThreshold() {
        PaymentRequest paymentRequest = TestDataBuilder.buildPaymentRequest();
        mockRuleParams(DURATION_IN_MINS, MAX_THRESHOLD);
        when(merchantReqRepo.countRequestsForUserInLastMinutes(
                paymentRequest.getUser().getEndUserID(), 10))
                .thenReturn(4);

        PaymentValidationException exception = assertThrows(
                PaymentValidationException.class,
                () -> validator.validate(paymentRequest));

        assertEquals(
                ErrorCodeEnum.PAYMENT_ATTEMPT_THRESHOLD_EXCEEDED.getErrorCode(),
                exception.getErrorCode());
        assertEquals(
                ErrorCodeEnum.PAYMENT_ATTEMPT_THRESHOLD_EXCEEDED.getErrorMessage(),
                exception.getErrorMessage());
        assertEquals(HttpStatus.TOO_MANY_REQUESTS, exception.getHttpStatus());
    }

    @Test
    void validate_usesConfiguredDurationWindow_whenCountingAttempts() {
        PaymentRequest paymentRequest = TestDataBuilder.buildPaymentRequest();
        mockRuleParams("30", MAX_THRESHOLD);
        when(merchantReqRepo.countRequestsForUserInLastMinutes(
                paymentRequest.getUser().getEndUserID(), 30))
                .thenReturn(0);

        assertDoesNotThrow(() -> validator.validate(paymentRequest));
    }
}
