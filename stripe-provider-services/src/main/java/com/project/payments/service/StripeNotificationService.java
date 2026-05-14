package com.project.payments.service;

public interface StripeNotificationService {
    void processNotification(String stripeSignature, String jsonRequest);
}