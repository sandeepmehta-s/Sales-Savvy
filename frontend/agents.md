# agents.md — Sales-Savvy Frontend Agent Instructions

> Read this file before making any frontend changes.
> Update it after any significant change.

---

## Project Overview

React 18 + Vite + Bootstrap 5 frontend for Sales-Savvy e-commerce app.

## Backend Connection

| Env Var | Purpose | Local Value |
|---------|---------|-------------|
| `VITE_API_URL` | Backend base URL | `http://localhost:8080` |
| `VITE_STRIPE_PUBLISHABLE_KEY` | Stripe.js init | `pk_test_...` |

**Always use `VITE_API_URL` from env — never hardcode backend URL.**

## Payment Integration (Stripe)

Frontend Stripe flow:
1. Call `POST /payment/create-intent` → get `clientSecret`
2. Use `@stripe/stripe-js` with `clientSecret` to show payment form
3. On success, call `POST /payment/confirm` with `paymentIntentId`

```js
import { loadStripe } from '@stripe/stripe-js';
const stripe = await loadStripe(import.meta.env.VITE_STRIPE_PUBLISHABLE_KEY);
```

## Key API Endpoints

| Feature | Endpoint | Auth |
|---------|----------|------|
| Register | `POST /auth/register` | No |
| Login | `POST /auth/login` | No |
| Products | `GET /products` | No |
| Cart | `GET /cart/items?username=X` | JWT |
| Add to cart | `POST /cart/add?productId=X&quantity=1` | JWT |
| Orders | `GET /orders/user/{username}` | JWT |
| Create payment intent | `POST /payment/create-intent` | JWT |
| Confirm payment | `POST /payment/confirm` | JWT |

## ID Types (MongoDB)

All IDs are now **String** (MongoDB ObjectId format: `"64abc123..."`).
Frontend should treat all IDs as strings — not numbers.

## Structure

```
src/
├── components/
│   ├── auth/          # Login.jsx, Register.jsx
│   ├── common/        # Header.jsx, Footer.jsx
│   └── products/      # ProductDetail.jsx
├── context/           # AuthContext.jsx
├── pages/             # AdminDashboard, Cart, Home, Orders, Products, Profile
└── services/          # api.js, auth.js, cart.js, order.js, payment.js
```

## Rules

1. Never hardcode `localhost:8080` — use `import.meta.env.VITE_API_URL`
2. Never hardcode Stripe key — use `import.meta.env.VITE_STRIPE_PUBLISHABLE_KEY`
3. Razorpay is removed — use Stripe only
4. Product/User/Order IDs are `String` not `number` in MongoDB

## Changelog

- [2026-09-12] Initial docs created
- [2026-09-12] Razorpay → Stripe migration; MySQL → MongoDB (IDs now String)
- [2026-09-12] Payment endpoints: `/payment/create-intent` and `/payment/confirm`
- [2026-09-12] Render deployment added: `VITE_API_URL` + `VITE_STRIPE_PUBLISHABLE_KEY` env vars