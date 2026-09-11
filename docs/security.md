# Feature: Security & CORS

## Overview
Spring Security + JWT stateless auth. CORS centrally managed via CorsConfig bean.
Teen config files hain — teeno ek hi property se CORS origin read karte hain.

## Backend Files
| File | Purpose |
|------|---------|
| `config/SecurityConfig.java` | Main security filter chain, CORS source |
| `config/CorsConfig.java` | Global CorsFilter bean |
| `config/WebConfig.java` | WebMvcConfigurer CORS mappings |
| `security/JwtAuthenticationFilter.java` | Har request pe JWT validate karo |
| `security/JwtUtil.java` | JWT generate / parse / validate |
| `security/CustomUserDetailsService.java` | Username → UserDetails loader |

## JWT Configuration
| Property | .env Variable | Default |
|----------|--------------|---------|
| `jwt.secret` | `JWT_SECRET` | (required — Base64 encoded) |
| `jwt.expiration` | `JWT_EXPIRATION` | `86400000` (24 hours ms) |

## CORS Configuration
**Single source of truth:** `cors.allowed-origins` property

```properties
# application.properties
cors.allowed-origins=${CORS_ALLOWED_ORIGINS:http://localhost:5173}
```

**Teen jagah read hoti hai yeh property:**
1. `CorsConfig.java` — global `CorsFilter` bean
2. `SecurityConfig.java` — Spring Security ka `CorsConfigurationSource`
3. `WebConfig.java` — `WebMvcConfigurer.addCorsMappings()`

**Production mein set karo:**
```
CORS_ALLOWED_ORIGINS=https://your-domain.com
```
Multiple origins:
```
CORS_ALLOWED_ORIGINS=https://domain1.com,https://domain2.com
```

## Public vs Protected Routes
```java
// SecurityConfig.java — securityFilterChain()
.requestMatchers("/auth/**", "/products").permitAll()
.anyRequest().authenticated()
```

| Endpoint | Auth Required |
|----------|--------------|
| `POST /auth/signup` | No |
| `POST /auth/signin` | No |
| `GET /products` | No |
| Everything else | Yes (JWT Bearer token) |

## Role-Based Access
| Annotation | Who can access |
|-----------|----------------|
| `@PreAuthorize("hasRole('ADMIN')")` | ROLE_ADMIN only |
| No annotation (authenticated) | Any logged-in user |
| Manual `principal.getName()` check | Own data only |

## JwtAuthenticationFilter Flow
```
Request comes in
  → Extract "Authorization: Bearer <token>" header
  → JwtUtil.extractUsername(token)
  → CustomUserDetailsService.loadUserByUsername()
  → JwtUtil.isTokenValid()
  → Set SecurityContextHolder
  → Continue to controller
```

## Rules (Agent ke liye)
- CORS origin kabhi hardcode mat karo — `cors.allowed-origins` property use karo
- Controller pe `@CrossOrigin` annotation mat lagao — global config sab handle karta hai
- JWT secret Base64 encoded hona chahiye, minimum 256-bit strong
- Naya public endpoint add karo → `SecurityConfig.requestMatchers()` mein add karo
- Naya protected endpoint add karo → kuch karna nahi, default protected hai

## Changelog
- 2026-09-12 CORS origin hardcoding removed from SecurityConfig, WebConfig, CorsConfig
- 2026-09-12 @Value injection from cors.allowed-origins property added in all 3 config files
- 2026-09-12 @CrossOrigin removed from all 6 controllers (redundant)
- 2026-09-12 Initial docs created
