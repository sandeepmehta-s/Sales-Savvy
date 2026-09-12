package com.shopSphere.cart.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "carts")
public class Cart {

    @Id
    private String id;

    @Indexed(unique = true)
    private String username;

    private List<CartItem> cartItems = new ArrayList<>();

    public Cart() {}

    public Cart(String username) { this.username = username; }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public List<CartItem> getCartItems() { return cartItems; }
    public void setCartItems(List<CartItem> cartItems) { this.cartItems = cartItems; }

    public void addCartItem(CartItem newItem) {
        for (CartItem existing : cartItems) {
            if (existing.getProductId().equals(newItem.getProductId())) {
                existing.setQuantity(existing.getQuantity() + newItem.getQuantity());
                return;
            }
        }
        cartItems.add(newItem);
    }

    public void updateCartItem(String productId, int quantity) {
        if (quantity == 0) {
            cartItems.removeIf(item -> item.getProductId().equals(productId));
        } else {
            cartItems.stream().filter(item -> item.getProductId().equals(productId))
                .findFirst().ifPresent(item -> item.setQuantity(quantity));
        }
    }

    public void clearCart() { cartItems.clear(); }
}