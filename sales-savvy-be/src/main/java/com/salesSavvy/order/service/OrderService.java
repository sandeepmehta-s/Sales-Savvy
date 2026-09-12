package com.salesSavvy.order.service;

import com.salesSavvy.order.entity.Orders;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface OrderService {
    Orders createOrderFromCart(String username, String stripePaymentIntentId, BigDecimal amount);
    Orders createOrderAndMarkPaid(String username, String stripePaymentIntentId, BigDecimal amount, String paymentId);
    Orders placeOrder(Orders order);
    List<Orders> getAllOrders();
    List<Orders> getOrdersByUsername(String username);
    Optional<Orders> getOrderById(String id);
    Optional<Orders> getOrderByStripePaymentIntentId(String paymentIntentId);
    String cancelOrder(String stripePaymentIntentId);
    String updateOrderStatus(String stripePaymentIntentId, String status);
    void updatePaymentDetails(String stripePaymentIntentId, String paymentId, String status);
    BigDecimal getTotalSales();
    long getTotalOrderCount();
}