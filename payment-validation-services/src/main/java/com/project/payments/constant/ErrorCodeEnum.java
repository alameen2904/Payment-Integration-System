package com.project.payments.constant;

import lombok.Getter;

@Getter
public enum ErrorCodeEnum {

	GENERIC_ERROR("10000", "An unexpected error occurred, please try again later"),
	USER_NULL("10001", "user must not be null"),
	PAYMENT_NULL("10002", "payment must not be null"),
	END_USER_ID_BLANK("10003", "endUserID must not be blank"),
	END_USER_ID_TOO_LONG("10004", "endUserID must not exceed 64 characters"),
	FIRSTNAME_BLANK("10005", "firstname must not be blank"),
	FIRSTNAME_TOO_LONG("10006", "firstname must not exceed 50 characters"),
	LASTNAME_BLANK("10007", "lastname must not be blank"),
	LASTNAME_TOO_LONG("10008", "lastname must not exceed 50 characters"),
	EMAIL_BLANK("10009", "email must not be blank"),
	EMAIL_INVALID("10010", "email must be valid"),
	MOBILE_PHONE_BLANK("10011", "mobilePhone must not be blank"),
	MOBILE_PHONE_INVALID("10012", "mobilePhone must be a valid international number"),
	CURRENCY_BLANK("10013", "currency must not be blank"),
	CURRENCY_INVALID("10014", "currency must be 3-letter ISO code"),
	AMOUNT_NULL("10015", "amount must not be null"),
	AMOUNT_INVALID("10016", "amount must be greater than 0"),
	BRAND_NAME_BLANK("10017", "brandName must not be blank"),
	BRAND_NAME_TOO_LONG("10018", "brandName must not exceed 100 characters"),
	LOCALE_BLANK("10019", "locale must not be blank"),
	LOCALE_INVALID("10020", "locale must be valid format (example: en-US)"),
	COUNTRY_BLANK("10021", "country must not be blank"),
	COUNTRY_INVALID("10022", "country must be 2-letter ISO code"),
	MERCHANT_TXN_REF_BLANK("10023", "merchantTxnRef must not be blank"),
	MERCHANT_TXN_REF_TOO_LONG("10024", "merchantTxnRef must not exceed 100 characters"),
	PAYMENT_METHOD_BLANK("10025", "paymentMethod must not be blank"),
	PROVIDER_BLANK("10026", "provider must not be blank"),
	PAYMENT_TYPE_BLANK("10027", "paymentType must not be blank"),
	SUCCESS_URL_BLANK("10028", "successUrl must not be blank"),
	SUCCESS_URL_INVALID("10029", "successUrl must be valid HTTP/HTTPS URL"),
	CANCEL_URL_BLANK("10030", "cancelUrl must not be blank"),
	CANCEL_URL_INVALID("10031", "cancelUrl must be valid HTTP/HTTPS URL"),
	LINE_ITEMS_EMPTY("10032", "lineItems must not be empty"),
	PRODUCT_NAME_BLANK("10033", "productName must not be blank"),
	PRODUCT_NAME_TOO_LONG("10034", "productName must not exceed 100 characters"),
	UNIT_AMOUNT_NULL("10035", "unitAmount must not be null"),
	UNIT_AMOUNT_INVALID("10036", "unitAmount must be greater than 0"),
	QUANTITY_NULL("10037", "quantity must not be null"),
	QUANTITY_INVALID("10038", "quantity must be greater than 0"),
	FIRSTNAME_CONTAINS_HELLO("10039", "firstname must not contain the word 'hello'");
	private final String errorCode;
	private final String errorMessage;

	ErrorCodeEnum(String errorCode, String errorMessage) {
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}
}