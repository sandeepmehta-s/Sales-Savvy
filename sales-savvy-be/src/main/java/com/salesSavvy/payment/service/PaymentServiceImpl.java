package com.salesSavvy.payment.service;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Value("${razorpay.key.id}")
    private String keyId;

    @Value("${razorpay.key.secret}")
    private String keySecret;

    @Override
    public Order createRazorpayOrder(int amountPaise) throws RazorpayException {
        RazorpayClient client = new RazorpayClient(keyId, keySecret);

        JSONObject request = new JSONObject();
        request.put("amount", amountPaise);
        request.put("currency", "INR");
        request.put("receipt", "rcpt_" + System.currentTimeMillis());
        request.put("payment_capture", 1);

        return client.orders.create(request);
    }

    @Override
    public boolean verifySignature(String orderId, String paymentId, String signature) {
        JSONObject payload = new JSONObject();
        payload.put("razorpay_order_id", orderId);
        payload.put("razorpay_payment_id", paymentId);
        payload.put("razorpay_signature", signature);

        try {
            Utils.verifyPaymentSignature(payload, keySecret);
            return true;
        } catch (RazorpayException e) {
            return false;
        }
    }

    @Override
    public String getKeyId() {
        return keyId;
    }
}