# Trading Journal API

> **Live Production API**: https://trading-journal-project.onrender.com
JWT-authenticated REST API for tracking trading journal with statistics.

## 🏗️ Architecture & Tech Stack
* **Backend:** Java 21, Spring Boot 3, Spring Data JPA, Hibernate
* **Security:** Spring Security, JWT (Stateless Authentication)
* **Database:** PostgreSQL (Production) / MySQL (Local)
* **Infrastructure:** Docker Containerization, CI/CD via Render

# Trading Journal API Documentation

## 🔌 API Environments
* **Production Base URL:** `https://trading-journal-api-xxxx.onrender.com
* **Local Base URL:** `http://localhost:8080

## Authentication
All endpoints (except /api/auth/*) require JWT token in header:
```
Authorization: Bearer <token>
```

## Endpoints

### Authentication

#### Register User
```
POST /api/auth/register
Body: {
  "username": "string",
  "email": "string",
  "password": "string",
  "fullName": "string"
}
Response: { "message": "User registered successfully", "username": "..." }
```
#### Login
```
POST /api/auth/login
Body: {
  "username": "string",
  "password": "string"
}
Response: { 
  "message": "Login successful", 
  "username": "...",
  "token": "eyJhbGc..." 
}
```

### Trades

#### Get All User's Trades
```
GET /api/trades
Response: [{ trade objects }]
```

#### Get Single Trade
```
GET /api/trades/{id}
Response: { trade object }
```

#### Add Trade
```
POST /api/trades
Body: {
  "symbol": "RELIANCE",
  "type": "BUY" or "SELL",
  "entryPrice": 2500,
  "exitPrice": 2550,
  "quantity": 10,
  "strategy": "Intraday/Swing/Scalping"
}
Response: { created trade }
```

#### Update Trade
```
PUT /api/trades/{id}
Body: { updated trade fields }
Response: { updated trade }
```

#### Delete Trade
```
DELETE /api/trades/{id}
Response: "Trade deleted with id: X"
```

### Statistics
```
GET /api/trades/stats/total-pnl - Total profit/loss
GET /api/trades/stats/winrate - Win percentage
GET /api/trades/stats/best-trade - Highest profit trade
GET /api/trades/stats/worst-trade - Biggest loss trade
GET /api/trades/stats/avg-pnl - Average P&L per trade
GET /api/trades/stats/by-strategy - Stats grouped by strategy
GET /api/trades/stats/by-symbol - Trade count per symbol
GET /api/trades/stats/win-loss - Detailed win/loss breakdown
```

### Filtering
```
GET /api/trades/strategy/{strategy} - Filter by strategy
GET /api/trades/date/{date} - Trades on specific date (YYYY-MM-DD)
GET /api/trades/date-range?start=X&end=Y - Date range
GET /api/trades/today - Today's trades
GET /api/trades/this-week - Last 7 days
GET /api/trades/this-month - Last 30 days
```

## Example Usage

1. Register user
2. Login to get token
3. Add token to all requests: `Authorization: Bearer <token>`
4. Add trades
5. View statistics

## Security

- JWT tokens expire after 24 hours
- Each user can only access their own trades
- Passwords are hashed with BCrypt
- Stateless authentication (no sessions)
- **Strict User Isolation:**
    Users can only access, modify, or delete their own trades via security context extraction.
- **Database Agnosticism:**
    Developed with MySQL and deployed to PostgreSQL with zero business-logic changes, proving 
    a strictly decoupled ORM layer.

## 📸 Production Preview


### 1. Register User
![Register User](./Screenshots/Register%20User.png)

### 2. Login User - Secure Authentication (JWT)
![Login User](./Screenshots/Login%20User.png)

### 3. Logging a Trade (Protected Route)
![Cretae Trade](./Screenshots/Create%20Trade.png)