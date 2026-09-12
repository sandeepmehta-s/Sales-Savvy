package com.salesSavvy.cart.dto;

import java.util.List;

public class CartResponse {
    private String cartId;
    private String username;
    private List<CartItemResponse> items;
    private int itemCount;

    public CartResponse() {}

    public CartResponse(String cartId, String username, List<CartItemResponse> items) {
        this.cartId = cartId;
        this.username = username;
        this.items = items;
        this.itemCount = items != null ? items.size() : 0;
    }

    public String getCartId() { return cartId; }
    public void setCartId(String cartId) { this.cartId = cartId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public List<CartItemResponse> getItems() { return items; }
    public void setItems(List<CartItemResponse> items) { this.items = items; this.itemCount = items != null ? items.size() : 0; }
    public int getItemCount() { return itemCount; }
    public void setItemCount(int itemCount) { this.itemCount = itemCount; }
}