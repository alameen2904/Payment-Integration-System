package com.project.payments.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.project.payments.pojo.ErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        FieldError fieldError = ex.getBindingResult().getFieldErrors().get(0);
        String enumKey = fieldError.getDefaultMessage();
        ErrorCodeEnum errorCodeEnum = ErrorCodeEnum.valueOf(enumKey);
        ErrorResponse response = new ErrorResponse(
                errorCodeEnum.getErrorCode(),
                errorCodeEnum.getErrorMessage()
        );
        return ResponseEntity.badRequest().body(response);
    }
}