package com.project.payments.repository.interfaces;

import com.project.payments.entity.MerchantPaymentRequestEntity;

public interface MerchantPaymentRequestRepository {
   
   public int saveMerchantPaymentRequest(MerchantPaymentRequestEntity merchantPaymentRequestEntity);
public int countRequestsForUserInLastMinutes(String endUserId, int minutes);
}