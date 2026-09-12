package com.salesSavvy.product.repository;

import com.salesSavvy.product.entity.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
import java.util.Optional;

public interface ProductRepository extends MongoRepository<Product, String> {
    Optional<Product> findByName(String name);
    List<Product> findByCategory(String category);
    List<Product> findByNameContainingIgnoreCase(String keyword);
    boolean existsByName(String name);
    List<Product> findAllByOrderByNameAsc();
}