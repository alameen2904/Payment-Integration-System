package com.project.payments.exception;

import lombok.Getter;

@Getter
public enum ErrorCodeEnum {

    SUCCESS_URL_BLANK("10001", "successUrl must not be blank"),
    SUCCESS_URL_INVALID("10002", "successUrl must be a valid HTTP/HTTPS URL"),

    CANCEL_URL_BLANK("10003", "cancelUrl must not be blank"),
    CANCEL_URL_INVALID("10004", "cancelUrl must be a valid HTTP/HTTPS URL"),

    LINE_ITEMS_EMPTY("10005", "lineItems must not be empty"),

    CURRENCY_BLANK("10006", "currency must not be blank"),
    CURRENCY_INVALID("10007", "currency must be a valid 3-letter ISO code"),

    PRODUCT_NAME_BLANK("10008", "productName must not be blank"),
    PRODUCT_NAME_TOO_LONG("10009", "productName must not exceed 100 characters"),

    UNIT_AMOUNT_NULL("10010", "unitAmount must not be null"),
    UNIT_AMOUNT_INVALID("10011", "unitAmount must be greater than 0"),

    QUANTITY_NULL("10012", "quantity must not be null"),
    QUANTITY_INVALID("10013", "quantity must be at least 1");

    private final String code;
    private final String message;

    ErrorCodeEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
