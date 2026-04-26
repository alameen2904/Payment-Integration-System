package com.project.payments.service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.security.NoSuchAlgorithmException;
import java.security.InvalidKeyException;
import org.springframework.stereotype.Service;

@Service
public class HmacSha256Service {

    private static final String SECRET_KEY = "THIS_IS_MY_SECRET";
    private static final String ALGORITHM = "HmacSHA256";

    public String computeHmacSha256(String jsonInput) {
        try {
            //SecretKeySpec setup
            SecretKeySpec keySpec = new SecretKeySpec(
                    SECRET_KEY.getBytes(StandardCharsets.UTF_8),
                    ALGORITHM
            );

            //Mac instance initialization
            Mac mac = Mac.getInstance(ALGORITHM);
            mac.init(keySpec);

            //Signature generation
            byte[] signatureBytes = mac.doFinal(jsonInput.getBytes(StandardCharsets.UTF_8));

            //Base64 Encoding
            String hmacSignature = Base64.getEncoder().encodeToString(signatureBytes);

            
            System.out.println("HMAC-SHA256 Signature: " + hmacSignature);

            return hmacSignature;

        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Error computing HMAC signature", e);
        }
    }
}