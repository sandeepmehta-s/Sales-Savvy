package com.salesSavvy.product.service;

import com.salesSavvy.product.entity.Product;
import java.math.BigDecimal;
import java.util.List;

public interface ProductService {
    String addProduct(Product product);
    Product getProductById(Long id);
    Product getProductByName(String name);
    List<Product> getProductsByCategory(String category);
    String updateProduct(Product product);
    String deleteProduct(Long id);
    List<Product> getAllProducts();
    List<Product> searchProducts(String keyword);
    List<Product> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice);
    List<Product> getProductsByCategoryAndPrice(String category, BigDecimal minPrice, BigDecimal maxPrice);
    List<String> getAllCategories();
}