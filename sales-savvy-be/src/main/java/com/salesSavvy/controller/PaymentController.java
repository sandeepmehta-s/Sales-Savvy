package com.salesSavvy.controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.razorpay.Order;
import com.razorpay.RazorpayException;
import com.salesSavvy.dto.PaymentRequest;
import com.salesSavvy.dto.PaymentVerifyRequest;
import com.salesSavvy.entity.Orders;
import com.salesSavvy.service.OrderService;
import com.salesSavvy.service.PaymentService;

@RestController
@RequestMapping("/payment")
public class PaymentController {

    private final PaymentService paymentService;
    private final OrderService orderService;

    public PaymentController(PaymentService paymentService, OrderService orderService) {
        this.paymentService = paymentService;
        this.orderService = orderService;
    }

    @PostMapping("/create-order")
    public ResponseEntity<Map<String, Object>> createOrder(@RequestBody PaymentRequest paymentRequest, Principal principal) {
        try {
            if (principal == null || !principal.getName().equals(paymentRequest.getUsername())) {
                return ResponseEntity.status(403).build();
            }
            Order razorpayOrder = paymentService.createRazorpayOrder(paymentRequest.getAmount());

            JSONObject orderJson = new JSONObject(razorpayOrder.toString());
            
            Map<String, Object> response = new HashMap<>();
            response.put("orderId", orderJson.getString("id"));
            response.put("amount", orderJson.getInt("amount"));
            response.put("currency", orderJson.getString("currency"));
            response.put("key", paymentService.getKeyId());

            return ResponseEntity.ok(response);
        } catch (RazorpayException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to create order");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    
    @PostMapping("/verify")
    public ResponseEntity<Map<String, Object>> verifyPayment(
            @RequestBody PaymentVerifyRequest verifyRequest,
            Principal principal) {

        Map<String, Object> response = new HashMap<>();

        try {
//            System.out.println("=== STEP 1: Starting payment verification ===");
//            System.out.println("Received payment data:");
//            System.out.println(" - Order ID: " + verifyRequest.getOrderId());
//            System.out.println(" - Payment ID: " + verifyRequest.getPaymentId());
//            System.out.println(" - Signature: " + verifyRequest.getSignature());
//            System.out.println(" - Amount: " + verifyRequest.getAmount());
//            System.out.println(" - Principal: " + (principal != null ? principal.getName() : "NULL"));

            // ✅ 1. Verify Razorpay signature
            System.out.println("=== STEP 2: Verifying Razorpay signature ===");
            boolean isValid = paymentService.verifySignature(
                    verifyRequest.getOrderId(),
                    verifyRequest.getPaymentId(),
                    verifyRequest.getSignature()
            );

//            System.out.println("Signature verification result: " + isValid);

            if (!isValid) {
                System.out.println("❌ Signature verification FAILED");
                response.put("status", "error");
                response.put("message", "Payment verification failed");
                return ResponseEntity.badRequest().body(response);
            }

            System.out.println("✅ Signature verification PASSED");

            // ✅ 2. Get username
            System.out.println("=== STEP 3: Getting username ===");
            String username = principal.getName();
            System.out.println("Using username: " + username);

            // ✅ 3. Create Order from Cart
            System.out.println("=== STEP 4: Creating order from cart ===");
            System.out.println("Calling orderService.createOrderFromCart...");
            
            // Bug Fix #3: Single atomic call — creates order AND marks PAID in one transaction
            // Previously two separate calls could leave order in CREATED state if second call failed
            Orders createdOrder = orderService.createOrderAndMarkPaid(
                    username,
                    verifyRequest.getOrderId(),
                    verifyRequest.getAmount(),
                    verifyRequest.getPaymentId()
            );

            System.out.println("✅ Order created and marked PAID with ID: " + createdOrder.getId());

            // ✅ 5. Send success response
            response.put("status", "success");
            response.put("message", "Payment verified and order created successfully");
            response.put("orderId", createdOrder.getId());
            response.put("amount", createdOrder.getAmount());
            
            return ResponseEntity.ok(response);

        } catch (Exception e) {
//            System.out.println("❌ ERROR IN PAYMENT VERIFICATION:");
//            System.out.println("Error message: " + e.getMessage());
//            System.out.println("Error class: " + e.getClass().getName());
            e.printStackTrace(); // This will show the exact line where it fails
            
            response.put("status", "error");
            response.put("message", "Payment was successful but there was an issue creating your order");
            response.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/key")
    public ResponseEntity<Map<String, String>> getKey() {
        Map<String, String> response = new HashMap<>();
        response.put("key", paymentService.getKeyId());
        return ResponseEntity.ok(response);
    }
}