package com.salesSavvy.product.repository;

import com.salesSavvy.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByName(String name);
    List<Product> findByCategory(String category);
    List<Product> findByCategoryOrderByPriceAsc(String category);
    List<Product> findByCategoryOrderByPriceDesc(String category);
    List<Product> findByPriceBetween(java.math.BigDecimal minPrice, java.math.BigDecimal maxPrice);
    List<Product> findByNameContainingIgnoreCase(String name);
    List<Product> findByCategoryAndPriceBetween(String category, java.math.BigDecimal minPrice, java.math.BigDecimal maxPrice);
    Boolean existsByName(String name);
    List<Product> findAllByOrderByPriceAsc();
    List<Product> findAllByOrderByPriceDesc();
}