package com.project.payments.exception;



import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.project.payments.pojo.ErrorResponse;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

	@ExceptionHandler(StripeProviderException.class)
	public ResponseEntity<ErrorResponse> handleStripeProviderException(StripeProviderException ex) {
		log.error("StripeProviderException caught: {}", ex.toString());

		HttpStatus status = ex.getHttpStatus();
		ErrorResponse body = new ErrorResponse();
		body.setErrorCode(ex.getErrorCode());
		body.setErrorMessage(ex.getErrorMessage()!=null?ex.getErrorMessage():ex.getMessage());

		log.error("Returning error response: status={}, body={}", status, body);
		return new ResponseEntity<>(body, status);

	}
}
