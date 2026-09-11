# Feature: Products

## Overview
Product catalog with CRUD (Admin only for write), search, category filter.
Stock tracking with optimistic locking to prevent overselling.

## Backend Files
| File | Purpose |
|------|---------|
| `controller/ProductController.java` | `/products/**` endpoints |
| `entity/Product.java` | Product entity (DB table: `products`) |
| `service/ProductService.java` | Interface |
| `repository/ProductRepository.java` | JPA queries |

## Entity Fields
| Field | Type | Notes |
|-------|------|-------|
| id | Long | Auto generated PK |
| name | String | Unique, 3–50 chars |
| description | String | Max 500 chars |
| price | BigDecimal | Min 1 |
| photo | String | URL/path to image |
| category | String | Required |
| stockQuantity | int | Default 0 — inventory count |
| version | Long | JPA @Version — optimistic locking |
| reviews | List<String> | Stored in `product_reviews` table |

## API Endpoints

### GET `/products` — Public
All products list.

### GET `/products/{id}` — Public
Single product by ID.

### GET `/products/search?keyword=` — Public
Search by name (case insensitive contains).

### GET `/products/category/{category}` — Public
Products filtered by category.

### GET `/products/categories` — Public
All unique category names.

### POST `/products` — ADMIN ONLY
Create new product. Request body: Product JSON.

### PUT `/products/{id}` — ADMIN ONLY
Update product. Request body: Product JSON.

### DELETE `/products/{id}` — ADMIN ONLY
Delete product.

## Stock Management (Important!)

> stockQuantity field se actual inventory track hoti hai.

- Order create hone pe `deductStock(qty)` call hota hai automatically
- Agar stock < required quantity → `IllegalStateException` throw hoti hai
- `@Version` field JPA optimistic locking provide karta hai
  - Do concurrent transactions ek saath same product update nahi kar sakte safely
  - Second transaction `OptimisticLockException` throw karega → retry karo

### Stock set karna (Admin)
```
PUT /products/{id}
Body: { ..., "stockQuantity": 100 }
```

## Frontend Files
| File | Purpose |
|------|---------|
| `services/product.js` | getAllProducts, getProductById, search, etc. |
| `pages/Products.jsx` | Product listing page |
| `components/products/ProductDetail.jsx` | Single product detail + add to cart |
| `pages/AdminProducts.jsx` | Admin CRUD for products |

## Rules (Agent ke liye)
- Naya product field add karo → `ProductResponse.java` DTO bhi update karo
- Stock ko kabhi directly set mat karo order flow mein — `deductStock()` use karo
- `@Version` field remove mat karo — concurrent order safety ke liye zaruri hai

## Changelog
- 2026-09-12 stockQuantity + @Version added (bug fix: race condition on concurrent orders)
- 2026-09-12 Initial docs created
