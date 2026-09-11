# Feature: Users

## Overview
User management — profile, admin user list, delete.
Roles: `ROLE_USER` (default customer) aur `ROLE_ADMIN`.

## Backend Files
| File | Purpose |
|------|---------|
| `controller/UserController.java` | `/users/**` endpoints |
| `entity/Users.java` | User entity (DB table: `users`) |
| `service/UsersService.java` | Interface |
| `dto/UserResponse.java` | User response DTO (password exclude) |

## Entity Fields
| Field | Type | Validation |
|-------|------|------------|
| id | Long | PK, auto |
| username | String | Unique, 4–20 chars |
| email | String | Unique, valid email format |
| password | String | BCrypt encoded, min 8 chars |
| gender | String | Optional |
| dob | String | Optional, date of birth |
| role | String | ROLE_USER / ROLE_ADMIN |
| cart | Cart | OneToOne |
| orders | List<Orders> | OneToMany |

## API Endpoints

### GET `/users` — ADMIN ONLY
Saare users ka list. Response: `UserResponse[]` (password excluded).

### GET `/users/{username}` — Auth required (own user only)
User by username. 403 if accessing another user's data.

### GET `/users/profile` — Auth required
Apna profile dekho (JWT se username auto-detect).

### PUT `/users/{id}` — ADMIN ONLY
User update karo.

### DELETE `/users/{id}` — ADMIN ONLY
User delete karo.

## Roles
| Role | Access |
|------|--------|
| `ROLE_USER` | Own cart, own orders, own profile |
| `ROLE_ADMIN` | All users, all orders, product CRUD, order status update, dashboard stats |

## Admin Account
Admin account `.env` ke `ADMIN_PASSWORD` se configure hota hai.
Admin signup pe role manually `ROLE_ADMIN` dena padta hai.

## Frontend Files
| File | Purpose |
|------|---------|
| `services/user.js` | getUserProfile, updateUser, getAllUsers, deleteUser |
| `pages/Profile.jsx` | User profile page |
| `pages/AdminUsers.jsx` | Admin user management |
| `context/AuthContext.jsx` | role check ke liye: `user.role === "ROLE_ADMIN"` |

## Rules (Agent ke liye)
- Password response mein kabhi include mat karo — `UserResponse` DTO use karo
- Admin check: `@PreAuthorize("hasRole('ADMIN')")` — role prefix `ROLE_` automatic hai Spring Security mein
- Own data check: `if (!principal.getName().equals(username))` → 403

## Changelog
- 2026-09-12 Initial docs created
