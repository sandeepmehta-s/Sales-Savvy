# Database — ShopSphere (MongoDB)

## Connection

```
spring.data.mongodb.uri=${MONGODB_URI:mongodb://localhost:27017/shopsphere}
spring.data.mongodb.auto-index-creation=true
```

**Production:** MongoDB Atlas Free Tier (512 MB). IP Whitelist: `0.0.0.0/0` required for Render.

---

## Collections

### `users`
```json
{
  "_id": "ObjectId (String)",
  "username": "john_doe",         // @Indexed(unique=true)
  "email": "john@example.com",    // @Indexed(unique=true)
  "password": "bcrypt-hash",
  "gender": "MALE",
  "dob": "1990-01-15",
  "role": "ROLE_CUSTOMER",        // ROLE_CUSTOMER | ROLE_ADMIN
  "cartId": "ObjectId reference to carts",
  "orderIds": ["ObjectId", "ObjectId"]
}
```

### `products`
```json
{
  "_id": "ObjectId (String)",
  "name": "Wireless Headphones",  // @Indexed(unique=true)
  "description": "...",
  "price": 2999.00,
  "photo": "https://...",
  "category": "Electronics",
  "stockQuantity": 50,
  "version": 1,                   // optimistic lock — DO NOT remove
  "reviews": ["Great product!", "Loved it"]
}
```

### `carts`
```json
{
  "_id": "ObjectId (String)",
  "username": "john_doe",         // @Indexed(unique=true)
  "cartItems": [
    {
      "productId": "ObjectId",
      "productName": "Wireless Headphones",
      "price": 2999.00,           // snapshot at time of adding
      "photoUrl": "https://...",
      "quantity": 2
    }
  ]
}
```
> `cartItems` are **embedded** — no separate collection.

### `orders`
```json
{
  "_id": "ObjectId (String)",
  "stripePaymentIntentId": "pi_xxx", // @Indexed(unique=true)
  "amount": 599800,               // in paise (INR × 100)
  "currency": "INR",
  "status": "PAID",               // CREATED | PAID | CANCELLED | SHIPPED | DELIVERED
  "receipt": "rcpt_1234567890",
  "paymentId": "ch_xxx",
  "username": "john_doe",         // @Indexed
  "items": [
    {
      "productId": "ObjectId",
      "productName": "Wireless Headphones",
      "price": 2999.00,           // snapshot at time of order
      "quantity": 2
    }
  ],
  "createdAt": "ISODate",
  "updatedAt": "ISODate"
}
```
> `items` are **embedded** — price/name are snapshots, never change after order creation.

---

## Indexes

| Collection | Field | Type |
|------------|-------|------|
| users | username | unique |
| users | email | unique |
| products | name | unique |
| carts | username | unique |
| orders | stripePaymentIntentId | unique |
| orders | username | non-unique |

Indexes are created automatically via `spring.data.mongodb.auto-index-creation=true`.

---

## ID Type Migration Note

All IDs were migrated from `Long` (MySQL auto-increment) to `String` (MongoDB ObjectId).
Frontend and API consumers must treat all IDs as strings.