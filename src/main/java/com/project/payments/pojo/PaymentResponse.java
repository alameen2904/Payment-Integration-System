package com.project.payments.pojo;

import lombok.Data;

@Data
public class PaymentResponse {
	private String stripeSessionId;
	private String hostedPageUrl;

}
