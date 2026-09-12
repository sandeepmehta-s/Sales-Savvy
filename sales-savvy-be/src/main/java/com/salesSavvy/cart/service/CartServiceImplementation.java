package com.salesSavvy.cart.service;

import com.salesSavvy.cart.entity.Cart;
import com.salesSavvy.cart.entity.CartItem;
import com.salesSavvy.product.entity.Product;
import com.salesSavvy.shared.exception.ResourceNotFoundException;
import com.salesSavvy.cart.repository.CartRepository;
import com.salesSavvy.product.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class CartServiceImplementation implements CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

    public CartServiceImplementation(CartRepository cartRepository,
                                     ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
    }

    @Override
    public void addToCart(String username, String productId, int quantity) {
        if (quantity <= 0) throw new IllegalArgumentException("Quantity must be greater than 0");

        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + productId));

        Cart cart = cartRepository.findByUsername(username)
            .orElseGet(() -> cartRepository.save(new Cart(username)));

        CartItem newItem = new CartItem(
            product.getId(), product.getName(), product.getPrice(), product.getPhoto(), quantity
        );
        cart.addCartItem(newItem);
        cartRepository.save(cart);
    }

    @Override
    public void updateCartItem(String username, String productId, int quantity) {
        if (quantity < 0) throw new IllegalArgumentException("Quantity cannot be negative");

        Cart cart = cartRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user: " + username));

        if (quantity == 0) {
            cart.updateCartItem(productId, 0);
        } else {
            // Check if item exists; if not, add it
            boolean found = cart.getCartItems().stream()
                .anyMatch(item -> item.getProductId().equals(productId));
            if (found) {
                cart.updateCartItem(productId, quantity);
            } else {
                Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + productId));
                cart.addCartItem(new CartItem(product.getId(), product.getName(), product.getPrice(), product.getPhoto(), quantity));
            }
        }
        cartRepository.save(cart);
    }

    @Override
    public void removeFromCart(String username, String productId) {
        updateCartItem(username, productId, 0);
    }

    @Override
    public void clearCart(String username) {
        cartRepository.findByUsername(username).ifPresent(cart -> {
            cart.clearCart();
            cartRepository.save(cart);
        });
    }

    @Override
    public List<CartItem> getCartItems(String username) {
        Optional<Cart> cartOpt = cartRepository.findByUsername(username);
        return cartOpt.map(Cart::getCartItems).orElse(Collections.emptyList());
    }

    @Override
    public Cart getCartByUsername(String username) {
        return cartRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user: " + username));
    }

    @Override
    public Integer getCartItemCount(String username) {
        return cartRepository.findByUsername(username)
            .map(cart -> cart.getCartItems().size())
            .orElse(0);
    }

    @Override
    public BigDecimal getCartTotalValue(String username) {
        List<CartItem> items = getCartItems(username);
        BigDecimal total = BigDecimal.ZERO;
        for (CartItem item : items) {
            total = total.add(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        }
        return total;
    }
}