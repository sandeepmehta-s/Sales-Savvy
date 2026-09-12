# ShopSphere

<div align="center">
 <h3>A modern, full-stack E-Commerce platform built with React, Spring Boot, MongoDB, and Stripe.</h3>
</div>

---

## Problem Statement

Building a reliable e-commerce platform requires handling complex asynchronous workflows, preventing race conditions during checkout (like overselling inventory), and ensuring secure payment processing. Many monolithic architectures struggle with these requirements.

**ShopSphere** was designed to solve these issues by:
1. **Preventing Race Conditions:** Utilizing MongoDB's Optimistic Locking (`@Version`) to guarantee that concurrent users purchasing the same item do not oversell the available stock.
2. **Secure Payments:** Offloading payment compliance (PCI) to **Stripe**, using Payment Intents and client-side 3D Secure verification before the backend finalizes the order atomically.
3. **Responsive SPA:** Delivering a fast, seamless shopping experience using React Router without full-page reloads.

---

## Features

### For Customers
- **Authentication & Authorization:** Secure JWT-based login and registration.
- **Product Catalog:** Browse products, filter by category, and search by keywords.
- **Shopping Cart:** Add, update, or remove products. Cart items are embedded directly in the Cart document for blazing-fast retrieval.
- **Secure Checkout:** Integrated **Stripe** payment gateway for seamless and secure credit card processing.
- **Order History:** View past orders and payment receipts.

### For Administrators
- **Inventory Management:** Full CRUD operations on products. Update stock quantities easily.
- **Order Tracking:** View all customer orders across the platform.
- **User Management:** Monitor registered users and manage access.

---

## Technology Stack

| Layer | Technology |
|-------|------------|
| **Frontend** | React 18, Vite, Bootstrap 5, React Router |
| **Backend** | Spring Boot 3.5, Java 17, Spring Security |
| **Database** | MongoDB Atlas (Spring Data MongoDB) |
| **Authentication**| JSON Web Tokens (jjwt) |
| **Payments** | Stripe Java SDK & Stripe.js |
| **Deployment** | Docker, Render (Free Tier) |

---

## Quick Start (Local Development)

### 1. Clone the repository
```bash
git clone https://github.com/sandeepmehta-s/shopSphere.git
cd shopSphere
```

### 2. Backend Setup
```bash
cd backend
# Copy the env template
cp .env.example .env
```
Fill in your `JWT_SECRET`, `MONGODB_URI`, and Stripe test keys in `.env`.
```bash
# Run the Spring Boot server
mvnw.cmd clean spring-boot:run
```
*The backend will start on `http://localhost:8080`.*

### 3. Frontend Setup
```bash
cd frontend
# Copy the env template
cp .env.example .env
```
Ensure `VITE_API_URL=http://localhost:8080` and add your Stripe publishable key.
```bash
# Install dependencies and start Vite dev server
npm install
npm run dev
```
*The frontend will start on `http://localhost:5173`.*

---

## Architecture & Documentation

For a deep dive into the system design, database schemas, deployment pipelines, and API flow, check out the [Documentation Directory](./docs/):

- [Architecture & Data Flow](./docs/architecture.md)
- [Database Schema](./docs/database.md)
- [Deployment Guide](./docs/deployment.md)

*(AI Agents: Please read `AGENT.md` before making architectural changes).*

