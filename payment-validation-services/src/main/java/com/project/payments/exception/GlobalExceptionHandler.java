package com.project.payments.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.project.payments.pojo.ErrorResponse;
import org.springframework.validation.FieldError;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {

        // 1. Extract the specific field error
        FieldError fieldError = ex.getBindingResult()
                                  .getFieldErrors()
                                  .get(0);

        // 2. Get the message string (e.g., "CURRENCY_INVALID")
        String enumKey = fieldError.getDefaultMessage();

        // 3. Convert String to Enum constant
        ErrorCodeEnum errorCodeEnum = ErrorCodeEnum.valueOf(enumKey);

        // 4. Build and return the clean response
        ErrorResponse response = new ErrorResponse(
                errorCodeEnum.getCode(),
                errorCodeEnum.getMessage()
        );

        return ResponseEntity.badRequest().body(response);
    }
}