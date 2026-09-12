package com.salesSavvy.payment.service;

import java.util.Map;

public interface PaymentService {
    Map<String, Object> createPaymentIntent(long amountPaise, String currency) throws Exception;
    boolean verifyWebhookSignature(String payload, String sigHeader) throws Exception;
    String getPublishableKey();
}