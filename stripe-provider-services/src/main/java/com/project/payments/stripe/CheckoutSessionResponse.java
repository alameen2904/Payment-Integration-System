package com.project.payments.stripe;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true) // Crucial: ignores fields we didn't define
public class CheckoutSessionResponse {
    private String id;
    private String url;
    
    @JsonProperty("success_url")
    private String successUrl;
    
    @JsonProperty("cancel_url")
    private String cancelUrl;
    
    private String mode;
    private String status;
}