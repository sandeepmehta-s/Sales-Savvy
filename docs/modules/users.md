# Users Module — ShopSphere

## Overview
User profile management. Admins can list, update, and delete users.

## Package
`com.shopSphere.user`

## MongoDB Document

```json
{ "_id": "String", "username": "String", "email": "String",
  "password": "bcrypt", "gender": "String", "dob": "String",
  "role": "ROLE_CUSTOMER | ROLE_ADMIN", "cartId": "String", "orderIds": ["String"] }
```

## Endpoints

| Method | Path | Auth | Role | Description |
|--------|------|------|------|-------------|
| GET | `/users` | JWT | ADMIN | List all users |
| GET | `/users/{username}` | JWT | SELF | Get user profile |
| GET | `/users/profile` | JWT | ANY | Get own profile |
| PUT | `/users/{id}` | JWT | ADMIN | Update user |
| DELETE | `/users/{id}` | JWT | ADMIN | Delete user (fails if has orders) |

## Key Files

- `user/controller/UserController.java`
- `user/entity/Users.java` — MongoDB `@Document(collection="users")`
- `user/repository/UsersRepository.java` — `MongoRepository<Users, String>`
- `user/service/UsersServiceImplementation.java`

## Business Rules

- Cannot delete a user who has existing orders (`orderIds` not empty)
- Role can only be changed by an existing ADMIN
- Password is always BCrypt-encoded before saving
- On registration, a `Cart` document is automatically created for the user