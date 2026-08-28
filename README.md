# 📈 Trading Journal & Analytics API

<div align="center">
  <img src="https://img.shields.io/badge/Java%2021-20232a?style=flat-square&logo=openjdk&logoColor=white" />
  <img src="https://img.shields.io/badge/Spring%20Boot-20232a?style=flat-square&logo=spring&logoColor=6DB33F" />
  <img src="https://img.shields.io/badge/Spring%20Security-20232a?style=flat-square&logo=springsecurity&logoColor=6DB33F" />
  <img src="https://img.shields.io/badge/JWT-20232a?style=flat-square&logo=jsonwebtokens&logoColor=white" />
  <img src="https://img.shields.io/badge/PostgreSQL-20232a?style=flat-square&logo=postgresql&logoColor=4169E1" />
  <img src="https://img.shields.io/badge/Oracle%20Cloud-20232a?style=flat-square&logo=oracle&logoColor=F80000" />
</div>

<br/>

A production-grade, multi-tenant RESTful backend designed for traders to securely log portfolios, calculate real-time analytics using Java Streams, process CSV datasets, and manage secure media attachments.

🚀 **Live System Portfolio:** [shubhamkushwahportfolio.site](https://shubhamkushwahportfolio.site)

---

## 🏗️ Core Engineering Highlights

* **Stateless JWT Security Filter Chain:** Implements a custom `OncePerRequestFilter` (`JwtAuthenticationFilter`) that intercepts requests, extracts authorization headers, validates tokens, and populates the `SecurityContextHolder` without relying on server sessions.
* **Strict Tenant Data Isolation:** Security context injection guarantees that all trade retrieval, updates, and deletion endpoints match authenticated ownership, preventing unauthorized cross-user access.
* **Advanced Analytics Engine:** Uses Java Streams (`Collectors.groupingBy`, custom comparators, mapping pipelines) to calculate real-time P&L, win rates, strategy-based performance metrics, and win/loss breakdowns in memory.
* **Bulk Data Processing & Export:** Integrated **Apache Commons CSV** parser to seamlessly ingest bulk CSV records into JPA entities and dynamically stream text/csv export reports.
* **Secure Media Management:** Implements unique UUID filename generation and automatic directory routing via `FileUploadService` to manage trade screenshot attachments securely.
* **Database Agnosticism:** * **Database Agnosticism:** Configured with Hibernate ORM, allowing seamless integration and portability across relational database management systems like MySQL.

---

## 📊 API Documentation & Endpoints

### 1. Authentication Endpoints (`/api/auth`)
*Public access routes.*

| Method | Endpoint | Description | Request Body |
| :--- | :--- | :--- | :--- |
| **POST** | `/api/auth/register` | Register a new user with BCrypt password hashing | `{ username, email, password, fullName }` |
| **POST** | `/api/auth/login` | Authenticate user and issue a JWT token | `{ username, password }` |

### 2. Trade Management Endpoints (`/api/trades`)
*Secured with `Authorization: Bearer <token>`*

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| **GET** | `/api/trades` | Fetch all trades linked to the authenticated user |
| **GET** | `/api/trades/paginated` | Fetch trades with pagination and dynamic sorting (`?page=&size=&sortBy=&direction=`) |
| **POST** | `/api/trades` | Log a new JSON-formatted trade entry |
| **POST** | `/api/trades` | Log a trade with a multipart screenshot file (`consumes = multipart/form-data`) |
| **GET** | `/api/trades/{id}` | Fetch a single trade (includes ownership verification check) |
| **PUT** | `/api/trades/{id}` | Update existing trade specifications |
| **DELETE** | `/api/trades/{id}` | Delete a trade and clean up database relations |

### 3. Analytics & Statistics Endpoints (`/api/trades/stats`)
*Secured with `Authorization: Bearer <token>`*

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| **GET** | `/api/trades/stats/total-pnl` | Calculate cumulative net Profit & Loss |
| **GET** | `/api/trades/stats/winrate` | Compute win percentage formatting |
| **GET** | `/api/trades/stats/best-trade` | Return the highest P&L trade entity |
| **GET** | `/api/trades/stats/worst-trade` | Return the maximum loss trade entity |
| **GET** | `/api/trades/stats/avg-pnl` | Compute average P&L per trade |
| **GET** | `/api/trades/stats/by-strategy` | Aggregate trade performance grouped by strategy |
| **GET** | `/api/trades/stats/by-symbol` | Calculate trade frequency count grouped by ticker symbol |
| **GET** | `/api/trades/stats/win-loss` | Detailed summary containing total wins, losses, avg win, and avg loss |

### 4. CSV Bulk Operations (`/api/trades`)
*Secured with `Authorization: Bearer <token>`*

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| **POST** | `/api/trades/import/csv` | Bulk import trades using an uploaded CSV multi-part file |
| **GET** | `/api/trades/export/csv` | Export all user trades dynamically as a downloadable `trades.csv` attachment |

---

## 🚀 Quick Start (Local Deployment)

### Prerequisites
- JDK 21 installed
- MySQL or PostgreSQL database instance running locally

### Setup & Execution
1. Clone the repository:
   ```bash
   git clone [https://github.com/shubhamKushwah-5/TradingJournalProject.git](https://github.com/shubhamKushwah-5/TradingJournalProject.git)
   cd TradingJournalProject
   ```
2. Configure database properties and file upload directory paths inside `src/main/resources/application.properties`.
3. Build and launch the application:
   ```bash
   mvn spring-boot:run
   ```

---

## 📸 Interface Previews

### 1. Secure Registration
![Register User](./journal-api/Screenshots/Register%20User.png)

### 2. JWT Generation (Login)
![Login User](./journal-api/Screenshots/Login%20User.png)

### 3. Protected Trade Logging
![Create Trade](./journal-api/Screenshots/Create%20Trade.png)

---

## 👨‍💻 Author
**Shubham Kushwah**
* **Portfolio:** [shubhamkushwahportfolio.site](https://shubhamkushwahportfolio.site)
* **LinkedIn:** [Shubham Kushwah](https://www.linkedin.com/in/shubham-kushwah-5657912b8/)
* **LeetCode:** [Shubhamkushwah0777 (Rating: 1650)](https://leetcode.com/u/Shubhamkushwah0777/)
