# Cart Module — ShopSphere

## Overview
Per-user shopping cart. CartItems are **embedded** inside the Cart document (no separate collection).

## Package
`com.salesSavvy.cart`

## MongoDB Design

```
carts collection:
  Cart { id, username, cartItems: [CartItem, ...] }
  CartItem { productId, productName, price, photoUrl, quantity }
```

CartItem is a plain POJO (no `@Document`). Price and name are snapshots — they do not update if the product price changes after adding to cart.

## Endpoints

| Method | Path | Auth | Description |
|--------|------|------|-------------|
| POST | `/cart/add?productId=&quantity=` | JWT | Add item to cart |
| PUT | `/cart/update?username=&productId=&quantity=` | JWT | Update quantity (0 = remove) |
| DELETE | `/cart/remove?username=&productId=` | JWT | Remove item |
| DELETE | `/cart/clear?username=` | JWT | Clear entire cart |
| GET | `/cart/items?username=` | JWT | Get cart contents |
| GET | `/cart/count?username=` | JWT | Item count |
| GET | `/cart/total?username=` | JWT | Total value (INR) |

## Business Rules

- Adding an existing product increments quantity (no duplicate line items)
- Quantity = 0 removes the item
- Cart is created automatically when user registers
- Cart is cleared after a successful order is placed

## Key Files

- `cart/controller/CartController.java`
- `cart/entity/Cart.java` — `@Document(collection="carts")`
- `cart/entity/CartItem.java` — embedded POJO (no @Document)
- `cart/repository/CartRepository.java` — findByUsername
- `cart/service/CartServiceImplementation.java`