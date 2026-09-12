package com.salesSavvy.order.controller;

import com.salesSavvy.order.dto.OrderItemResponse;
import com.salesSavvy.order.dto.OrderResponse;
import com.salesSavvy.order.entity.OrderItem;
import com.salesSavvy.order.entity.Orders;
import com.salesSavvy.order.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders().stream()
            .map(this::toResponse).collect(Collectors.toList()));
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<List<OrderResponse>> getOrdersByUsername(
            @PathVariable String username, Principal principal) {
        if (!principal.getName().equals(username)) return ResponseEntity.status(403).build();
        return ResponseEntity.ok(orderService.getOrdersByUsername(username).stream()
            .map(this::toResponse).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable String id) {
        return orderService.getOrderById(id)
            .map(o -> ResponseEntity.ok(toResponse(o)))
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/stripe/{paymentIntentId}")
    public ResponseEntity<OrderResponse> getOrderByStripeId(@PathVariable String paymentIntentId) {
        return orderService.getOrderByStripePaymentIntentId(paymentIntentId)
            .map(o -> ResponseEntity.ok(toResponse(o)))
            .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{paymentIntentId}/cancel")
    public ResponseEntity<String> cancelOrder(@PathVariable String paymentIntentId) {
        return ResponseEntity.ok(orderService.cancelOrder(paymentIntentId));
    }

    @PutMapping("/{paymentIntentId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> updateOrderStatus(
            @PathVariable String paymentIntentId, @RequestParam String status) {
        return ResponseEntity.ok(orderService.updateOrderStatus(paymentIntentId, status));
    }

    @GetMapping("/sales/total")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Double> getTotalSales() {
        return ResponseEntity.ok(orderService.getTotalSales().doubleValue());
    }

    @GetMapping("/sales/count")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Long> getTotalOrderCount() {
        return ResponseEntity.ok(orderService.getTotalOrderCount());
    }

    private OrderResponse toResponse(Orders order) {
        List<OrderItemResponse> items = order.getItems().stream()
            .map(item -> new OrderItemResponse(
                item.getProductId(), item.getProductName(), null, item.getPrice(), item.getQuantity()
            ))
            .collect(Collectors.toList());

        return new OrderResponse(
            order.getId(),
            order.getStripePaymentIntentId(),
            order.getAmount(),
            order.getCurrency(),
            order.getStatus(),
            order.getPaymentId(),
            order.getUsername(),
            items,
            order.getCreatedAt(),
            order.getUpdatedAt()
        );
    }
}