package com.salesSavvy.payment.service;

import com.razorpay.Order;
import com.razorpay.RazorpayException;

public interface PaymentService {
    Order createRazorpayOrder(int amountPaise) throws RazorpayException;
    boolean verifySignature(String orderId, String paymentId, String signature);
    String getKeyId();
}