package com.project.payments;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient; 

@SpringBootApplication
@EnableDiscoveryClient
public class PaymentValidationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PaymentValidationServiceApplication.class, args);
    }

    
    
}