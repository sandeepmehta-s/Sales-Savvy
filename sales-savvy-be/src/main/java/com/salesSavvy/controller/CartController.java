package com.salesSavvy.controller;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.salesSavvy.dto.CartItemResponse;
import com.salesSavvy.dto.CartResponse;
import com.salesSavvy.entity.Cart;
import com.salesSavvy.entity.CartItem;
import com.salesSavvy.service.CartService;

@RestController
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/add")
    public ResponseEntity<String> addToCart(
            @RequestParam Long productId,
            @RequestParam int quantity,
            Principal principal) {
    	
    	 String username = principal.getName();
        cartService.addToCart(username, productId, quantity);
        return ResponseEntity.ok("Product added to cart successfully");
    }

    @PutMapping("/update")
    public ResponseEntity<String> updateCartItem(
            @RequestParam String username,
            @RequestParam Long productId,
            @RequestParam int quantity,
            Principal principal) {
        if (!principal.getName().equals(username)) {
            return ResponseEntity.status(403).build();
        }
        cartService.updateCartItem(username, productId, quantity);
        return ResponseEntity.ok("Cart updated successfully");
    }

    @DeleteMapping("/remove")
    public ResponseEntity<String> removeFromCart(
            @RequestParam String username,
            @RequestParam Long productId,
            Principal principal) {
        if (!principal.getName().equals(username)) {
            return ResponseEntity.status(403).build();
        }
        cartService.removeFromCart(username, productId);
        return ResponseEntity.ok("Product removed from cart successfully");
    }

    @DeleteMapping("/clear")
    public ResponseEntity<String> clearCart(@RequestParam String username, Principal principal) {
        if (!principal.getName().equals(username)) {
            return ResponseEntity.status(403).build();
        }
        cartService.clearCart(username);
        return ResponseEntity.ok("Cart cleared successfully");
    }

    @GetMapping("/items")
    public ResponseEntity<CartResponse> getCartItems(@RequestParam String username, Principal principal) {
        if (!principal.getName().equals(username)) {
            return ResponseEntity.status(403).build();
        }
        Cart cart = cartService.getCartByUsername(username);
        List<CartItem> cartItems = cartService.getCartItems(username);

        List<CartItemResponse> itemResponses = cartItems.stream()
            .map(item -> new CartItemResponse(
                item.getId(),
                item.getProduct().getId(),
                item.getProduct().getName(),
                item.getProduct().getPrice(),
                item.getQuantity(),
                item.getProduct().getPhoto()
            ))
            .collect(Collectors.toList());

        CartResponse response = new CartResponse(
            cart.getId(),
            username,
            itemResponses
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/count")
    public ResponseEntity<Integer> getCartItemCount(@RequestParam String username, Principal principal) {
        if (!principal.getName().equals(username)) {
            return ResponseEntity.status(403).build();
        }
        Integer count = cartService.getCartItemCount(username);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/total")
    public ResponseEntity<Double> getCartTotalValue(@RequestParam String username, Principal principal) {
        if (!principal.getName().equals(username)) {
            return ResponseEntity.status(403).build();
        }
        Double total = cartService.getCartTotalValue(username).doubleValue();
        return ResponseEntity.ok(total);
    }
}