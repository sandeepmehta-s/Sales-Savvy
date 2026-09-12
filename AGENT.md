# AGENT.md — Sales-Savvy

## 🤖 Agent Instructions

> **Ye file AI agents ke liye hai.**
> Koi bhi change karne se pehle `docs/` folder ki relevant file zaroor padho.
> Change complete hone ke baad corresponding doc file update karo.

---

## Project Overview

**Sales-Savvy** ek full-stack e-commerce application hai.

| Layer     | Tech                              | Location          |
|-----------|-----------------------------------|-------------------|
| Backend   | Spring Boot 3, Java 17, JPA       | `sales-savvy-be/` |
| Frontend  | React 18, Vite, Bootstrap 5       | `sales_savvy_fr/` |
| Database  | MySQL                             | DB name: `ecom`   |
| Payment   | Razorpay                          | `/payment/**`     |
| Auth      | JWT (HS256, stateless)            | `auth/security/`  |

---

## 📚 Docs — Pehle Padho

| Kya change karna hai              | Konsi doc padho              |
|-----------------------------------|------------------------------|
| Auth / Login / Register / JWT     | `docs/auth.md`               |
| Products CRUD, Stock, Categories  | `docs/products.md`           |
| Cart operations                   | `docs/cart.md`               |
| Orders, Order status flow         | `docs/orders.md`             |
| Razorpay Payment flow             | `docs/payment.md`            |
| Users, Roles, Admin               | `docs/users.md`              |
| Security, CORS, JWT filter        | `docs/security.md`           |
| Environment variables / Config    | `docs/config.md`             |
| Frontend pages / routes / context | `docs/frontend.md`           |
| Database schema / entities        | `docs/database.md`           |

---

## 📝 Docs Update Rule

Jab bhi koi feature change karo — **usi feature ki doc update karo**.
Har doc ke end mein `## Changelog` section hai. Wahan add karo:

```
- [YYYY-MM-DD] Description of change — reason
```

---

## Key Rules (Agent ke liye)

1. **CORS origin** kabhi hardcode mat karo — `.env` ka `CORS_ALLOWED_ORIGINS` use karo
2. **Backend URL** frontend mein kabhi hardcode mat karo — `VITE_API_URL` env var use karo
3. **Payment flow** always atomic hona chahiye — `createOrderAndMarkPaid()` use karo
4. **Stock deduction** Order create karte time `@Version` optimistic locking ke saath honi chahiye
5. **Admin endpoints** pe hamesha `@PreAuthorize("hasRole('ADMIN')")` lagao
6. **Username** `Principal` se nikalo — query param pe blindly trust mat karo
7. Naya controller add karo toh `@CrossOrigin` mat lagao — global `CorsConfig` sab handle karta hai
8. Naya `.env` variable add karo toh `docs/config.md` bhi update karo
9. Nayi class add karo toh **correct feature package** mein daalo (neeche structure dekho)

---

## Package Structure (Backend) — Feature-based

```
src/main/java/com/salesSavvy/
│
├── auth/                          ← Authentication feature
│   ├── controller/AuthController.java
│   ├── dto/AuthResponse.java
│   ├── entity/UserLoginData.java
│   └── security/
│       ├── CustomUserDetailsService.java
│       ├── JwtAuthenticationFilter.java
│       └── JwtUtil.java
│
├── user/                          ← User management feature
│   ├── controller/UserController.java
│   ├── dto/UserResponse.java
│   ├── entity/Users.java
│   ├── repository/UsersRepository.java
│   └── service/
│       ├── UsersService.java
│       └── UsersServiceImplementation.java
│
├── product/                       ← Product catalog feature
│   ├── controller/ProductController.java
│   ├── dto/ProductResponse.java
│   ├── entity/Product.java
│   ├── repository/ProductRepository.java
│   └── service/
│       ├── ProductService.java
│       └── ProductServiceImplementation.java
│
├── cart/                          ← Shopping cart feature
│   ├── controller/CartController.java
│   ├── dto/
│   │   ├── CartItemResponse.java
│   │   └── CartResponse.java
│   ├── entity/
│   │   ├── Cart.java
│   │   └── CartItem.java
│   ├── repository/
│   │   ├── CartItemRepository.java
│   │   └── CartRepository.java
│   └── service/
│       ├── CartService.java
│       └── CartServiceImplementation.java
│
├── order/                         ← Order management feature
│   ├── controller/OrderController.java
│   ├── dto/
│   │   ├── OrderItemResponse.java
│   │   └── OrderResponse.java
│   ├── entity/
│   │   ├── OrderItem.java
│   │   └── Orders.java
│   ├── repository/
│   │   ├── OrderItemRepository.java
│   │   └── OrderRepository.java
│   └── service/
│       ├── OrderService.java
│       └── OrderServiceImpl.java
│
├── payment/                       ← Payment (Razorpay) feature
│   ├── controller/PaymentController.java
│   ├── dto/
│   │   ├── PaymentRequest.java
│   │   └── PaymentVerifyRequest.java
│   └── service/
│       ├── PaymentService.java
│       └── PaymentServiceImpl.java
│
├── shared/                        ← Shared cross-cutting concerns
│   ├── config/
│   │   ├── CorsConfig.java
│   │   ├── SecurityConfig.java
│   │   └── WebConfig.java
│   └── exception/
│       ├── BadRequestException.java
│       ├── CartOperationException.java
│       ├── DuplicateResourceException.java
│       ├── ErrorResponse.java
│       ├── GlobalExceptionHandler.java
│       ├── PaymentProcessingException.java
│       └── ResourceNotFoundException.java
│
└── Application.java
```

## Cross-Module Import Rules
Entities reference each other across modules — always use full qualified import:
| File | References | Import needed |
|------|-----------|---------------|
| `cart/entity/Cart.java` | Users | `import com.salesSavvy.user.entity.Users;` |
| `cart/entity/CartItem.java` | Product | `import com.salesSavvy.product.entity.Product;` |
| `order/entity/OrderItem.java` | Product | `import com.salesSavvy.product.entity.Product;` |
| `order/entity/Orders.java` | Users | `import com.salesSavvy.user.entity.Users;` |
| `user/entity/Users.java` | Cart, Orders | `import com.salesSavvy.cart.entity.Cart; import com.salesSavvy.order.entity.Orders;` |

## Frontend Structure (`sales_savvy_fr/`)
```
src/
├── components/
│   ├── auth/          # Login.jsx, Register.jsx
│   ├── common/        # Header.jsx, Footer.jsx
│   └── products/      # ProductDetail.jsx
├── context/           # AuthContext.jsx
├── pages/             # AdminDashboard, AdminOrders, AdminProducts,
│                      # AdminUsers, Cart, Home, Orders, Products, Profile
└── services/          # api.js, auth.js, cart.js, order.js,
                       # payment.js, product.js, user.js
```
