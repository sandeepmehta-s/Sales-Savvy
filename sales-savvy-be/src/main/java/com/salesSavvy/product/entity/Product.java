package com.salesSavvy.product.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Product name cannot be blank")
    @Size(min = 3, max = 50, message = "Product name must be between 3 and 50 characters")
    @Column(unique = true)
    private String name;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    @Min(value = 1, message = "Price must be greater than 0")
    private BigDecimal price;

    private String photo;

    @NotBlank(message = "Category cannot be blank")
    private String category;

    // Bug Fix #1: Stock quantity tracking — prevents overselling on concurrent orders
    @Min(value = 0, message = "Stock quantity cannot be negative")
    @Column(name = "stock_quantity", nullable = false)
    private int stockQuantity = 0;

    // Bug Fix #1: Optimistic locking — prevents race condition when 2 users order same item simultaneously
    @Version
    @Column(name = "version")
    private Long version;

    @ElementCollection
    @CollectionTable(name = "product_reviews", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "review")
    private List<String> reviews = new ArrayList<>();

    public Product() {}

    public Product(String name, String description, BigDecimal price, String category) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
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

    public int getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(int stockQuantity) { this.stockQuantity = stockQuantity; }

    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }

    public List<String> getReviews() { return reviews; }
    public void setReviews(List<String> reviews) { this.reviews = reviews; }

    // Helper: check if enough stock available
    public boolean hasStock(int requiredQuantity) {
        return this.stockQuantity >= requiredQuantity;
    }

    // Helper: deduct stock (call inside transaction)
    public void deductStock(int quantity) {
        if (!hasStock(quantity)) {
            throw new IllegalStateException(
                "Insufficient stock for product: " + this.name +
                ". Available: " + this.stockQuantity + ", Required: " + quantity
            );
        }
        this.stockQuantity -= quantity;
    }
}