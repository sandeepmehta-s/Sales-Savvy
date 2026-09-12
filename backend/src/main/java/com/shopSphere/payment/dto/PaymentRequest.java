package com.shopSphere.payment.dto;

public class PaymentRequest {
    private String username;
    private long amount;   // in paise (INR smallest unit)
    private String currency = "INR";

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public long getAmount() { return amount; }
    public void setAmount(long amount) { this.amount = amount; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
}