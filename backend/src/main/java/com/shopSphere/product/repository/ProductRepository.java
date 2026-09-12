package com.shopSphere.product.repository;

import com.shopSphere.product.entity.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ProductRepository extends MongoRepository<Product, String> {
    Optional<Product> findByName(String name);
    List<Product> findByCategory(String category);
    List<Product> findByNameContainingIgnoreCase(String keyword);
    boolean existsByName(String name);
    List<Product> findAllByOrderByNameAsc();
    @Query("{ 'price': { $gte: ?0, $lte: ?1 } }")
    List<Product> findByPriceBetween(BigDecimal min, BigDecimal max);
    @Query("{ 'category': ?0, 'price': { $gte: ?1, $lte: ?2 } }")
    List<Product> findByCategoryAndPriceBetween(String category, BigDecimal min, BigDecimal max);
}