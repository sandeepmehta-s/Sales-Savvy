# AGENT.md — Sales-Savvy Frontend

## 🤖 Agent Instructions

> Koi bhi change karne se pehle root `docs/` folder ki relevant file zaroor padho.
> Change complete hone ke baad corresponding doc file update karo.

## Docs Location
Root project docs: `../docs/`

| Kya change karna hai | Konsi doc |
|----------------------|-----------|
| Pages, routes | `../docs/frontend.md` |
| API service calls | `../docs/frontend.md` |
| Auth, login, register | `../docs/auth.md` |
| Cart UI | `../docs/cart.md` |
| Orders UI | `../docs/orders.md` |
| Payment flow (Razorpay) | `../docs/payment.md` |
| Product listing / detail | `../docs/products.md` |
| Admin pages | `../docs/users.md` |
| Backend URL config | `../docs/config.md` |

## Key Rules
1. Backend URL kabhi hardcode mat karo — `VITE_API_URL` env var use karo
2. JWT token `localStorage` mein `"token"` key pe hai
3. Admin check ke liye `AuthContext` ka `isAdmin()` use karo
4. Naya route add karo → `App.jsx` mein add karo
5. Naya API call add karo → `services/` mein existing service file update karo
6. `api.js` mein interceptor already JWT aur 401 handle karta hai — dobara mat likhna
