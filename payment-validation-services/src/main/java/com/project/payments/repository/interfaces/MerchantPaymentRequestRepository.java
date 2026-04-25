package com.project.payments.repository.interfaces;

import com.project.payments.entity.MerchantPaymentRequestEntity;

public interface MerchantPaymentRequestRepository {
   
    int saveMerchantPaymentRequest(MerchantPaymentRequestEntity merchantPaymentRequestEntity);
}