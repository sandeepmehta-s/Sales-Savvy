package com.salesSavvy.order.service;

import com.salesSavvy.cart.entity.CartItem;
import com.salesSavvy.cart.service.CartService;
import com.salesSavvy.order.entity.Orders;
import com.salesSavvy.order.entity.OrderItem;
import com.salesSavvy.product.entity.Product;
import com.salesSavvy.product.repository.ProductRepository;
import com.salesSavvy.shared.exception.ResourceNotFoundException;
import com.salesSavvy.order.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final CartService cartService;

    public OrderServiceImpl(OrderRepository orderRepository,
                            ProductRepository productRepository,
                            CartService cartService) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.cartService = cartService;
    }

    @Override
    public Orders createOrderFromCart(String username, String stripePaymentIntentId, BigDecimal amount) {
        List<CartItem> cartItems = cartService.getCartItems(username);

        if (cartItems.isEmpty()) {
            throw new IllegalArgumentException("Cannot create order: Cart is empty");
        }

        // Check and deduct stock for each item
        for (CartItem cartItem : cartItems) {
            Product product = productRepository.findById(cartItem.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + cartItem.getProductId()));

            if (!product.hasStock(cartItem.getQuantity())) {
                throw new IllegalStateException(
                    "Insufficient stock for: " + product.getName() +
                    ". Available: " + product.getStockQuantity() +
                    ", Requested: " + cartItem.getQuantity()
                );
            }
            product.deductStock(cartItem.getQuantity());
            productRepository.save(product);
        }

        // Build Order with embedded OrderItems
        Orders order = new Orders();
        order.setStripePaymentIntentId(stripePaymentIntentId);
        order.setAmount(amount);
        order.setCurrency("INR");
        order.setStatus("CREATED");
        order.setUsername(username);
        order.setReceipt("rcpt_" + System.currentTimeMillis());

        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = new OrderItem(
                cartItem.getProductId(),
                cartItem.getProductName(),
                cartItem.getPrice(),
                cartItem.getQuantity()
            );
            order.addItem(orderItem);
        }

        Orders savedOrder = orderRepository.save(order);
        cartService.clearCart(username);
        return savedOrder;
    }

    @Override
    public Orders createOrderAndMarkPaid(String username, String stripePaymentIntentId,
                                          BigDecimal amount, String paymentId) {
        Orders order = createOrderFromCart(username, stripePaymentIntentId, amount);
        order.setPaymentId(paymentId);
        order.setStatus("PAID");
        return orderRepository.save(order);
    }

    @Override
    public Orders placeOrder(Orders order) {
        if (order.getUsername() == null) throw new IllegalArgumentException("Order must have a username");
        if (order.getItems() == null || order.getItems().isEmpty()) throw new IllegalArgumentException("Order must have at least one item");
        order.setStatus("CREATED");
        return orderRepository.save(order);
    }

    @Override
    public List<Orders> getAllOrders() {
        return orderRepository.findAllByOrderByCreatedAtDesc();
    }

    @Override
    public List<Orders> getOrdersByUsername(String username) {
        return orderRepository.findByUsername(username);
    }

    @Override
    public Optional<Orders> getOrderById(String id) {
        return orderRepository.findById(id);
    }

    @Override
    public Optional<Orders> getOrderByStripePaymentIntentId(String paymentIntentId) {
        return orderRepository.findByStripePaymentIntentId(paymentIntentId);
    }

    @Override
    public String cancelOrder(String stripePaymentIntentId) {
        Orders order = orderRepository.findByStripePaymentIntentId(stripePaymentIntentId)
            .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + stripePaymentIntentId));
        if ("PAID".equalsIgnoreCase(order.getStatus()) || "SHIPPED".equalsIgnoreCase(order.getStatus())) {
            return "Cannot cancel a paid or shipped order";
        }
        order.setStatus("CANCELLED");
        orderRepository.save(order);
        return "Order cancelled successfully";
    }

    @Override
    public String updateOrderStatus(String stripePaymentIntentId, String status) {
        Orders order = orderRepository.findByStripePaymentIntentId(stripePaymentIntentId)
            .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + stripePaymentIntentId));
        String upperStatus = status.toUpperCase();
        if (!List.of("CREATED", "PAID", "CANCELLED", "SHIPPED", "DELIVERED").contains(upperStatus)) {
            return "Invalid order status: " + status;
        }
        order.setStatus(upperStatus);
        orderRepository.save(order);
        return "Order status updated to " + upperStatus;
    }

    @Override
    public void updatePaymentDetails(String stripePaymentIntentId, String paymentId, String status) {
        Orders order = orderRepository.findByStripePaymentIntentId(stripePaymentIntentId)
            .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + stripePaymentIntentId));
        order.setPaymentId(paymentId);
        order.setStatus(status);
        orderRepository.save(order);
    }

    @Override
    public BigDecimal getTotalSales() {
        List<Orders> paidOrders = orderRepository.findByStatus("PAID");
        BigDecimal total = BigDecimal.ZERO;
        for (Orders o : paidOrders) total = total.add(o.getAmount());
        return total.divide(BigDecimal.valueOf(100)); // paise → rupees
    }

    @Override
    public long getTotalOrderCount() {
        return orderRepository.countByStatus("PAID");
    }
}