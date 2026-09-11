# Feature: Database Schema

## Overview
MySQL database, name: `ecom`. Hibernate `ddl-auto=update` — schema auto-update hota hai.

## Tables

### `users`
| Column | Type | Constraints |
|--------|------|-------------|
| id | BIGINT | PK, AUTO_INCREMENT |
| username | VARCHAR | UNIQUE, NOT NULL, 4–20 chars |
| email | VARCHAR | UNIQUE, NOT NULL, valid email |
| password | VARCHAR | NOT NULL, BCrypt encoded |
| gender | VARCHAR | nullable |
| dob | VARCHAR | nullable |
| role | VARCHAR | NOT NULL (`ROLE_USER` / `ROLE_ADMIN`) |

### `carts`
| Column | Type | Constraints |
|--------|------|-------------|
| id | BIGINT | PK, AUTO_INCREMENT |
| user_id | BIGINT | FK → users.id, UNIQUE (1:1) |

### `cart_items`
| Column | Type | Constraints |
|--------|------|-------------|
| id | BIGINT | PK, AUTO_INCREMENT |
| cart_id | BIGINT | FK → carts.id |
| product_id | BIGINT | FK → products.id |
| quantity | INT | NOT NULL |

### `products`
| Column | Type | Constraints |
|--------|------|-------------|
| id | BIGINT | PK, AUTO_INCREMENT |
| name | VARCHAR | UNIQUE, NOT NULL, 3–50 chars |
| description | VARCHAR(500) | nullable |
| price | DECIMAL | NOT NULL, min 1 |
| photo | VARCHAR | nullable (image URL) |
| category | VARCHAR | NOT NULL |
| stock_quantity | INT | NOT NULL, default 0 |
| version | BIGINT | NOT NULL (optimistic locking) |

### `product_reviews`
| Column | Type | Constraints |
|--------|------|-------------|
| product_id | BIGINT | FK → products.id |
| review | VARCHAR | review text |

### `orders`
| Column | Type | Constraints |
|--------|------|-------------|
| id | BIGINT | PK, AUTO_INCREMENT |
| razorpay_order_id | VARCHAR | UNIQUE, NOT NULL |
| amount | DECIMAL | NOT NULL (paise mein) |
| currency | VARCHAR | default "INR" |
| status | VARCHAR | CREATED/PAID/CANCELLED/SHIPPED/DELIVERED |
| receipt | VARCHAR | nullable |
| payment_id | VARCHAR | nullable (Razorpay payment ID) |
| user_id | BIGINT | FK → users.id |
| created_at | DATETIME | auto-set on insert |
| updated_at | DATETIME | auto-set on insert/update |

### `order_items`
| Column | Type | Constraints |
|--------|------|-------------|
| id | BIGINT | PK, AUTO_INCREMENT |
| order_id | BIGINT | FK → orders.id |
| product_id | BIGINT | FK → products.id |
| product_name | VARCHAR | snapshot at order time |
| price | DECIMAL | snapshot at order time |
| quantity | INT | |

## Entity Relationships
```
Users (1) ──── (1) Cart
Users (1) ──── (N) Orders
Cart (1) ──── (N) CartItem
CartItem (N) ──── (1) Product
Orders (1) ──── (N) OrderItem
OrderItem (N) ──── (1) Product
Product (1) ──── (N) product_reviews (embedded)
```

## Important Notes
- `order_items.product_name` aur `order_items.price` snapshot hain order time ke
  - Product baad mein delete ho toh bhi order history intact rahegi
- `products.stock_quantity` inventory track karta hai — order create hone pe deduct hota hai
- `products.version` JPA @Version hai — kabhi manually set mat karo
- `orders.amount` paise mein store hota hai — display ke liye 100 se divide karo

## Migration Notes
- `stock_quantity` aur `version` columns naaye hain (`products` table mein)
- `ddl-auto=update` pe automatically add ho jaayenge next restart pe
- Existing products ka `stock_quantity = 0` hoga — manually set karna hoga

## Rules (Agent ke liye)
- Naya entity field add karo → is doc ka corresponding table update karo
- `@Version` aur `stock_quantity` products table mein kabhi remove mat karo
- Order items mein price/name snapshot store karo — live product reference pe depend mat karo display ke liye

## Changelog
- 2026-09-12 products.stock_quantity, products.version columns added
- 2026-09-12 Initial docs created
