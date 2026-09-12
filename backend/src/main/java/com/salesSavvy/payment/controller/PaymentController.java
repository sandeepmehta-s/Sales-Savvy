package com.salesSavvy.payment.controller;

import com.salesSavvy.order.entity.Orders;
import com.salesSavvy.order.service.OrderService;
import com.salesSavvy.payment.dto.PaymentRequest;
import com.salesSavvy.payment.dto.PaymentVerifyRequest;
import com.salesSavvy.payment.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/payment")
public class PaymentController {

    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);

    private final PaymentService paymentService;
    private final OrderService orderService;

    public PaymentController(PaymentService paymentService, OrderService orderService) {
        this.paymentService = paymentService;
        this.orderService = orderService;
    }

    /**
     * Step 1: Frontend calls this to create a Stripe PaymentIntent
     * Returns clientSecret for Stripe.js to confirm payment on frontend
     */
    @PostMapping("/create-intent")
    public ResponseEntity<Map<String, Object>> createIntent(
            @RequestBody PaymentRequest request, Principal principal) {
        try {
            if (principal == null || !principal.getName().equals(request.getUsername())) {
                return ResponseEntity.status(403).build();
            }
            Map<String, Object> response = paymentService.createPaymentIntent(
                request.getAmount(), request.getCurrency()
            );
            response.put("publishableKey", paymentService.getPublishableKey());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> err = new HashMap<>();
            err.put("error", "Failed to create payment intent");
            err.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(err);
        }
    }

    /**
     * Step 2: Frontend calls this after Stripe.js confirms payment
     * Creates order atomically and marks it PAID
     */
    @PostMapping("/confirm")
    public ResponseEntity<Map<String, Object>> confirmPayment(
            @RequestBody PaymentVerifyRequest verifyRequest, Principal principal) {

        Map<String, Object> response = new HashMap<>();
        try {
            String username = principal.getName();

            // Atomic: create order + mark PAID in one call
            Orders createdOrder = orderService.createOrderAndMarkPaid(
                username,
                verifyRequest.getPaymentIntentId(),
                verifyRequest.getAmount(),
                verifyRequest.getPaymentId()
            );

            response.put("status", "success");
            response.put("message", "Payment confirmed and order created successfully");
            response.put("orderId", createdOrder.getId());
            response.put("amount", createdOrder.getAmount());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Order creation failed after Stripe payment confirmed", e);
            response.put("status", "error");
            response.put("message", "Payment was successful but order creation failed");
            response.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /** Returns Stripe publishable key for frontend Stripe.js initialization */
    @GetMapping("/key")
    public ResponseEntity<Map<String, String>> getKey() {
        Map<String, String> response = new HashMap<>();
        response.put("publishableKey", paymentService.getPublishableKey());
        return ResponseEntity.ok(response);
    }
}