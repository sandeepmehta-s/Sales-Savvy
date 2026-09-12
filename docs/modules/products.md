# Products Module — ShopSphere

## Overview
Product catalog management. Public read, admin write.

## Package
`com.salesSavvy.product`

## MongoDB Document

```json
{ "_id": "String", "name": "String (unique)", "description": "String",
  "price": "Decimal128", "photo": "String (URL)", "category": "String",
  "stockQuantity": "int", "version": "Long", "reviews": ["String"] }
```

> `version` is the optimistic lock field — **never manually set this field**.

## Endpoints

| Method | Path | Auth | Role | Description |
|--------|------|------|------|-------------|
| GET | `/products` | No | — | List all products |
| GET | `/products/{id}` | No | — | Get product by ID |
| GET | `/products/search?keyword=` | No | — | Search by name |
| GET | `/products/category/{cat}` | No | — | Filter by category |
| GET | `/products/categories` | No | — | List all categories |
| POST | `/products` | JWT | ADMIN | Create product |
| PUT | `/products/{id}` | JWT | ADMIN | Update product |
| DELETE | `/products/{id}` | JWT | ADMIN | Delete product |

## Stock Management

- `stockQuantity` is decremented atomically when an order is placed
- `Product.hasStock(qty)` — checks if enough stock available
- `Product.deductStock(qty)` — deducts + throws if insufficient
- `@Version Long version` — prevents two simultaneous orders from over-selling

## Key Files

- `product/controller/ProductController.java`
- `product/entity/Product.java`
- `product/repository/ProductRepository.java`
- `product/service/ProductServiceImplementation.java`