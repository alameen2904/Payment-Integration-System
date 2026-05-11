package com.project.payments.stripeprovider;

import lombok.Data;

@Data
public class SPPaymentResponse {

	private String stripeSessionId;

	private String hostedPageUrl;
}
