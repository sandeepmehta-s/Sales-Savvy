package com.shopSphere.order.repository;

import com.shopSphere.order.entity.Orders;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends MongoRepository<Orders, String> {
    List<Orders> findByUsername(String username);
    List<Orders> findByStatus(String status);
    Optional<Orders> findByStripePaymentIntentId(String paymentIntentId);
    List<Orders> findAllByOrderByCreatedAtDesc();
    long countByStatus(String status);
}