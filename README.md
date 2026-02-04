# backend-assessment
Develop a Spring Boot application to manage products, users, and orders, including dynamic discount computation based on user type and order amount.

---

## Requirements Covered
### Product Management
- CRUD products: `id, name, description, price, quantity, timestamps`
- Soft delete (logical delete)
- Search/filter by `name`, `price range`, `availability`
- Pagination and sorting

### User Management
- Roles: `USER`, `PREMIUM_USER`, `ADMIN`
- JWT auth (login)
- RBAC:
    - `ADMIN`: create/update/delete products + manage user role
    - `USER/PREMIUM_USER`: view/search products + place orders

### Order Management
- Place order for multiple products
- Validate stock + decrement inventory within transaction
- Order totals: `subtotal`, `discountTotal`, `total`
- Item totals: `unitPrice`, `discountApplied`, `totalPrice`
- Dynamic discounts (strategy-based rules)

### Bonus
- Docker Compose (Postgres + Redis)
- Redis caching
- Global exception handler + unified error response
- OpenAPI/Swagger docs
- Actuator endpoints

---

## Tech Stack
- Java 17
- Spring Boot 3.x
- Spring Web, Spring Security (JWT), Spring Data JPA + Specifications, Validation
- PostgreSQL 16 + Flyway
- Redis 7 + Spring Cache
- OpenAPI/Swagger
- JUnit 5 + Mockito

---

## Project Structure
### Layering summary
- **api/**: HTTP layer (controllers + DTOs + error model)
- **application/**: business use-cases (services, validation, transactions)
- **domain/**: core domain (entities, rules, repositories interfaces)
- **infrastructure/**: external concerns (cache, security, OpenAPI config)

---

## Setup & Run

### Environment Variables
Create a `.env` file in the project root:

## API Documentation

### Swagger / OpenAPI
- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

### Actuator
- `GET http://localhost:8080/actuator/health`
- `GET http://localhost:8080/actuator/info`
- `GET http://localhost:8080/actuator/metrics`
- `GET http://localhost:8080/actuator/prometheus`

---

## Postman Collection

- Collection: `postman/backend-assessment.Assessment-APIs-collection.postman_collection.json`

## Design Decisions

### 1) Layering & separation of concerns by applying following archicture
- **Controllers** handle HTTP concerns (request/response) and input validation.
- **Services** implement business logic and transaction boundaries.
- **Domain** contains the core model: entities, repositories, and discount logic.

### 2) Discount design pattern (Strategy)
- Each discount is implemented as a `DiscountRule`.
- `DiscountCalculator` receives `List<DiscountRule>` and applies only the rules that match the current `DiscountContext`.
- This approach makes it easy to add/remove discount rules without changing the calculator implementation.

### 4) Paging + caching
- Product search endpoint supports pagination and sorting using Spring `Pageable`.
- Cache is evicted on product create/update/delete to avoid stale results.


## Env Setup

```env
POSTGRES_USER=assessment
POSTGRES_PASSWORD=123
POSTGRES_DB=assessment_db
POSTGRES_PORT=5332

Start Infrastructure (Postgres + Redis)
docker compose up -d
docker compose ps

Run Application
Using Gradle:
./gradlew clean bootRun

Database Migrations (Flyway)

Migrations are located at:
src/main/resources/db/migration

Allowed Users
==============
username: ADMIN
role:admin
password:123

username: premium
role:PREMIUM_USER
password:123

username: user
role:USER
password:123