# agents.md — ShopSphere Backend

> Read this file before making any changes to the backend.
> Update it after any significant architectural or structural change.
> For full system docs and data flow, see the root `docs/` folder.

---

## Project Overview

**ShopSphere** — Spring Boot 3.5 + Java 17 + MongoDB + Stripe REST API.

| File/Folder | Location |
|-------------|----------|
| Source code | `backend/src/main/java/com/shopSphere/` |
| Resources | `backend/src/main/resources/` (application.properties) |
| Env template| `backend/.env.example` |

---

## Environment Variables

| Variable | Local Value | Production |
|----------|-------------|------------|
| `MONGODB_URI` | `mongodb://localhost:27017/shopsphere` | Atlas connection string |
| `JWT_SECRET` | 32+ char base64 string | Production secret |
| `STRIPE_SECRET_KEY` | `sk_test_...` | `sk_live_...` |
| `CORS_ALLOWED_ORIGINS`| `http://localhost:5173` | Production frontend domain |

**Always use `@Value` or `Environment` to read variables — never hardcode secrets.**

---

## Source Structure

```
backend/src/main/java/com/shopSphere/
├── auth/          # JWT authentication, CustomUserDetailsService
├── cart/          # Cart + embedded CartItems
├── order/         # Orders + embedded OrderItems
├── payment/       # Stripe integrations
├── product/       # Product catalog
├── shared/        # CorsConfig, SecurityConfig, GlobalExceptionHandler
└── user/          # User CRUD
```

---

## Stripe Integration

The backend is responsible for creating `PaymentIntents` and verifying payment confirmation.
Amounts are processed in **smallest currency units (e.g., paise for INR)**.

1. `POST /payment/create-intent`: Accepts amount, creates a Stripe PaymentIntent, returns `clientSecret` and `paymentIntentId`.
2. `POST /payment/confirm`: Accepts `paymentIntentId`. Verifies it with Stripe, creates an `Order`, and marks it as `PAID`.

---

## Data Model & MongoDB

1. **IDs are Strings**: All MongoDB entity IDs are `String` (`ObjectId`). Avoid using `Long` or `Integer` for IDs. Use `@Id String id`.
2. **Embedded Documents**: 
   - `CartItem` is embedded within the `Cart` document (no separate `cart_items` collection).
   - `OrderItem` is embedded within the `Orders` document.
3. **Null-Type Safety**: Spring Data methods (like `findById`) expect `@NonNull` arguments. Use `Objects.requireNonNull(id)` to prevent JDT compiler warnings when passing `String` IDs.

---

## Security (Spring Security 6.4+)

- Configured in `shared/config/SecurityConfig.java`.
- **Stateless**: Uses `SessionCreationPolicy.STATELESS` and JWT tokens via `JwtAuthenticationFilter`.
- **Deprecated APIs**: Avoid explicit `DaoAuthenticationProvider` instantiation in Spring Security 6.4+. Simply expose `PasswordEncoder` and `UserDetailsService` beans and let Spring auto-configure it.

---

## Agent Rules

1. **No MySQL / No SQL**: The database is MongoDB. Do not write SQL queries or use `@Entity` (JPA) / `@Table`. Use `@Document`.
2. **No Razorpay**: Payment gateway is exclusively Stripe.
3. **Never Hardcode Secrets**: Read from `application.properties` via `@Value`.
4. **Fix Warnings**: Address JDT warnings (like Null-type safety and unhandled optionals) as they appear. Use `Objects.requireNonNull()` for IDs.
5. **Update Docs**: After backend API changes, update the frontend `agents.md` and the root `docs/` folder appropriately.

---

## Changelog

| Date | Change |
|------|--------|
| 2026-09-13 | Package renamed from `com.salesSavvy` to `com.shopSphere` globally |
| 2026-09-13 | Fixed JDT warnings (null-type safety, SecurityConfig deprecations, WebConfig) |
| 2026-09-12 | Migrated from MySQL to MongoDB (IDs changed from Long to String) |
| 2026-09-12 | Migrated payment gateway from Razorpay to Stripe |
