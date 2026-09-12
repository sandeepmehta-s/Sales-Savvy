# agents.md — ShopSphere Frontend

> Read this file before making any changes to the frontend.
> Update it after any significant change.
> For backend API docs, see the root `docs/` folder.

---

## Project Overview

**ShopSphere** — React 18 + Vite + Bootstrap 5 single-page e-commerce application.

| File | Location |
|------|----------|
| Source code | `frontend/src/` |
| Build output | `frontend/dist/` (git-ignored) |
| Env template | `frontend/.env.example` |

---

## Environment Variables

| Variable | Local Value | Production |
|----------|-------------|------------|
| `VITE_API_URL` | `http://localhost:8080` | `https://shopsphere-backend.onrender.com` |
| `VITE_STRIPE_PUBLISHABLE_KEY` | `pk_test_...` | `pk_live_...` |

**Always use `import.meta.env.VITE_*` — never hardcode URLs or keys.**

---

## Source Structure

```
frontend/src/
├── components/
│   ├── auth/          # Login.jsx, Register.jsx
│   ├── common/        # Header.jsx, Footer.jsx, Navbar.jsx
│   └── products/      # ProductCard.jsx, ProductDetail.jsx
├── context/
│   └── AuthContext.jsx  # JWT token, user state
├── pages/
│   ├── Home.jsx
│   ├── Products.jsx
│   ├── Cart.jsx
│   ├── Orders.jsx
│   ├── Profile.jsx
│   └── AdminDashboard.jsx
└── services/
    ├── api.js          # axios base instance (reads VITE_API_URL)
    ├── auth.js
    ├── cart.js
    ├── order.js
    └── payment.js
```

---

## Stripe Integration

```javascript
import { loadStripe } from '@stripe/stripe-js';

// 1. Init Stripe
const stripe = await loadStripe(import.meta.env.VITE_STRIPE_PUBLISHABLE_KEY);

// 2. Create PaymentIntent
const { clientSecret, paymentIntentId } = await fetch(`${VITE_API_URL}/payment/create-intent`, {
  method: 'POST',
  headers: { Authorization: `Bearer ${token}`, 'Content-Type': 'application/json' },
  body: JSON.stringify({ username, amount, currency: 'INR' })
}).then(r => r.json());

// 3. Confirm payment with Stripe.js (handles 3DS, card UI etc.)
const { error } = await stripe.confirmPayment({ elements, confirmParams: { ... } });

// 4. On success, notify backend
await fetch(`${VITE_API_URL}/payment/confirm`, {
  method: 'POST',
  body: JSON.stringify({ paymentIntentId, paymentId, amount })
});
```

---

## Key API Endpoints

| Feature | Method | Path | Auth |
|---------|--------|------|------|
| Register | POST | `/auth/register` | No |
| Login | POST | `/auth/login` | No |
| List products | GET | `/products` | No |
| Get product | GET | `/products/{id}` | No |
| Add to cart | POST | `/cart/add?productId=&quantity=` | JWT |
| Get cart | GET | `/cart/items?username=` | JWT |
| My orders | GET | `/orders/user/{username}` | JWT |
| Create payment intent | POST | `/payment/create-intent` | JWT |
| Confirm payment | POST | `/payment/confirm` | JWT |
| Stripe key | GET | `/payment/key` | No |

---

## ID Types — IMPORTANT

All entity IDs are **String** (MongoDB ObjectId format: `"64abc123def456..."`).\
Do **not** treat IDs as numbers. Always compare with `===` not `==`.

---

## Authentication

JWT is stored in `localStorage` (or `sessionStorage`).
Include in every protected request:
```javascript
headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
```

---

## Agent Rules

1. Never hardcode `localhost:8080` — use `import.meta.env.VITE_API_URL`
2. Never hardcode Stripe key — use `import.meta.env.VITE_STRIPE_PUBLISHABLE_KEY`
3. Payment gateway is **Stripe only** — Razorpay removed
4. All IDs are `String` — not `number`
5. This is a SPA — use React Router, not page reloads
6. After backend API changes, update this file

---

## Changelog

| Date | Change |
|------|--------|
| 2026-09-12 | Project renamed Sales-Savvy → ShopSphere; folders renamed |
| 2026-09-12 | Razorpay → Stripe; MySQL → MongoDB (IDs now String) |
| 2026-09-12 | Endpoints: `/payment/create-intent` + `/payment/confirm` |
| 2026-09-12 | Render deployment added: `VITE_API_URL` + `VITE_STRIPE_PUBLISHABLE_KEY` |
| 2026-09-12 | **Bug fixes**: `services/payment.js` rewritten for Stripe (was Razorpay); `services/order.js` corrupted duplicate code fixed + razorpayOrderId → stripePaymentIntentId; `Cart.jsx` full Stripe checkout flow with CardElement modal; `AdminOrders.jsx` fixed razorpayOrderId field + removed invalid ACCEPTED status; `OrderCard.jsx` fixed item.subtotal (field doesn't exist) → price×qty, fixed PENDING status (use CREATED); `Login.jsx`+`Register.jsx` branding SalesSavvy → ShopSphere; `Orders.jsx`+`Profile.jsx` `<a href>` → `<Link>` SPA fix; `Profile.jsx` broken `/edit-profile` route removed; `Home.jsx` Razorpay → Stripe branding; `backend/.env` MySQL/Razorpay → MongoDB/Stripe vars; installed `@stripe/stripe-js` + `@stripe/react-stripe-js` |