# AGENT.md — ShopSphere

## Agent Instructions

> 🛑 **CRITICAL AI AGENT INSTRUCTION** 🛑
> 1. **BEFORE making ANY changes:** You MUST read this `AGENT.md` file completely, and then read the specific feature documentation inside the `docs/` folder.
> 2. **AFTER making ANY changes:** You MUST update the relevant files in the `docs/` folder to accurately reflect your new implementation or architectural changes.

---

## Project Overview

**ShopSphere** is a full-stack e-commerce web application.

| Layer     | Technology                          | Location    |
|-----------|-------------------------------------|-------------|
| Backend   | Spring Boot 3.5, Java 17, MongoDB   | `backend/`  |
| Frontend  | React 18, Vite, Bootstrap 5         | `frontend/` |
| Database  | MongoDB Atlas (free tier)           | Atlas Cloud |
| Payment   | Stripe Payment Intents              | Stripe API  |
| Hosting   | Render (free tier)                  | render.yaml |

---

## Architecture at a Glance

```
ShopSphere/
├── backend/                   # Spring Boot REST API
│   ├── src/main/java/com/shopSphere/
│   │   ├── auth/              # JWT auth, login, register
│   │   ├── user/              # User CRUD
│   │   ├── product/           # Product catalog
│   │   ├── cart/              # Shopping cart (embedded CartItems)
│   │   ├── order/             # Orders (embedded OrderItems)
│   │   ├── payment/           # Stripe payment
│   │   └── shared/            # Config, Security, Exceptions
│   ├── Dockerfile
│   └── .env.example
├── frontend/                  # React SPA
│   ├── src/
│   │   ├── components/
│   │   ├── pages/
│   │   ├── context/
│   │   └── services/
│   └── .env.example
├── docs/
│   ├── architecture.md        # System design and data flow
│   ├── deployment.md          # Render deploy steps
│   ├── database.md            # MongoDB schema
│   ├── config.md              # Environment variables reference
│   └── modules/               # Feature-level docs
│       ├── auth.md
│       ├── users.md
│       ├── products.md
│       ├── cart.md
│       ├── orders.md
│       ├── payment.md
│       └── security.md
├── render.yaml                # Render IaC
├── .gitignore
└── AGENT.md                   # This file

```

---

## MongoDB Collections

| Collection | Document Type | Notes |
|------------|---------------|-------|
| `users`    | Users         | Indexed on `username`, `email` |
| `products` | Product       | `@Version` for optimistic locking |
| `carts`    | Cart          | CartItems **embedded** |
| `orders`   | Orders        | OrderItems **embedded** |

**All IDs are `String` (MongoDB ObjectId format).**

---

## Payment Flow (Stripe)

```
Frontend            Backend            Stripe
   |                   |                  |
   |-- POST /payment/create-intent ------->|
   |<-- { clientSecret, paymentIntentId } |
   |                   |                  |
   |-- Stripe.js confirmPayment ---------->|
   |<-- Payment confirmed                 |
   |                   |                  |
   |-- POST /payment/confirm ------------>|
   |   { paymentIntentId, amount }        |
   |<-- { orderId, status: PAID }         |
```

---

## Environment Variables

See [`docs/config.md`](docs/config.md) for the full reference.

| Variable | Used By | Example |
|----------|---------|---------|
| `MONGODB_URI` | Backend | `mongodb+srv://...` |
| `JWT_SECRET` | Backend | 32+ char base64 string |
| `STRIPE_SECRET_KEY` | Backend | `sk_live_...` |
| `STRIPE_PUBLISHABLE_KEY` | Both | `pk_live_...` |
| `STRIPE_WEBHOOK_SECRET` | Backend | `whsec_...` |
| `CORS_ALLOWED_ORIGINS` | Backend | Frontend URL |
| `VITE_API_URL` | Frontend | Backend URL |

---

## Key Rules for Agents

1. **Never hardcode URLs** — always read from env vars (`${VARIABLE}` in backend, `import.meta.env.VITE_*` in frontend)
2. **Never commit `.env`** — only `.env.example` is tracked by git
3. **IDs are always `String`** — MongoDB ObjectId, not `Long`/`number`
4. **CartItems are embedded in Cart** — no separate `CartItem` collection
5. **OrderItems are embedded in Orders** — no separate `OrderItem` collection
6. **Payment gateway is Stripe only** — Razorpay has been removed
7. **Update `docs/modules/<feature>.md`** after changing any feature
8. **Update `frontend/agents.md`** after any frontend-relevant change

---

## Docs Reference

| What you want to know | Read |
|-----------------------|------|
| System design, data flow | [`docs/architecture.md`](docs/architecture.md) |
| Deploy to Render | [`docs/deployment.md`](docs/deployment.md) |
| MongoDB schemas | [`docs/database.md`](docs/database.md) |
| All env variables | [`docs/config.md`](docs/config.md) |
| Auth / JWT | [`docs/modules/auth.md`](docs/modules/auth.md) |
| User management | [`docs/modules/users.md`](docs/modules/users.md) |
| Product catalog | [`docs/modules/products.md`](docs/modules/products.md) |
| Cart logic | [`docs/modules/cart.md`](docs/modules/cart.md) |
| Order lifecycle | [`docs/modules/orders.md`](docs/modules/orders.md) |
| Stripe payment | [`docs/modules/payment.md`](docs/modules/payment.md) |
| Security / CORS | [`docs/modules/security.md`](docs/modules/security.md) |