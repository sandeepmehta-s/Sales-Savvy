package com.salesSavvy.order.dto;

import java.math.BigDecimal;

public class OrderItemResponse {
    private String productId;
    private String productName;
    private String photo;
    private BigDecimal price;
    private int quantity;
    private BigDecimal subtotal;

    public OrderItemResponse() {}

    public OrderItemResponse(String productId, String productName, String photo, BigDecimal price, int quantity) {
        this.productId = productId;
        this.productName = productName;
        this.photo = photo;
        this.price = price;
        this.quantity = quantity;
        this.subtotal = price != null ? price.multiply(BigDecimal.valueOf(quantity)) : BigDecimal.ZERO;
    }

    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public String getPhoto() { return photo; }
    public void setPhoto(String photo) { this.photo = photo; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
}