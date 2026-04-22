package com.project.payments.pojo;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class PaymentRequest {

	@NotBlank(message = "SUCCESS_URL_BLANK")
    @Pattern(
        regexp = "^(http|https)://.*$",
        message = "SUCCESS_URL_INVALID" // Corrected to match Enum SUCCESS_URL_INVALID
    )
    private String successUrl;

    @NotBlank(message = "CANCEL_URL_BLANK")
    @Pattern(
        regexp = "^(http|https)://.*$",
        message = "CANCEL_URL_INVALID" // Corrected to match Enum CANCEL_URL_INVALID
    )
    private String cancelUrl;

    @NotEmpty(message = "LINE_ITEMS_EMPTY")
    @Valid
    private List<LineItem> lineItems;
}