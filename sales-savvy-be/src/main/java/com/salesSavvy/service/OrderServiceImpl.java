package com.salesSavvy.service;

import com.salesSavvy.entity.Orders;
import com.salesSavvy.entity.OrderItem;
import com.salesSavvy.entity.CartItem;
import com.salesSavvy.entity.Users;
import com.salesSavvy.exception.ResourceNotFoundException;
import com.salesSavvy.repository.OrderRepository;
import com.salesSavvy.repository.UsersRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UsersRepository usersRepository;
    private final CartService cartService;

    public OrderServiceImpl(OrderRepository orderRepository, 
                          UsersRepository usersRepository,
                          CartService cartService) {
        this.orderRepository = orderRepository;
        this.usersRepository = usersRepository;
        this.cartService = cartService;
    }

    @Override
    @Transactional
    public Orders createOrderFromCart(String username, String razorpayOrderId, BigDecimal amount) {
        Users user = usersRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));

        // Get user's cart items
        List<CartItem> cartItems = cartService.getCartItems(username);
        
        if (cartItems.isEmpty()) {
            throw new IllegalArgumentException("Cannot create order: Cart is empty");
        }

        // Bug Fix #1: Check stock availability before creating order
        for (CartItem cartItem : cartItems) {
            if (!cartItem.getProduct().hasStock(cartItem.getQuantity())) {
                throw new IllegalStateException(
                    "Insufficient stock for product: " + cartItem.getProduct().getName() +
                    ". Available: " + cartItem.getProduct().getStockQuantity() +
                    ", Requested: " + cartItem.getQuantity()
                );
            }
        }

        // Create order
        Orders order = new Orders();
        order.setRazorpayOrderId(razorpayOrderId);
        order.setAmount(amount);
        order.setCurrency("INR");
        order.setStatus("CREATED");
        order.setUser(user);
        order.setReceipt("rcpt_" + System.currentTimeMillis());

        // Bug Fix #1: Deduct stock atomically while converting cart items to order items
        for (CartItem cartItem : cartItems) {
            cartItem.getProduct().deductStock(cartItem.getQuantity()); // deducts + validates
            OrderItem orderItem = new OrderItem(order, cartItem.getProduct(), cartItem.getQuantity());
            order.addItem(orderItem);
        }

        Orders savedOrder = orderRepository.save(order);

        // Clear cart after successful order creation
        cartService.clearCart(username);

        return savedOrder;
    }

    // Bug Fix #3: Atomic method — creates order AND marks it PAID in one transaction
    // Prevents partial state where order is created but payment status never updated
    @Override
    @Transactional
    public Orders createOrderAndMarkPaid(String username, String razorpayOrderId,
                                         BigDecimal amount, String paymentId) {
        Orders order = createOrderFromCart(username, razorpayOrderId, amount);
        order.setPaymentId(paymentId);
        order.setStatus("PAID");
        return orderRepository.save(order);
    }

    @Override
    @Transactional
    public Orders placeOrder(Orders order) {
        if (order.getUser() == null) {
            throw new IllegalArgumentException("Order must be associated with a user");
        }
        
        if (order.getItems() == null || order.getItems().isEmpty()) {
            throw new IllegalArgumentException("Order must have at least one item");
        }
        
        order.setStatus("CREATED");
        return orderRepository.save(order);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Orders> getAllOrders() {
        return orderRepository.findAllByOrderByCreatedAtDesc();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Orders> getOrdersByUserId(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Orders> getOrdersByUsername(String username) {
        return orderRepository.findByUserUsername(username);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Orders> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Orders> getOrderByRazorpayId(String razorpayOrderId) {
        return orderRepository.findByRazorpayOrderId(razorpayOrderId);
    }

    @Override
    @Transactional
    public String cancelOrder(String razorpayOrderId) {
        Orders order = orderRepository.findByRazorpayOrderId(razorpayOrderId)
            .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + razorpayOrderId));
        
        if ("PAID".equalsIgnoreCase(order.getStatus()) || "SHIPPED".equalsIgnoreCase(order.getStatus())) {
            return "Cannot cancel a paid or shipped order";
        }
        
        order.setStatus("CANCELLED");
        orderRepository.save(order);
        return "Order cancelled successfully";
    }

    @Override
    @Transactional
    public String updateOrderStatus(String razorpayOrderId, String status) {
        Orders order = orderRepository.findByRazorpayOrderId(razorpayOrderId)
            .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + razorpayOrderId));
        
        String upperStatus = status.toUpperCase();
        if (!isValidStatus(upperStatus)) {
            return "Invalid order status: " + status;
        }
        
        order.setStatus(upperStatus);
        orderRepository.save(order);
        return "Order status updated to " + upperStatus;
    }

    @Override
    @Transactional
    public void updatePaymentDetails(String razorpayOrderId, String paymentId, String status) {
        Orders order = orderRepository.findByRazorpayOrderId(razorpayOrderId)
            .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + razorpayOrderId));
        
        order.setPaymentId(paymentId);
        order.setStatus(status);
        orderRepository.save(order);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalSales() {
        List<Orders> paidOrders = orderRepository.findByStatus("PAID");
        return paidOrders.stream()
            .map(Orders::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add)
            .divide(BigDecimal.valueOf(100)); // Convert from paise to rupees
    }

    // Bug Fix #2: Total order count — returns number of PAID orders, not amount sum
    @Override
    @Transactional(readOnly = true)
    public long getTotalOrderCount() {
        return orderRepository.countByStatus("PAID");
    }

    private boolean isValidStatus(String status) {
        return List.of("CREATED", "PAID", "CANCELLED", "SHIPPED", "DELIVERED").contains(status);
    }
}