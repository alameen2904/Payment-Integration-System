package com.project.payments.service.interfaces;

import com.project.payments.pojo.PaymentRequest;

public interface BusinessValidator {
	
	public void validate(PaymentRequest paymentRequest);

}
