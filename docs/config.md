# Feature: Configuration & Environment Variables

## Overview
Saari sensitive config `.env` file mein hai. `application.properties` env vars read karta hai.
Kabhi bhi secrets ya URLs Java code mein hardcode mat karo.

## .env File Location
`sales-savvy-be/.env`

## All Environment Variables

| Variable | Required | Default | Description |
|----------|----------|---------|-------------|
| `DB_URL` | Yes | `jdbc:mysql://localhost:3306/ecom` | MySQL connection URL |
| `DB_USERNAME` | Yes | `root` | DB username |
| `DB_PASSWORD` | Yes | (empty) | DB password |
| `RAZORPAY_KEY_ID` | Yes | (empty) | Razorpay public key |
| `RAZORPAY_KEY_SECRET` | Yes | (empty) | Razorpay secret key |
| `JWT_SECRET` | Yes | `change-this-development-secret` | JWT signing key (Base64) |
| `JWT_EXPIRATION` | No | `86400000` | JWT expiry in ms (24h) |
| `ADMIN_PASSWORD` | Yes | `StrongAdmin123` | Default admin password |
| `CORS_ALLOWED_ORIGINS` | No | `http://localhost:5173` | Frontend URL(s) for CORS |

## application.properties Mapping
```properties
spring.datasource.url=${DB_URL:jdbc:mysql://localhost:3306/ecom...}
spring.datasource.username=${DB_USERNAME:root}
spring.datasource.password=${DB_PASSWORD:}
jwt.secret=${JWT_SECRET:change-this-development-secret}
jwt.expiration=${JWT_EXPIRATION:86400000}
razorpay.key.id=${RAZORPAY_KEY_ID:}
razorpay.key.secret=${RAZORPAY_KEY_SECRET:}
cors.allowed-origins=${CORS_ALLOWED_ORIGINS:http://localhost:5173}
```

## Frontend .env
Frontend ke liye `.env` file `sales_savvy_fr/` mein banana hoga:

```
# sales_savvy_fr/.env
VITE_API_URL=http://localhost:8080
```

Frontend mein `api.js` yeh read karta hai:
```js
baseURL: import.meta.env.VITE_API_URL || 'http://localhost:8080'
```

## Server Configuration
```properties
server.port=8080
spring.jpa.hibernate.ddl-auto=update   # Auto schema update
spring.jpa.show-sql=true               # SQL log on (dev mode)
```

## Production Checklist
- [ ] `JWT_SECRET` strong random Base64 string set karo
- [ ] `DB_PASSWORD` strong password set karo
- [ ] `CORS_ALLOWED_ORIGINS` production frontend URL set karo
- [ ] `RAZORPAY_KEY_ID` aur `SECRET` live keys se replace karo
- [ ] `spring.jpa.show-sql=false` kar do production mein
- [ ] `spring.jpa.hibernate.ddl-auto=validate` kar do production mein

## Rules (Agent ke liye)
- Naya config value add karo → `.env` mein variable add karo, `application.properties` mein `${VAR:default}` use karo, is file mein table update karo
- Kabhi bhi `.env` file git commit mat karo (`.gitignore` mein hai)
- Frontend URL hardcode nahi — `VITE_API_URL` env var use karo
- Backend URL hardcode nahi — `CORS_ALLOWED_ORIGINS` env var use karo

## Changelog
- 2026-09-12 CORS_ALLOWED_ORIGINS variable added to .env and application.properties
- 2026-09-12 Initial docs created
