package com.salesSavvy.cart.service;

import com.salesSavvy.cart.entity.Cart;
import com.salesSavvy.cart.entity.CartItem;
import java.math.BigDecimal;
import java.util.List;

public interface CartService {
    void addToCart(String username, String productId, int quantity);
    void updateCartItem(String username, String productId, int quantity);
    void removeFromCart(String username, String productId);
    void clearCart(String username);
    List<CartItem> getCartItems(String username);
    Cart getCartByUsername(String username);
    Integer getCartItemCount(String username);
    BigDecimal getCartTotalValue(String username);
}