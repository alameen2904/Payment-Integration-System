package com.project.payments.service;

import org.junit.jupiter.api.Test;
import java.util.Base64; // Indha import mukkiyam
import static org.junit.jupiter.api.Assertions.*;

public class HmacSha256ServiceTest {

    @Test
    void computeHmacSha256_returnsNonNullAndExpectedLength() {
        HmacSha256Service service = new HmacSha256Service();
        String dummyJson = "{\"amount\":100,\"currency\":\"USD\"}";

        String signature1 = service.computeHmacSha256(dummyJson);

        // 1. Check if null
        assertNotNull(signature1, "Signature should not be null");

        // 2. Check length (SHA-256 binary is always 32 bytes)
        byte[] decoded = Base64.getDecoder().decode(signature1);
        assertEquals(32, decoded.length, "HMAC-SHA256 signature should be 32 bytes long");

        // 3. Determinism test: Same input = Same output
        String signature2 = service.computeHmacSha256(dummyJson);
        assertEquals(signature1, signature2, "Same input should produce the same signature");
    }
}