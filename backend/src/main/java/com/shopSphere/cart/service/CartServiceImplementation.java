package com.shopSphere.cart.service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;

import com.shopSphere.cart.entity.Cart;
import com.shopSphere.cart.entity.CartItem;
import com.shopSphere.cart.repository.CartRepository;
import com.shopSphere.product.entity.Product;
import com.shopSphere.product.repository.ProductRepository;
import com.shopSphere.shared.exception.ResourceNotFoundException;

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

        // Objects.requireNonNull narrows type to @NonNull — satisfies JDT null checker
        Product product = productRepository.findById(Objects.requireNonNull(productId))
            .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + productId));

        Cart cart = cartRepository.findByUsername(username)
            .orElseGet(() -> cartRepository.save(new Cart(username)));

        String productName = Objects.toString(product.getName(), "");
        String pid = Objects.toString(product.getId(), "");
        CartItem newItem = new CartItem(pid, productName, product.getPrice(), product.getPhoto(), quantity);
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
            boolean found = cart.getCartItems().stream()
                .anyMatch(item -> productId.equals(item.getProductId()));
            if (found) {
                cart.updateCartItem(productId, quantity);
            } else {
                Product product = productRepository.findById(Objects.requireNonNull(productId))
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + productId));
                String productName = Objects.toString(product.getName(), "");
                String pid = Objects.toString(product.getId(), "");
                cart.addCartItem(new CartItem(pid, productName, product.getPrice(), product.getPhoto(), quantity));
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
        // Avoid Cart::getCartItems method-reference form — use explicit lambda
        // so JDT can verify the return type is @NonNull
        return cartRepository.findByUsername(username)
            .map(cart -> cart.getCartItems())
            .orElse(Collections.emptyList());
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
            BigDecimal price = item.getPrice() != null ? item.getPrice() : BigDecimal.ZERO;
            total = total.add(price.multiply(BigDecimal.valueOf(item.getQuantity())));
        }
        return total;
    }
}