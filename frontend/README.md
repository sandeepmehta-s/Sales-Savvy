# ShopSphere - Frontend SPA

The frontend for **ShopSphere**, a modern, responsive Single Page Application (SPA) built for a seamless e-commerce experience.

## Tech Stack

- **Framework:** React 18
- **Build Tool:** Vite
- **Styling:** Bootstrap 5 (with React-Bootstrap)
- **Routing:** React Router DOM
- **Payments:** Stripe.js & React Stripe Elements
- **API Client:** Axios

---

## Prerequisites

Before running the frontend, ensure you have the following installed:
- [Node.js](https://nodejs.org/) (v16 or higher)
- [npm](https://www.npmjs.com/) (Node Package Manager)

---

## Environment Configuration

1. Locate the `.env.example` file in the `frontend/` directory.
2. Copy it to create your local environment file:
   ```bash
   cp .env.example .env
   ```
3. Open `.env` and fill in the required values:

| Variable | Description | Default / Example |
|----------|-------------|-------------------|
| `VITE_API_URL` | The URL of your Spring Boot backend | `http://localhost:8080` |
| `VITE_STRIPE_PUBLISHABLE_KEY` | Your public Stripe testing key | `pk_test_...` |

*(Note: Never hardcode these values in the source code. Always use `import.meta.env.VITE_*`)*

---

## Running the Application

### Local Development Server

1. Install all required dependencies:
   ```bash
   npm install
   ```
2. Start the Vite development server:
   ```bash
   npm run dev
   ```
3. Open your browser and navigate to `http://localhost:5173`.

*(Note: Vite supports Fast Refresh (HMR), so your changes will instantly appear in the browser without reloading).*

### Building for Production

To create an optimized production build:
```bash
npm run build
```
This will compile and minify all assets into the `dist/` directory, which can be deployed to any static hosting service (Render, Vercel, Netlify, etc.).

---

## Project Structure

```
frontend/src/
├── components/
│   ├── auth/          # Login.jsx, Register.jsx
│   ├── common/        # Header, Footer, Navbar
│   └── products/      # Product cards and detailed views
├── context/
│   └── AuthContext.jsx # Global JWT and User state management
├── pages/
│   ├── Home.jsx       # Landing page
│   ├── Products.jsx   # Catalog
│   ├── Cart.jsx       # Shopping cart & Stripe checkout modal
│   ├── Orders.jsx     # Order history
│   ├── Profile.jsx    # User settings
│   └── AdminDashboard.jsx # Admin inventory/order management
└── services/
    ├── api.js         # Base Axios instance
    ├── auth.js        # Registration & login APIs
    ├── cart.js        # Cart management APIs
    └── payment.js     # Stripe Intent lifecycle APIs
```

---

## Documentation

For deep-dive technical details regarding system architecture, data models, and API behaviors, please see the `docs/` folder located in the repository root.

> **Note for AI Agents:** Please read `agents.md` before modifying this frontend.
