package com.project.payments.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;

import java.lang.reflect.Field;
import java.util.Base64;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.payments.pojo.PaymentRequest;
import com.project.payments.service.data.TestDataBuilder;

import lombok.extern.slf4j.Slf4j;
@Slf4j
public class HmacSha256ServiceTest {

	@Test
	public void testComputeHmacSha256ProducesDeterministic32ByteHmac() {
        HmacSha256Service service = new HmacSha256Service(null);
        try {
        Field secretKeyField = HmacSha256Service.class.getDeclaredField("secretKey");
    	secretKeyField.setAccessible(true);
    	
    	secretKeyField.set(service, "THIS_IS_MY_SECRET");
    	log.info("Secretkey set");
        } catch (Exception e) {
			e.printStackTrace();
			
		}
    
        PaymentRequest requestObj=TestDataBuilder.buildPaymentRequest();
		ObjectMapper objectMapper = new ObjectMapper();
		String dummyJson=null;
		try {
			dummyJson = objectMapper.writeValueAsString(requestObj);
		} catch (Exception e) {
		e.printStackTrace();	
		}
		String signature1 = service.computeHmacSha256(dummyJson);


		assertNotNull(signature1, "Signature should not be null");


		byte[] decoded = Base64.getDecoder().decode(signature1);
		assertEquals(32, decoded.length, "HMAC-SHA256 signature should be 32 bytes long");


		String signature2 = service.computeHmacSha256(dummyJson);
		assertEquals(signature1, signature2, "Same input should produce the same signature");
	}
}