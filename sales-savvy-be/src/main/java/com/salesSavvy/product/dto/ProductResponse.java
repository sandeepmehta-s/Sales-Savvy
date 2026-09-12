package com.salesSavvy.product.dto;

import java.math.BigDecimal;
import java.util.List;

public class ProductResponse {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private String photo;
    private String category;
    private List<String> reviews;
    private int reviewCount;

    public ProductResponse() {}

    public ProductResponse(Long id, String name, String description, BigDecimal price, 
                          String photo, String category, List<String> reviews) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.photo = photo;
        this.category = category;
        this.reviews = reviews;
        this.reviewCount = reviews != null ? reviews.size() : 0;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public String getPhoto() { return photo; }
    public void setPhoto(String photo) { this.photo = photo; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public List<String> getReviews() { return reviews; }
    public void setReviews(List<String> reviews) { this.reviews = reviews; }

    public int getReviewCount() { return reviewCount; }
    public void setReviewCount(int reviewCount) { this.reviewCount = reviewCount; }
}