# Auth Module — ShopSphere

## Overview
Handles user registration, login, and JWT token management.

## Package
`com.shopSphere.auth`

## Endpoints

| Method | Path | Auth | Description |
|--------|------|------|-------------|
| POST | `/auth/register` | No | Register a new user |
| POST | `/auth/login` | No | Login, returns JWT |
| GET | `/auth/test` | No | Health check |

## Flow

### Register
```
POST /auth/register
Body: { username, email, password, gender, dob, role }

1. Validate fields (Jakarta Validation)
2. UsersService.signUp() — checks duplicate username/email
3. BCrypt-encode password
4. Force role = ROLE_CUSTOMER (ignores body role)
5. Save user → create Cart document for user
6. Return 200 OK
```

### Login
```
POST /auth/login
Body: { username, password }

1. CustomUserDetailsService loads user from MongoDB
2. BCrypt.matches(rawPassword, storedHash)
3. JwtUtil.generateToken(username, role)
4. Return { token, username, role, expiresIn }
```

## JWT

- Algorithm: HMAC-SHA256 (HS256)
- Secret: `${JWT_SECRET}` (min 32 chars, base64 recommended)
- Expiry: `${JWT_EXPIRATION}` ms (default 24h = 86400000)
- Header: `Authorization: Bearer <token>`

## Key Files

- `auth/controller/AuthController.java`
- `auth/security/JwtUtil.java` — token generation + validation
- `auth/security/JwtAuthenticationFilter.java` — OncePerRequestFilter
- `auth/security/CustomUserDetailsService.java` — UserDetailsService impl
- `auth/entity/UserLoginData.java` — login request DTO
- `auth/dto/AuthResponse.java` — login response DTO