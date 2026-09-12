package com.salesSavvy.payment.dto;

import java.math.BigDecimal;

public class PaymentVerifyRequest {
    private String paymentIntentId;   // Stripe PaymentIntent ID (pi_xxx)
    private String paymentId;         // Stripe Charge/Payment ID
    private BigDecimal amount;

    public String getPaymentIntentId() { return paymentIntentId; }
    public void setPaymentIntentId(String paymentIntentId) { this.paymentIntentId = paymentIntentId; }
    public String getPaymentId() { return paymentId; }
    public void setPaymentId(String paymentId) { this.paymentId = paymentId; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
}