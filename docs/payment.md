# Feature: Payment

## Overview
Razorpay payment gateway use hota hai. Frontend Razorpay checkout open karta hai.
Payment successful hone ke baad backend signature verify karta hai aur atomically order create karta hai.

## Backend Files
| File | Purpose |
|------|---------|
| `controller/PaymentController.java` | `/payment/**` endpoints |
| `service/PaymentService.java` | Interface |
| `service/PaymentServiceImpl.java` | Razorpay API calls |
| `dto/PaymentRequest.java` | Create order request DTO |
| `dto/PaymentVerifyRequest.java` | Verify payment request DTO |

## API Endpoints

### POST `/payment/create-order` — Auth required
Razorpay order create karo (payment shuru karne se pehle).
- **Request body:** `{ amount: int (paise mein), username: string }`
- **Response:** `{ orderId, amount, currency, key }`
- `amount` **paise mein** hona chahiye — 100 rupees = 10000 paise

### POST `/payment/verify` — Auth required
Payment verify karo aur order create karo (ATOMIC).
- **Request body:** `{ orderId, paymentId, signature, amount }`
- **Steps (backend):**
  1. Razorpay signature verify karo
  2. `createOrderAndMarkPaid()` atomic call — order + PAID status ek transaction mein
- **Response:** `{ status: "success", orderId, amount }` ya `{ status: "error", message }`

### GET `/payment/key` — Auth required
Frontend ke liye Razorpay public key.

## Complete Payment Flow

```
1. User "Pay" click karta hai
2. Frontend: cartTotal calculate karo (paise mein multiply by 100)
3. Frontend → POST /payment/create-order { amount, username }
4. Backend → Razorpay API se orderId milta hai
5. Frontend → Razorpay Checkout open karo (orderId, amount, key)
6. User → Razorpay UI mein card/UPI details dalo
7. Razorpay → success callback mein { razorpay_order_id, razorpay_payment_id, razorpay_signature } milta hai
8. Frontend → POST /payment/verify { orderId, paymentId, signature, amount }
9. Backend → HMAC-SHA256 signature verify karo
10. Backend → createOrderAndMarkPaid() — stock deduct, order save, cart clear, status=PAID
11. Frontend → success page / orders page pe redirect
```

## Signature Verification
```java
// Razorpay signature = HMAC-SHA256(orderId + "|" + paymentId, keySecret)
Utils.verifyPaymentSignature(payload, keySecret)
// throws RazorpayException if invalid → return 400
```

## Frontend Files
| File | Purpose |
|------|---------|
| `services/payment.js` | createOrder, verifyPayment, getKey |
| `pages/Cart.jsx` | Payment initiation from cart |

## Environment Variables Required
```
RAZORPAY_KEY_ID=rzp_test_...      # Public key (frontend ko bhi chahiye via /payment/key)
RAZORPAY_KEY_SECRET=...           # Secret key (backend only, kabhi frontend pe nahi)
```

## Rules (Agent ke liye)
- `amount` paise mein hai Razorpay mein — `getTotalSales()` mein 100 se divide karo rupees ke liye
- `RAZORPAY_KEY_SECRET` kabhi frontend pe expose mat karo
- Payment verify ke baad hamesha `createOrderAndMarkPaid()` use karo — do alag calls nahi
- Test mode: `rzp_test_*` keys use karo — production mein `rzp_live_*`

## Changelog
- 2026-09-12 verifyPayment now uses atomic createOrderAndMarkPaid() (bug fix: non-atomic order creation)
- 2026-09-12 Initial docs created
