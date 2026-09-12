package com.salesSavvy.cart.repository;

import com.salesSavvy.cart.entity.Cart;
import com.salesSavvy.cart.entity.CartItem;
import com.salesSavvy.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByCart(Cart cart);
    List<CartItem> findByProduct(Product product);
    Optional<CartItem> findByCartAndProduct(Cart cart, Product product);
    void deleteByCart(Cart cart);
    void deleteByCartAndProduct(Cart cart, Product product);
    Integer countByCart(Cart cart);
}