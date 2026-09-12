# Payment Module — ShopSphere (Stripe)

## Overview
Handles payment processing via Stripe Payment Intents.

## Package
`com.salesSavvy.payment`

## Environment Variables

| Variable | Description |
|----------|-------------|
| `STRIPE_SECRET_KEY` | Server-side key (`sk_live_...`) |
| `STRIPE_PUBLISHABLE_KEY` | Client-safe key (`pk_live_...`) returned to frontend |
| `STRIPE_WEBHOOK_SECRET` | Webhook signature secret (`whsec_...`) |

## Endpoints

| Method | Path | Auth | Description |
|--------|------|------|-------------|
| POST | `/payment/create-intent` | JWT | Create Stripe PaymentIntent, return clientSecret |
| POST | `/payment/confirm` | JWT | Confirm payment + create order atomically |
| GET | `/payment/key` | No | Returns publishable key for Stripe.js init |

## Payment Flow

### Step 1 — Create Intent
```
POST /payment/create-intent
Body: { username, amount (paise), currency: "INR" }

Response: { clientSecret, paymentIntentId, publishableKey, amount, currency }
```

### Step 2 — Frontend confirms payment
```javascript
const stripe = await loadStripe(publishableKey);
const { error } = await stripe.confirmPayment({
  elements,           // Stripe Elements with clientSecret
  confirmParams: { return_url: window.location.origin }
});
```

### Step 3 — Confirm with backend
```
POST /payment/confirm
Body: { paymentIntentId, paymentId, amount }

Response: { status: "success", orderId, amount }
```

## Key Files

- `payment/controller/PaymentController.java`
- `payment/service/PaymentServiceImpl.java` — wraps `Stripe.apiKey`, calls `PaymentIntent.create()`
- `payment/dto/PaymentRequest.java` — amount in paise, currency
- `payment/dto/PaymentVerifyRequest.java` — paymentIntentId, paymentId, amount

## Currency Note

Amount is always in **paise** (INR smallest unit).
- ₹100 = 10000 paise
- Minimum Stripe charge: ₹0.50 = 50 paise