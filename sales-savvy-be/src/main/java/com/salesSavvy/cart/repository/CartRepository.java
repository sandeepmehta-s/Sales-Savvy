package com.salesSavvy.cart.repository;

import com.salesSavvy.cart.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUserId(Long userId);
    Optional<Cart> findByUserUsername(String username);
    Boolean existsByUserId(Long userId);
    void deleteByUserId(Long userId);
}