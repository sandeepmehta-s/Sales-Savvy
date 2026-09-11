package com.salesSavvy.service;

import com.salesSavvy.entity.Orders;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface OrderService {
    Orders createOrderFromCart(String username, String razorpayOrderId, BigDecimal amount);

    // Bug Fix #3: Atomic method — create order + mark paid in one transaction
    Orders createOrderAndMarkPaid(String username, String razorpayOrderId,
                                   BigDecimal amount, String paymentId);

    Orders placeOrder(Orders order);
    List<Orders> getAllOrders();
    List<Orders> getOrdersByUserId(Long userId);
    List<Orders> getOrdersByUsername(String username);
    Optional<Orders> getOrderById(Long id);
    Optional<Orders> getOrderByRazorpayId(String razorpayOrderId);
    String cancelOrder(String razorpayOrderId);
    String updateOrderStatus(String razorpayOrderId, String status);
    void updatePaymentDetails(String razorpayOrderId, String paymentId, String status);
    BigDecimal getTotalSales();

    // Bug Fix #2: Returns actual count of PAID orders (not amount sum)
    long getTotalOrderCount();
}