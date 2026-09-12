# ShopSphere - Backend API

The backend for **ShopSphere**, a full-stack e-commerce platform built with Spring Boot, MongoDB, and Stripe.

## 🚀 Tech Stack

- **Framework:** Spring Boot 3.5 (Java 17)
- **Database:** MongoDB (using Spring Data MongoDB)
- **Security:** Spring Security 6 + JWT (JSON Web Tokens)
- **Payments:** Stripe API (Payment Intents)
- **Build Tool:** Maven
- **Containerization:** Docker

---

## 🛠️ Prerequisites

Before running the backend, ensure you have the following installed:
- [Java 17 JDK](https://adoptium.net/)
- [Maven](https://maven.apache.org/) (or use the included `mvnw` wrapper)
- A running instance of **MongoDB** (Local or MongoDB Atlas)
- A **Stripe** account for payment API keys

---

## ⚙️ Environment Configuration

1. Locate the `.env.example` file in the `backend/` directory.
2. Copy it to create your local environment file:
   ```bash
   cp .env.example .env
   ```
3. Open `.env` and fill in the required values:

| Variable | Description | Default / Example |
|----------|-------------|-------------------|
| `MONGODB_URI` | Your MongoDB connection string | `mongodb://localhost:27017/shopsphere` |
| `JWT_SECRET` | 32+ character Base64 string for token signing | *Cannot be empty* |
| `JWT_EXPIRATION`| Token lifespan in milliseconds | `86400000` (1 day) |
| `STRIPE_SECRET_KEY` | Your Stripe secret key | `sk_test_...` |
| `STRIPE_PUBLISHABLE_KEY` | Your Stripe publishable key | `pk_test_...` |
| `CORS_ALLOWED_ORIGINS` | Permitted frontend origins | `http://localhost:5173` |

---

## 🏃‍♂️ Running the Application

### Option 1: Using Maven (Local)

You can run the application directly using the Maven wrapper:

**Windows:**
```cmd
mvnw.cmd clean spring-boot:run
```

**Mac/Linux:**
```bash
./mvnw clean spring-boot:run
```
The server will start on `http://localhost:8080`.

### Option 2: Using Docker

A production-ready `Dockerfile` is included using a multi-stage build.

1. Build the image:
   ```bash
   docker build -t shopsphere-backend .
   ```
2. Run the container:
   ```bash
   docker run -p 8080:8080 --env-file .env shopsphere-backend
   ```

---

## 📁 Project Structure

```
src/main/java/com/shopSphere/
├── auth/          # JWT authentication and filters
├── cart/          # Shopping cart logic (Items embedded)
├── order/         # Order processing and history
├── payment/       # Stripe PaymentIntent integration
├── product/       # Product catalog CRUD
├── shared/        # Global configs (CORS, Security, Exceptions, Mongo Tx)
└── user/          # User management and profiles
```

---

## 📚 Documentation

For deep-dive technical details regarding system architecture, data models, and API behaviors, please see the `docs/` folder located in this directory (or the repository root).

> **Note for AI Agents:** Please read `agents.md` before modifying this backend.
