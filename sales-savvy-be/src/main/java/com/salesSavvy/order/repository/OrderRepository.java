package com.salesSavvy.order.repository;

import com.salesSavvy.order.entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Orders, Long> {
    List<Orders> findByUserId(Long userId);
    List<Orders> findByUserUsername(String username);
    List<Orders> findByStatus(String status);
    Optional<Orders> findByRazorpayOrderId(String razorpayOrderId);
    Optional<Orders> findByPaymentId(String paymentId);
    List<Orders> findAllByOrderByCreatedAtDesc();
    Long countByUserId(Long userId);
    // Bug Fix #2: Count orders by status (used for total order count on dashboard)
    long countByStatus(String status);
}