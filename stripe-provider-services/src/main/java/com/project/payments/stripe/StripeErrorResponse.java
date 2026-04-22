package com.project.payments.stripe;

import com.project.payments.stripe.StripeError;

import lombok.Data;

@Data
public class StripeErrorResponse {
	
	private StripeError error;

}
