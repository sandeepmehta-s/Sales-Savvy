# Deployment — ShopSphere on Render

## Prerequisites

1. MongoDB Atlas account — [cloud.mongodb.com](https://cloud.mongodb.com)
2. Stripe account — [dashboard.stripe.com](https://dashboard.stripe.com)
3. GitHub repo with ShopSphere code
4. Render account — [render.com](https://render.com)

---

## Step 1: MongoDB Atlas Setup

1. Create a free cluster (M0)
2. Create a database user with read/write access
3. Network Access → Add IP Address → `0.0.0.0/0` (allow all — required for Render)
4. Get connection string: `mongodb+srv://<user>:<pass>@cluster.mongodb.net/shopsphere`

---

## Step 2: Stripe Setup

1. Get **Secret Key** (`sk_live_...`) from Stripe Dashboard → Developers → API keys
2. Get **Publishable Key** (`pk_live_...`) — safe for frontend
3. Set up Webhook:
   - Endpoint URL: `https://shopsphere-backend.onrender.com/payment/webhook`
   - Events: `payment_intent.succeeded`
   - Copy **Webhook Secret** (`whsec_...`)

---

## Step 3: Deploy to Render (Blueprint)

```bash
# Push code to GitHub
git push origin main
```

1. Render Dashboard → New → Blueprint
2. Connect GitHub repo
3. Render reads `render.yaml` automatically — creates both services

### Backend Environment Variables (set in Render dashboard)

| Variable | Value |
|----------|-------|
| `MONGODB_URI` | `mongodb+srv://...` |
| `JWT_SECRET` | 32+ char random string |
| `JWT_EXPIRATION` | `86400000` |
| `STRIPE_SECRET_KEY` | `sk_live_...` |
| `STRIPE_PUBLISHABLE_KEY` | `pk_live_...` |
| `STRIPE_WEBHOOK_SECRET` | `whsec_...` |
| `CORS_ALLOWED_ORIGINS` | `https://shopsphere-frontend.onrender.com` |
| `ADMIN_PASSWORD` | Strong password |
| `SPRING_PROFILES_ACTIVE` | `prod` |

### Frontend Environment Variables

| Variable | Value |
|----------|-------|
| `VITE_API_URL` | `https://shopsphere-backend.onrender.com` |
| `VITE_STRIPE_PUBLISHABLE_KEY` | `pk_live_...` |

---

## Step 4: Keep Free Tier Alive

Render free tier spins down after 15 min of inactivity. Use **UptimeRobot**:
- URL: `https://shopsphere-backend.onrender.com/auth/test`
- Interval: every 14 minutes
- Monitor type: HTTP(s)

---

## Docker (Local)

```bash
cd backend
docker build -t shopsphere-backend .
docker run -p 8080:8080 \
  -e MONGODB_URI=mongodb://host.docker.internal:27017/shopsphere \
  -e JWT_SECRET=dev-secret \
  -e STRIPE_SECRET_KEY=sk_test_... \
  shopsphere-backend
```

---

## Health Check

- Backend: `GET /auth/test` → `200 OK`
- Frontend: served as static files, SPA routing via `/* → /index.html`