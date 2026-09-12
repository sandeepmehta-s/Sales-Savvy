package com.salesSavvy.cart.dto;

import java.math.BigDecimal;
import java.util.List;

public class CartResponse {
    private Long cartId;
    private String username;
    private List<CartItemResponse> items;
    private BigDecimal totalPrice;
    private int totalItems;

    public CartResponse() {}

    public CartResponse(Long cartId, String username, List<CartItemResponse> items) {
        this.cartId = cartId;
        this.username = username;
        this.items = items;
        this.totalPrice = calculateTotalPrice(items);
        this.totalItems = calculateTotalItems(items);
    }

    // Getters and Setters
    public Long getCartId() { return cartId; }
    public void setCartId(Long cartId) { this.cartId = cartId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public List<CartItemResponse> getItems() { return items; }
    public void setItems(List<CartItemResponse> items) { 
        this.items = items;
        this.totalPrice = calculateTotalPrice(items);
        this.totalItems = calculateTotalItems(items);
    }

    public BigDecimal getTotalPrice() { return totalPrice; }
    public void setTotalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; }

    public int getTotalItems() { return totalItems; }
    public void setTotalItems(int totalItems) { this.totalItems = totalItems; }

    // Helper methods
    private BigDecimal calculateTotalPrice(List<CartItemResponse> items) {
        return items.stream()
            .map(CartItemResponse::getSubtotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private int calculateTotalItems(List<CartItemResponse> items) {
        return items.stream()
            .mapToInt(CartItemResponse::getQuantity)
            .sum();
    }
}