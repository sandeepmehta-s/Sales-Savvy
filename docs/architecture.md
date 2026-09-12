# Architecture — ShopSphere

## Overview

ShopSphere is a three-tier web application:

```
[React SPA] ──HTTP/JSON──> [Spring Boot REST API] ──-> [MongoDB Atlas]
                                    |
                                    └──────────────────> [Stripe API]
```

## Technology Stack

| Layer | Technology | Version |
|-------|-----------|---------|
| Frontend | React + Vite + Bootstrap 5 | React 18 |
| Backend | Spring Boot | 3.5.0 |
| Language | Java | 17 |
| Database | MongoDB | Atlas Free (512 MB) |
| Auth | JWT (jjwt) | 0.11.5 |
| Payment | Stripe Java SDK | 26.3.0 |
| Hosting | Render | Free tier |

---

## Request Flow

### Public Request (e.g., GET /products)
```
Browser → Render CDN → Frontend (React) → fetch(VITE_API_URL/products)
→ Backend (Spring Boot) → ProductRepository → MongoDB Atlas → Response
```

### Authenticated Request (e.g., POST /cart/add)
```
Browser → fetch(VITE_API_URL/cart/add) + Authorization: Bearer <jwt>
→ JwtAuthenticationFilter → SecurityContext → CartController
→ CartServiceImplementation → CartRepository → MongoDB Atlas → Response
```

### Payment Flow
```
1. Frontend: POST /payment/create-intent { username, amount, currency }
2. Backend: Stripe.PaymentIntent.create() → returns clientSecret
3. Frontend: stripe.confirmPayment(clientSecret) → Stripe handles 3DS etc.
4. Frontend: POST /payment/confirm { paymentIntentId, paymentId, amount }
5. Backend: createOrderAndMarkPaid() — atomic: creates Order + sets PAID
6. Cart cleared, stock deducted, order saved
```

---

## Backend Package Structure

```
com.salesSavvy/
├── auth/
│   ├── controller/AuthController.java
│   ├── dto/AuthResponse.java
│   ├── entity/UserLoginData.java
│   └── security/
│       ├── CustomUserDetailsService.java
│       ├── JwtAuthenticationFilter.java
│       └── JwtUtil.java
├── user/
│   ├── controller/UserController.java
│   ├── dto/UserResponse.java
│   ├── entity/Users.java
│   ├── repository/UsersRepository.java
│   └── service/UsersService.java + UsersServiceImplementation.java
├── product/
│   ├── controller/ProductController.java
│   ├── dto/ProductResponse.java
│   ├── entity/Product.java
│   ├── repository/ProductRepository.java
│   └── service/ProductService.java + ProductServiceImplementation.java
├── cart/
│   ├── controller/CartController.java
│   ├── dto/CartResponse.java + CartItemResponse.java
│   ├── entity/Cart.java (Document) + CartItem.java (Embedded)
│   ├── repository/CartRepository.java
│   └── service/CartService.java + CartServiceImplementation.java
├── order/
│   ├── controller/OrderController.java
│   ├── dto/OrderResponse.java + OrderItemResponse.java
│   ├── entity/Orders.java (Document) + OrderItem.java (Embedded)
│   ├── repository/OrderRepository.java
│   └── service/OrderService.java + OrderServiceImpl.java
├── payment/
│   ├── controller/PaymentController.java
│   ├── dto/PaymentRequest.java + PaymentVerifyRequest.java
│   └── service/PaymentService.java + PaymentServiceImpl.java
└── shared/
    ├── config/CorsConfig.java + SecurityConfig.java + WebConfig.java
    └── exception/ (ResourceNotFoundException, DuplicateResourceException, ...)
```

---

## MongoDB Document Design

### Embedded vs Referenced
| Relationship | Design Choice | Reason |
|--------------|---------------|--------|
| Cart → CartItems | **Embedded** | Always accessed together; 1-to-few |
| Orders → OrderItems | **Embedded** | Snapshot at order time; immutable after creation |
| User → Cart | **Reference** (cartId: String) | Cart can grow large |
| User → Orders | **Reference** (orderIds: [String]) | Orders can be many |

---

## Security Architecture

```
Request → CorsConfig (CORS filter)
        → JwtAuthenticationFilter (validate Bearer token)
        → SecurityConfig (role-based access rules)
        → Controller
```

- Public endpoints: `GET /products/**`, `POST /auth/login`, `POST /auth/register`, `GET /auth/test`
- User endpoints: `/cart/**`, `/orders/user/**`, `GET /users/{username}`
- Admin endpoints: `POST /products`, `DELETE /products/**`, `GET /orders`, `GET /users`

---

## Concurrency Protection

- **Optimistic locking** on `Product.version` (`@Version`) — prevents two simultaneous orders from over-selling the same stock
- **Atomic order creation** — `createOrderAndMarkPaid()` is a single `@Transactional` call: creates order AND marks PAID in one step