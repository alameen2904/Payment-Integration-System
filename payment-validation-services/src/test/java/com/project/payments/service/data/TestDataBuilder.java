package com.project.payments.service.data;

import java.util.ArrayList;
import java.util.List;
import com.project.payments.pojo.LineItem;
import com.project.payments.pojo.Payment;
import com.project.payments.pojo.PaymentRequest;
import com.project.payments.pojo.User;

public class TestDataBuilder {

    public static PaymentRequest buildPaymentRequest() {

        // Create User
        User user = new User();
        user.setEndUserID("user123123"); // Matched with JSON
        user.setFirstname("John hello"); // Matched
        user.setLastname("Doe");
        user.setEmail("john.doe@example.com");
        user.setMobilePhone("+1234567890");

        // Line Item 1
        LineItem item1 = new LineItem();
        item1.setCurrency("EUR");
        item1.setProductName("Phone"); // Matched
        item1.setUnitAmount(200); // Matched (previously 2000000)
        item1.setQuantity(1);

        // Line Item 2
        LineItem item2 = new LineItem();
        item2.setCurrency("EUR");
        item2.setProductName("Headphones"); // Matched
        item2.setUnitAmount(500); // Matched (previously 50000)
        item2.setQuantity(2); // Matched

        List<LineItem> lineItems = new ArrayList<>();
        lineItems.add(item1);
        lineItems.add(item2);

        // Create Payment
        Payment payment = new Payment();
        payment.setCurrency("USD");
        payment.setAmount(100);
        payment.setBrandName("MyShop");
        payment.setLocale("en-US"); // Matched (en_US to en-US)
        payment.setCountry("US");
        payment.setMerchantTxnRef("TXN1234560001"); // Matched
        payment.setPaymentMethod("APM");
        payment.setProvider("STRIPE");
        payment.setPaymentType("SALE");
        payment.setSuccessUrl("https://example.com/success");
        payment.setCancelUrl("https://example.com/cancel");
        payment.setLineItems(lineItems);

        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setUser(user);
        paymentRequest.setPayment(payment);

        return paymentRequest;
    }
}