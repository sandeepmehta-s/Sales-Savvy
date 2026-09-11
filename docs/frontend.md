# Feature: Frontend

## Overview
React 18 + Vite app. Bootstrap 5 + Bootstrap Icons for UI.
JWT token localStorage mein store hota hai. AuthContext globally state manage karta hai.

## Tech Stack
| Tool | Purpose |
|------|---------|
| React 18 | UI framework |
| Vite | Build tool |
| React Router v6 | Client-side routing |
| Axios | HTTP client |
| Bootstrap 5 | CSS framework |
| Bootstrap Icons | Icons |

## Routes (App.jsx)
| Path | Component | Auth |
|------|-----------|------|
| `/` | Home | Public |
| `/products` | Products | Public |
| `/products/:id` | ProductDetail | Public |
| `/cart` | Cart | Soft (redirects) |
| `/orders` | Orders | Required |
| `/profile` | Profile | Required |
| `/login` | Login | Public |
| `/register` | Register | Public |
| `/admin` | AdminDashboard | Admin only |
| `/admin/users` | AdminUsers | Admin only |

## Services (API Calls)
| File | Exports |
|------|---------|
| `api.js` | Axios instance with JWT interceptor + 401 redirect |
| `auth.js` | signup(user), signin(credentials) |
| `cart.js` | getCart, addToCart, updateCartItem, removeFromCart, clearCart, getCartTotal |
| `order.js` | createOrder, getUserOrders, getOrderById, cancelOrder, getAllOrders, updateOrderStatus |
| `payment.js` | createOrder(amount, username), verifyPayment(paymentData), getKey() |
| `product.js` | getAllProducts, getProductById, searchProducts, addProduct, updateProduct, deleteProduct |
| `user.js` | getUserProfile, updateUser, getAllUsers, deleteUser |

## AuthContext
`context/AuthContext.jsx` — global state:
- `user` — `{ username, role, token }`
- `login(userData)` — localStorage set + state update
- `logout()` — localStorage clear + state reset
- `isAdmin()` — `user?.role === "ROLE_ADMIN"` check

## API Base URL
```js
// services/api.js
baseURL: import.meta.env.VITE_API_URL || 'http://localhost:8080'
```
Development mein `.env` file banao:
```
VITE_API_URL=http://localhost:8080
```

## JWT Interceptor
`api.js` mein automatically har request pe token add hota hai:
```js
config.headers.Authorization = `Bearer ${token}`
```
401 response aane pe → localStorage clear → `/login` redirect.

## Pages Overview
| Page | Features |
|------|---------|
| Home | Hero section, featured products |
| Products | Product listing, search, category filter |
| ProductDetail | Product info, add to cart |
| Cart | Cart items, quantity update, checkout → payment |
| Orders | User ke saare orders, status, cancel |
| Profile | User info |
| AdminDashboard | Stats: total sales, order count, user count |
| AdminOrders | All orders, status update |
| AdminProducts | Product CRUD, stock management |
| AdminUsers | User list, delete |

## Rules (Agent ke liye)
- Backend URL kabhi hardcode mat karo — `VITE_API_URL` use karo
- Naya route add karo → `App.jsx` mein add karo + is doc mein routes table update karo
- Admin pages pe manually admin check karo: `AuthContext` se `isAdmin()` use karo
- Naya service method add karo → corresponding `services/*.js` file update karo + is table update karo

## Changelog
- 2026-09-12 Initial docs created
