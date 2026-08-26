package com.project.payments.service.impl.businessvalidators;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import com.project.payments.constant.ErrorCodeEnum;
import com.project.payments.entity.MerchantPaymentRequestEntity;
import com.project.payments.exception.PaymentValidationException;
import com.project.payments.pojo.PaymentRequest;
import com.project.payments.repository.interfaces.MerchantPaymentRequestRepository;
import com.project.payments.service.data.TestDataBuilder;
import com.project.payments.util.JsonUtil;

/**
 * Unit tests for {@link DuplicateTxnValidator}.
 *
 * The validator relies on the repository returning -1 to signal that a
 * duplicate (endUserID + merchantTxnReference) combination already exists,
 * enforced at the database layer via a uniqueness constraint. These tests
 * verify both the happy path and the duplicate-rejection path, and confirm
 * the correct fields are persisted for the duplicate check to work.
 */
@ExtendWith(MockitoExtension.class)
class DuplicateTxnValidatorTest {

    @Mock
    private MerchantPaymentRequestRepository repository;

    @Mock
    private JsonUtil jsonUtil;

    private DuplicateTxnValidator validator;

    @BeforeEach
    void setUp() {
        validator = new DuplicateTxnValidator(repository, jsonUtil);
    }

    @Test
    void validate_savesRequestSuccessfully_whenTransactionIsNotADuplicate() {
        PaymentRequest paymentRequest = TestDataBuilder.buildPaymentRequest();
        when(jsonUtil.convertObjectToJson(paymentRequest)).thenReturn("{\"serialized\":\"request\"}");
        when(repository.saveMerchantPaymentRequest(any(MerchantPaymentRequestEntity.class)))
                .thenReturn(1);

        assertDoesNotThrow(() -> validator.validate(paymentRequest));

        verify(repository).saveMerchantPaymentRequest(any(MerchantPaymentRequestEntity.class));
    }

    @Test
    void validate_throwsDuplicateTransactionException_whenRepositoryReturnsMinusOne() {
        PaymentRequest paymentRequest = TestDataBuilder.buildPaymentRequest();
        when(jsonUtil.convertObjectToJson(paymentRequest)).thenReturn("{\"serialized\":\"request\"}");
        when(repository.saveMerchantPaymentRequest(any(MerchantPaymentRequestEntity.class)))
                .thenReturn(-1);

        PaymentValidationException exception = assertThrows(
                PaymentValidationException.class,
                () -> validator.validate(paymentRequest));

        assertEquals(ErrorCodeEnum.DUPLICATE_TRANSACTION.getErrorCode(), exception.getErrorCode());
        assertEquals(ErrorCodeEnum.DUPLICATE_TRANSACTION.getErrorMessage(), exception.getErrorMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
    }

    @Test
    void validate_persistsCorrectEndUserIdAndMerchantTxnReference() {
        PaymentRequest paymentRequest = TestDataBuilder.buildPaymentRequest();
        when(jsonUtil.convertObjectToJson(paymentRequest)).thenReturn("{\"serialized\":\"request\"}");
        when(repository.saveMerchantPaymentRequest(any(MerchantPaymentRequestEntity.class)))
                .thenReturn(1);

        validator.validate(paymentRequest);

        ArgumentCaptor<MerchantPaymentRequestEntity> captor =
                ArgumentCaptor.forClass(MerchantPaymentRequestEntity.class);
        verify(repository).saveMerchantPaymentRequest(captor.capture());

        MerchantPaymentRequestEntity savedEntity = captor.getValue();
        assertEquals(paymentRequest.getUser().getEndUserID(), savedEntity.getEndUserID());
        assertEquals(paymentRequest.getPayment().getMerchantTxnRef(), savedEntity.getMerchantTxnReference());
        assertEquals("{\"serialized\":\"request\"}", savedEntity.getTransactionRequest());
    }
}
