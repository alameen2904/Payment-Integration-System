package com.project.payments.pojo;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty; // Added Import
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Request payload to create a Stripe checkout session. "
		+ "Provide success and cancel URLs and at least one line item.")
public class CreatePaymentReq {
	
	@JsonProperty("successUrl") // Ensures correct JSON mapping
	@Schema(description = "URL to redirect the user after successful payment.", 
			example = "https://example.com/success")
	private String successUrl;
	
	@JsonProperty("cancelUrl") // Ensures correct JSON mapping
	@Schema(description = "URL to redirect the user if they cancel the payment.", 
			example = "https://example.com/cancel")
	private String cancelUrl;

	@Schema(description = "List of items to be charged. Each item must include currency, "
			+ "unitAmount (in smallest currency unit), quantity and productName.")
	List<LineItem> lineItems;
}