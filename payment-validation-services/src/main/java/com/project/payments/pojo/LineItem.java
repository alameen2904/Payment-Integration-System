package com.project.payments.pojo;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import lombok.Data;

@Data
public class LineItem {

	@NotBlank(message = "CURRENCY_BLANK")
    @Pattern(
        regexp = "^[A-Z]{3}$",
        message = "CURRENCY_INVALID" // Corrected to match Enum CURRENCY_INVALID
    
    )
    private String currency;

    @NotBlank(message = "PRODUCT_NAME_BLANK")
    private String productName;

    @NotNull(message = "UNIT_AMOUNT_NULL")
    @Min(value = 1, message = "UNIT_AMOUNT_INVALID")
    private Integer unitAmount;

    @NotNull(message = "QUANTITY_NULL")
    @Min(value = 1, message = "QUANTITY_INVALID")
    private Integer quantity;
}