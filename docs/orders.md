# Feature: Orders

## Overview
Order payment verify hone ke baad create hota hai. Order items cart se copy hote hain.
Order create hote time hi stock deduct hoti hai.

## Backend Files
| File | Purpose |
|------|---------|
| `controller/OrderController.java` | `/orders/**` endpoints |
| `entity/Orders.java` | Order entity (DB table: `orders`) |
| `entity/OrderItem.java` | OrderItem entity (DB table: `order_items`) |
| `service/OrderService.java` | Interface |
| `service/OrderServiceImpl.java` | Business logic |
| `repository/OrderRepository.java` | JPA queries |
| `dto/OrderResponse.java` | Order response DTO |
| `dto/OrderItemResponse.java` | OrderItem response DTO |

## Order Statuses
| Status | Meaning |
|--------|---------|
| CREATED | Order ban gaya, payment pending |
| PAID | Payment successful |
| CANCELLED | User/Admin ne cancel kiya |
| SHIPPED | Order ship ho gaya |
| DELIVERED | Order deliver ho gaya |

## API Endpoints

### POST `/orders/create?username=&razorpayOrderId=&amount=` — Auth required
Cart se order banao.
> **Note:** Ye directly call mat karo. Payment verify flow ke baad `createOrderAndMarkPaid()` automatically call hota hai.

### GET `/orders` — ADMIN ONLY
Saare orders (latest first).

### GET `/orders/user/{username}` — Auth required (own user only)
User ke saare orders.

### GET `/orders/{id}` — Auth required
Order by DB id.

### GET `/orders/razorpay/{razorpayOrderId}` — Auth required
Order by Razorpay order ID.

### PUT `/orders/{razorpayOrderId}/cancel` — Auth required
Order cancel karo (sirf CREATED status pe, PAID/SHIPPED pe nahi).

### PUT `/orders/{razorpayOrderId}/status?status=` — ADMIN ONLY
Order status update karo.

### GET `/orders/sales/total` — ADMIN ONLY
Total revenue — sum of all PAID orders' amount (rupees mein, paise se convert).

### GET `/orders/sales/count` — ADMIN ONLY
Total PAID orders count (dashboard ke liye).

## Order Creation Flow (Critical!)

```
Frontend → POST /payment/create-order (Razorpay order create)
     ↓
Razorpay → user pays
     ↓
Frontend → POST /payment/verify (signature verify)
     ↓
Backend (ATOMIC) → createOrderAndMarkPaid()
  └── createOrderFromCart()
        ├── Stock check karo (IllegalStateException if insufficient)
        ├── Stock deduct karo (@Version optimistic locking)
        ├── Order + OrderItems save karo
        └── Cart clear karo
  └── Order status = PAID, paymentId set karo
  └── Save karo
```

> **IMPORTANT:** `createOrderFromCart()` aur `updatePaymentDetails()` kabhi alag mat bulao.
> Hamesha `createOrderAndMarkPaid()` use karo — yeh atomic hai ek transaction mein.

## Frontend Files
| File | Purpose |
|------|---------|
| `services/order.js` | getUserOrders, getAllOrders, cancelOrder, updateOrderStatus |
| `pages/Orders.jsx` | User ka order history |
| `pages/AdminOrders.jsx` | Admin order management |

## Rules (Agent ke liye)
- Order entity mein naya field add karo → `OrderResponse.java` DTO bhi update karo
- Status values sirf: `CREATED, PAID, CANCELLED, SHIPPED, DELIVERED`
- `getTotalSales()` = revenue (amount sum) ; `getTotalOrderCount()` = count — confuse mat karo
- Stock deduction Order create karte time hoti hai — payment ke baad, Order ke andar

## Changelog
- 2026-09-12 createOrderAndMarkPaid() atomic method added (bug fix: non-atomic payment)
- 2026-09-12 GET /orders/sales/count endpoint added (bug fix: total orders count)
- 2026-09-12 Stock check + deduction added in createOrderFromCart() (bug fix: race condition)
- 2026-09-12 Initial docs created
