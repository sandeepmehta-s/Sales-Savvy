package com.salesSavvy.cart.repository;

import com.salesSavvy.cart.entity.Cart;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface CartRepository extends MongoRepository<Cart, String> {
    Optional<Cart> findByUsername(String username);
    void deleteByUsername(String username);
}