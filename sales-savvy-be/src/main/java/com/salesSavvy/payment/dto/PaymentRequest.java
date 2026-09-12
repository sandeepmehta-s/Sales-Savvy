package com.salesSavvy.payment.dto;

public class PaymentRequest {
    private String username;
    private int amount; // in paise

    public PaymentRequest() {}

    public PaymentRequest(String username, int amount) {
        this.username = username;
        this.amount = amount;
    }

    // Getters and Setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public int getAmount() { return amount; }
    public void setAmount(int amount) { this.amount = amount; }
}