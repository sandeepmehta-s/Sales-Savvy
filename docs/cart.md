# Feature: Cart

## Overview
Har user ka ek cart hota hai (1:1 relationship). Cart mein multiple CartItems hote hain.
Cart items persist karte hain — session ke baad bhi.

## Backend Files
| File | Purpose |
|------|---------|
| `controller/CartController.java` | `/cart/**` endpoints |
| `entity/Cart.java` | Cart entity (DB table: `carts`) |
| `entity/CartItem.java` | CartItem entity (DB table: `cart_items`) |
| `service/CartService.java` | Interface |
| `service/CartServiceImplementation.java` | Business logic |
| `repository/CartRepository.java` | JPA queries |
| `repository/CartItemRepository.java` | JPA queries |
| `dto/CartResponse.java` | Cart response DTO |
| `dto/CartItemResponse.java` | CartItem response DTO |

## Entity Fields

### Cart
| Field | Notes |
|-------|-------|
| id | PK |
| user | OneToOne → Users |
| cartItems | OneToMany → CartItem |

### CartItem
| Field | Notes |
|-------|-------|
| id | PK |
| cart | ManyToOne → Cart |
| product | ManyToOne → Product |
| quantity | int |

## API Endpoints

### POST `/cart/add?productId=&quantity=` — Auth required
Cart mein product add karo. `username` JWT Principal se nikalta hai automatically.
- Agar product already cart mein hai → quantity add ho jaati hai (replace nahi)

### PUT `/cart/update?username=&productId=&quantity=` — Auth required
Quantity update karo. quantity=0 dena → item remove ho jaati hai.

### DELETE `/cart/remove?username=&productId=` — Auth required
Single item remove karo.

### DELETE `/cart/clear?username=` — Auth required
Poora cart khali karo (order place hone ke baad automatically call hota hai).

### GET `/cart/items?username=` — Auth required
Cart items list. Response: `CartResponse { cartId, username, items[] }`

### GET `/cart/count?username=` — Auth required
Cart mein kitne distinct items hain (quantity nahi, item count).

### GET `/cart/total?username=` — Auth required
Cart ka total value (Double, rupees mein).

## Important Behavior

- `getCartItems()` — agar cart exist nahi karta (naya user) → **empty list return hoti hai**, exception nahi
- Cart automatically create hoti hai jab pehla item add hota hai
- Order place hone ke baad cart automatically clear ho jaati hai

## Frontend Files
| File | Purpose |
|------|---------|
| `services/cart.js` | getCart, addToCart, updateCartItem, removeFromCart, clearCart, getCartTotal |
| `pages/Cart.jsx` | Cart page UI |

## Rules (Agent ke liye)
- `addToCart` endpoint pe `username` query param nahi hai — `Principal` se automatically aata hai
- `updateCartItem` / `removeFromCart` / `clearCart` pe username verify karo vs Principal
- `getCartItems()` kabhi exception throw nahi karti (empty list return karo) — `createOrderFromCart` check karta hai

## Changelog
- 2026-09-12 getCartItems() now returns empty list instead of ResourceNotFoundException (bug fix)
- 2026-09-12 Initial docs created
