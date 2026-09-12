package com.shopSphere.cart.controller;

import com.shopSphere.cart.dto.CartItemResponse;
import com.shopSphere.cart.dto.CartResponse;
import com.shopSphere.cart.entity.Cart;
import com.shopSphere.cart.entity.CartItem;
import com.shopSphere.cart.service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/add")
    public ResponseEntity<String> addToCart(
            @RequestParam String productId,
            @RequestParam int quantity,
            Principal principal) {
        cartService.addToCart(principal.getName(), productId, quantity);
        return ResponseEntity.ok("Product added to cart successfully");
    }

    @PutMapping("/update")
    public ResponseEntity<String> updateCartItem(
            @RequestParam String username,
            @RequestParam String productId,
            @RequestParam int quantity,
            Principal principal) {
        if (!principal.getName().equals(username)) return ResponseEntity.status(403).build();
        cartService.updateCartItem(username, productId, quantity);
        return ResponseEntity.ok("Cart updated successfully");
    }

    @DeleteMapping("/remove")
    public ResponseEntity<String> removeFromCart(
            @RequestParam String username,
            @RequestParam String productId,
            Principal principal) {
        if (!principal.getName().equals(username)) return ResponseEntity.status(403).build();
        cartService.removeFromCart(username, productId);
        return ResponseEntity.ok("Product removed from cart successfully");
    }

    @DeleteMapping("/clear")
    public ResponseEntity<String> clearCart(@RequestParam String username, Principal principal) {
        if (!principal.getName().equals(username)) return ResponseEntity.status(403).build();
        cartService.clearCart(username);
        return ResponseEntity.ok("Cart cleared successfully");
    }

    @GetMapping("/items")
    public ResponseEntity<CartResponse> getCartItems(@RequestParam String username, Principal principal) {
        if (!principal.getName().equals(username)) return ResponseEntity.status(403).build();

        Cart cart = cartService.getCartByUsername(username);
        List<CartItem> cartItems = cartService.getCartItems(username);

        List<CartItemResponse> itemResponses = cartItems.stream()
            .map(item -> new CartItemResponse(
                item.getProductId(),
                item.getProductName(),
                item.getPrice(),
                item.getQuantity(),
                item.getPhotoUrl()
            ))
            .collect(Collectors.toList());

        return ResponseEntity.ok(new CartResponse(cart.getId(), username, itemResponses));
    }

    @GetMapping("/count")
    public ResponseEntity<Integer> getCartItemCount(@RequestParam String username, Principal principal) {
        if (!principal.getName().equals(username)) return ResponseEntity.status(403).build();
        return ResponseEntity.ok(cartService.getCartItemCount(username));
    }

    @GetMapping("/total")
    public ResponseEntity<Double> getCartTotalValue(@RequestParam String username, Principal principal) {
        if (!principal.getName().equals(username)) return ResponseEntity.status(403).build();
        return ResponseEntity.ok(cartService.getCartTotalValue(username).doubleValue());
    }
}