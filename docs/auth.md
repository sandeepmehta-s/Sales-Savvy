# Feature: Authentication

## Overview
JWT-based stateless authentication. Koi session nahi — har request mein Bearer token chahiye.

## Backend Files
| File | Purpose |
|------|---------|
| `controller/AuthController.java` | `/auth/**` endpoints |
| `security/JwtUtil.java` | Token generate / validate |
| `security/JwtAuthenticationFilter.java` | Har request pe token check |
| `security/CustomUserDetailsService.java` | Username se UserDetails load |
| `entity/Users.java` | User entity (DB table: `users`) |
| `entity/UserLoginData.java` | Login request body DTO |
| `dto/AuthResponse.java` | Login/Register response DTO |

## API Endpoints

### POST `/auth/signup`
- **Auth required:** No (public)
- **Request body:** `{ username, email, password, role, gender?, dob? }`
- **Validation:** username 4–20 chars, email valid, password min 8 chars
- **Response:** `{ message, username, role }` — **token nahi milta signup pe**
- **Note:** Role values: `ROLE_USER` ya `ROLE_ADMIN`

### POST `/auth/signin`
- **Auth required:** No (public)
- **Request body:** `{ username, password }`
- **Response:** `{ token, username, role, message }`
- **Token:** JWT, HS256, expiry `.env` ke `JWT_EXPIRATION` se (default 24h = 86400000ms)

### GET `/auth/test`
- **Auth required:** Yes
- **Purpose:** JWT working check karne ke liye test endpoint

## Token Flow
```
Client → POST /auth/signin → receives JWT
Client → stores JWT in localStorage
Client → every API call: Authorization: Bearer <token>
JwtAuthenticationFilter → validates token → sets SecurityContext
```

## Frontend Files
| File | Purpose |
|------|---------|
| `services/auth.js` | signup / signin API calls |
| `context/AuthContext.jsx` | Global auth state (user, token, login/logout) |
| `components/auth/Login.jsx` | Login page |
| `components/auth/Register.jsx` | Register page |

## Rules (Agent ke liye)
- Password hamesha BCrypt encode hona chahiye — `PasswordEncoder` bean use karo
- Token payload mein sensitive data mat daalo — sirf username
- `JWT_SECRET` `.env` se aata hai — kabhi hardcode mat karo
- `/auth/**` aur `/products` public hain — baaki sab protected

## Changelog
- 2026-09-12 Initial docs created
