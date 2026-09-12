package com.shopSphere.cart.entity;

import jakarta.validation.constraints.Min;
import java.math.BigDecimal;

public class CartItem {

    private String productId;
    private String productName;
    private BigDecimal price;
    private String photoUrl;

    @Min(value = 1, message = "Quantity must be at least 1")
    private int quantity;

    public CartItem() {}

    public CartItem(String productId, String productName, BigDecimal price, String photoUrl, int quantity) {
        this.productId = productId;
        this.productName = productName;
        this.price = price;
        this.photoUrl = photoUrl;
        this.quantity = quantity;
    }

    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}