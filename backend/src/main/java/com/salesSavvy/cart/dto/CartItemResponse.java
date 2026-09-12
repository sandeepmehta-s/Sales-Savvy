package com.salesSavvy.cart.dto;

import java.math.BigDecimal;

public class CartItemResponse {
    private String productId;
    private String productName;
    private BigDecimal price;
    private int quantity;
    private BigDecimal subtotal;
    private String photo;

    public CartItemResponse() {}

    public CartItemResponse(String productId, String productName, BigDecimal price, int quantity, String photo) {
        this.productId = productId;
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
        this.subtotal = price != null ? price.multiply(BigDecimal.valueOf(quantity)) : BigDecimal.ZERO;
        this.photo = photo;
    }

    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
    public String getPhoto() { return photo; }
    public void setPhoto(String photo) { this.photo = photo; }
}