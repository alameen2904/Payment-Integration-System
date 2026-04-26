package com.project.payments.service;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.project.payments.constant.Constant;
import com.project.payments.constant.ErrorCodeEnum;
import com.project.payments.exception.PaymentValidationException;
import com.project.payments.pojo.PaymentRequest;
import com.project.payments.util.JsonUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class HmacSha256Service {
	
@Value("${hmac.secret-key}")
	private String secretKey ;
    private final JsonUtil jsonUtil;
	public String computeHmacSha256(String jsonInput) {
        log.info("Computing HMAC-SHA256 for input: {}", jsonInput);
        String secretKey = "THIS_IS_MY_SECRET";
        
        try {
            //SecretKeySpec setup
            SecretKeySpec keySpec = new SecretKeySpec(
                    secretKey.getBytes(StandardCharsets.UTF_8),
                   Constant.HMAC_SHA256
            );

            //Mac instance initialization
           
            Mac mac = Mac.getInstance(Constant.HMAC_SHA256);
            mac.init(keySpec);

            //Signature generation
            byte[] signatureBytes = mac.doFinal(jsonInput.getBytes(StandardCharsets.UTF_8));

            //Base64 Encoding
            String hmacSignature1 = Base64.getEncoder().encodeToString(signatureBytes);

            log.info("HMAC-SHA256 Signature: {}", hmacSignature1);

            //THE MISSING RETURN (This removes the red line)
            return hmacSignature1;

        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
        	log.error("Error computing HMAC-SHA256 signature", e);
           
            throw new PaymentValidationException(
            		ErrorCodeEnum.HMAC_COMPUTATION_ERROR.getErrorCode(),
            		ErrorCodeEnum.HMAC_COMPUTATION_ERROR.getErrorMessage(),
            		HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
	public String isHmacSignatureValid(PaymentRequest paymentRequest, String headerHmacSignature) {
		if(headerHmacSignature == null || headerHmacSignature.isEmpty()) {
			   log.error("Missing HMAC signature in request header.");
			   throw new PaymentValidationException(
					   ErrorCodeEnum.MISSING_HMAC.getErrorCode(),
					   ErrorCodeEnum.MISSING_HMAC.getErrorMessage(),
					   HttpStatus.UNAUTHORIZED
					   );
			   }
			String jsonString= jsonUtil.convertObjectToJson(paymentRequest);
		    
		   String calculatedHmac= computeHmacSha256(jsonString);
		   if (!calculatedHmac.equals(headerHmacSignature)) {
			   log.error("HMAC signature mismatch. Calculated: {}, Received:{}", calculatedHmac, headerHmacSignature);
			   throw new PaymentValidationException(
					   ErrorCodeEnum.INVALID_HMAC.getErrorCode(),
					   ErrorCodeEnum.INVALID_HMAC.getErrorMessage(),
					   HttpStatus.UNAUTHORIZED
					   );
		   }
		return calculatedHmac;
	}
}