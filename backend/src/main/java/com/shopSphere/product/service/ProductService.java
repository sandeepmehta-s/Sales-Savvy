package com.shopSphere.product.service;

import com.shopSphere.product.entity.Product;
import java.math.BigDecimal;
import java.util.List;

public interface ProductService {
    String addProduct(Product product);
    Product getProductById(String id);
    Product getProductByName(String name);
    List<Product> getProductsByCategory(String category);
    String updateProduct(Product product);
    String deleteProduct(String id);
    List<Product> getAllProducts();
    List<Product> searchProducts(String keyword);
    List<Product> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice);
    List<Product> getProductsByCategoryAndPrice(String category, BigDecimal minPrice, BigDecimal maxPrice);
    List<String> getAllCategories();
}