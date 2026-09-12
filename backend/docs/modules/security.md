# Security Module — ShopSphere

## Overview
JWT-based authentication, role-based authorization, and CORS configuration.

## Package
`com.shopSphere.shared`

## JWT Filter Chain

```
HTTP Request
  → CorsConfig.corsConfigurationSource()      — CORS headers
  → JwtAuthenticationFilter.doFilterInternal() — extract + validate JWT
  → SecurityConfig.securityFilterChain()       — access control rules
  → Controller
```

## Role System

| Role | Value in DB | Access Level |
|------|-------------|--------------|
| Customer | `ROLE_CUSTOMER` | Own cart, own orders, own profile |
| Admin | `ROLE_ADMIN` | All endpoints including admin-only |

## Public Endpoints (no JWT required)

```
GET  /products/**
GET  /auth/test
POST /auth/login
POST /auth/register
GET  /payment/key
```

## CORS Configuration

All three files read the same property:
```properties
cors.allowed-origins=${CORS_ALLOWED_ORIGINS:http://localhost:5173}
```

| File | Purpose |
|------|---------|
| `shared/config/CorsConfig.java` | CORS filter bean |
| `shared/config/WebConfig.java` | MVC CORS registry |
| `shared/config/SecurityConfig.java` | Security + CORS integration |

> To add a new allowed origin, update only the `CORS_ALLOWED_ORIGINS` env var.
> Never hardcode origins in these files.

## Exception Classes

Located in `shared/exception/`:

| Class | HTTP Status | When thrown |
|-------|-------------|-------------|
| `ResourceNotFoundException` | 404 | Entity not found |
| `DuplicateResourceException` | 409 | Username/email already exists |
| `InsufficientStockException` | 422 | Not enough stock for order |

## Password Encoding

BCryptPasswordEncoder is the default. Configured in `SecurityConfig.java`.
Raw passwords are never stored.

## Key Files

- `shared/config/SecurityConfig.java`
- `shared/config/CorsConfig.java`
- `shared/config/WebConfig.java`
- `auth/security/JwtUtil.java`
- `auth/security/JwtAuthenticationFilter.java`
- `auth/security/CustomUserDetailsService.java`