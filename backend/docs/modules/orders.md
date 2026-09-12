# Orders Module — ShopSphere

## Overview
Order lifecycle management. OrderItems are **embedded** in the Orders document (snapshot at order time).

## Package
`com.shopSphere.order`

## MongoDB Design

```
orders collection:
  Orders { id, stripePaymentIntentId, amount, currency, status,
           receipt, paymentId, username, items: [OrderItem], createdAt, updatedAt }
  OrderItem { productId, productName, price, quantity }
```

Amount is stored in **paise** (INR × 100). Divide by 100 to display in rupees.

## Order Status Flow

```
CREATED → PAID → SHIPPED → DELIVERED
      ↘ CANCELLED (only if not PAID or SHIPPED)
```

## Endpoints

| Method | Path | Auth | Role | Description |
|--------|------|------|------|-------------|
| GET | `/orders` | JWT | ADMIN | List all orders (latest first) |
| GET | `/orders/user/{username}` | JWT | SELF | User's orders |
| GET | `/orders/{id}` | JWT | ANY | Order by MongoDB ID |
| GET | `/orders/stripe/{paymentIntentId}` | JWT | ANY | Order by Stripe PI |
| PUT | `/orders/{paymentIntentId}/cancel` | JWT | ANY | Cancel order |
| PUT | `/orders/{paymentIntentId}/status` | JWT | ADMIN | Update status |
| GET | `/orders/sales/total` | JWT | ADMIN | Total sales (₹) |
| GET | `/orders/sales/count` | JWT | ADMIN | Total PAID order count |

## Atomic Order Creation

`OrderServiceImpl.createOrderAndMarkPaid()` is a single transactional call:
1. Get cart items
2. Check + deduct stock for each item
3. Build `Orders` document with embedded `OrderItem` list
4. Save order with status = PAID
5. Clear cart

This prevents partial state (order created but not marked PAID).

## Key Files

- `order/controller/OrderController.java`
- `order/entity/Orders.java` — `@Document(collection="orders")`, uses `@CreatedDate` / `@LastModifiedDate`
- `order/entity/OrderItem.java` — embedded POJO (no @Document)
- `order/repository/OrderRepository.java`
- `order/service/OrderServiceImpl.java`