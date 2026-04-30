package com.project.payments.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.project.payments.constant.ErrorCodeEnum;
import com.project.payments.pojo.ErrorResponse; 
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        log.error("Validation error occurred: {}", ex.getMessage());

        
        FieldError fieldError = ex.getBindingResult().getFieldErrors().get(0);
        String enumKey = fieldError.getDefaultMessage();

         ErrorCodeEnum errorCodeEnum;
        try {
            errorCodeEnum = ErrorCodeEnum.valueOf(enumKey);
        } catch (Exception e) {
            errorCodeEnum = ErrorCodeEnum.GENERIC_ERROR;
        }

        ErrorResponse response = new ErrorResponse(
                errorCodeEnum.getErrorCode(),
                errorCodeEnum.getErrorMessage()
        );

        log.error("Validation error response: {}", response);
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(PaymentValidationException.class)
    public ResponseEntity<ErrorResponse> handlePaymentValidationException(PaymentValidationException ex) {
        log.error("PaymentValidationException caught: {}", ex.toString());

        HttpStatus status = ex.getHttpStatus() != null ? ex.getHttpStatus() : HttpStatus.BAD_REQUEST;

        ErrorResponse body = new ErrorResponse(
                ex.getErrorCode(),
                ex.getErrorMessage()
        );

        log.error("Returning payment validation error: status={}, body={}", status, body);
        return new ResponseEntity<>(body, status);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        log.error("Unexpected exception caught: ", ex);

        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

    
        ErrorResponse body = new ErrorResponse(
                ErrorCodeEnum.GENERIC_ERROR.getErrorCode(),
                ErrorCodeEnum.GENERIC_ERROR.getErrorMessage()
        );

        log.error("Returning generic error response: status={}, body={}", status, body);
        return new ResponseEntity<>(body, status);
    }
}