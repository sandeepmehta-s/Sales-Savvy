package com.salesSavvy.controller;

import com.salesSavvy.dto.OrderResponse;
import com.salesSavvy.dto.OrderItemResponse;
import com.salesSavvy.entity.Orders;
import com.salesSavvy.entity.OrderItem;
import com.salesSavvy.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.security.Principal;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/orders")
@CrossOrigin(origins = "http://localhost:5173")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/create")
    public ResponseEntity<OrderResponse> createOrderFromCart(
            @RequestParam String username,
            @RequestParam String razorpayOrderId,
            @RequestParam java.math.BigDecimal amount,
            Principal principal) {
        if (!principal.getName().equals(username)) {
            return ResponseEntity.status(403).build();
        }
        Orders order = orderService.createOrderFromCart(username, razorpayOrderId, amount);
        OrderResponse response = convertToOrderResponse(order);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        List<Orders> orders = orderService.getAllOrders();
        List<OrderResponse> orderResponses = orders.stream()
            .map(this::convertToOrderResponse)
            .collect(Collectors.toList());
        return ResponseEntity.ok(orderResponses);
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<List<OrderResponse>> getOrdersByUsername(@PathVariable String username, Principal principal) {
        if (!principal.getName().equals(username)) {
            return ResponseEntity.status(403).build();
        }
        List<Orders> orders = orderService.getOrdersByUsername(username);
        List<OrderResponse> orderResponses = orders.stream()
            .map(this::convertToOrderResponse)
            .collect(Collectors.toList());
        return ResponseEntity.ok(orderResponses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long id) {
        Orders order = orderService.getOrderById(id)
            .orElseThrow(() -> new RuntimeException("Order not found"));
        OrderResponse response = convertToOrderResponse(order);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/razorpay/{razorpayOrderId}")
    public ResponseEntity<OrderResponse> getOrderByRazorpayId(@PathVariable String razorpayOrderId) {
        Orders order = orderService.getOrderByRazorpayId(razorpayOrderId)
            .orElseThrow(() -> new RuntimeException("Order not found"));
        OrderResponse response = convertToOrderResponse(order);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{razorpayOrderId}/cancel")
    public ResponseEntity<String> cancelOrder(@PathVariable String razorpayOrderId) {
        String result = orderService.cancelOrder(razorpayOrderId);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/{razorpayOrderId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> updateOrderStatus(
            @PathVariable String razorpayOrderId,
            @RequestParam String status) {
        String result = orderService.updateOrderStatus(razorpayOrderId, status);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/sales/total")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Double> getTotalSales() {
        Double totalSales = orderService.getTotalSales().doubleValue();
        return ResponseEntity.ok(totalSales);
    }

    private OrderResponse convertToOrderResponse(Orders order) {
        List<OrderItemResponse> itemResponses = order.getItems().stream()
            .map(this::convertToOrderItemResponse)
            .collect(Collectors.toList());

        return new OrderResponse(
            order.getId(),
            order.getRazorpayOrderId(),
            order.getAmount(),
            order.getCurrency(),
            order.getStatus(),
            order.getPaymentId(),
            order.getUser().getId(),
            order.getUser().getUsername(),
            itemResponses,
            order.getCreatedAt(),
            order.getUpdatedAt()
        );
    }

    private OrderItemResponse convertToOrderItemResponse(OrderItem item) {
        return new OrderItemResponse(
            item.getProduct().getId(),
            item.getProductName(),
            item.getProduct().getPhoto(),
            item.getPrice(),
            item.getQuantity()
        );
    }
}