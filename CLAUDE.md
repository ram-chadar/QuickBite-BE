# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

```bash
# Build
./mvnw clean install

# Run (starts on port 8080)
./mvnw spring-boot:run

# Test
./mvnw test

# Run a single test class
./mvnw test -Dtest=CustomerServiceImplTest

# Compile only (fast check for errors)
./mvnw compile

# Package (produces target/*.jar)
./mvnw package
```

## Architecture

Spring Boot 3.5.13 / Java 17 REST API for a food delivery platform. MySQL at `localhost:3306/quickbite` (user: root/root). Hibernate DDL is set to `update` — schema evolves automatically.

### Layer structure

```
controller/ → service/*ServiceImpl → repository/ (JPA) → entity/
```

- **Controllers** — `@RestController` under `/api/*`; every endpoint returns `StanderedSuccessResponse` (note the intentional misspelling — do not rename it)
- **Services** — `*ServiceImpl` classes only (no separate interface); business logic and `@Transactional` live here
- **Repositories** — Spring Data JPA; `OrderRepository` and `MenuItemRepository` extend both `JpaRepository` and `JpaSpecificationExecutor` for Criteria API queries
- **Entities** — plain Java classes with manual getters/setters (no `@Data` on entities); use `@JsonManagedReference` / `@JsonBackReference` / `@JsonIgnore` to break serialization cycles
- **DTOs** — `dtos/` package; `OrderRequestDto` + `Menu_Qty` for place-order; response-only DTOs for complex responses (e.g. `OrderDetailDto`, `DeliveryReportDto`, `TopItemDto`)
- **Projections** — `projections/CustomerSummary.java` is a Spring Data interface projection (only id/name/phone)
- **Enums** — `OrderStatus`: `PLACED → PREPARING → OUT_FOR_DELIVERY → DELIVERED / CANCELLED`; DELIVERED and CANCELLED are terminal states
- **Exception handling** — `GlobalExceptionHandler` (`@RestControllerAdvice`) handles: `MethodArgumentNotValidException` (400), `HandlerMethodValidationException` (400), `MissingServletRequestParameterException` (400), `ResourceNotFoundException` (404), `DuplicateEmailException` (409), `ConflictException` (409), `IllegalStateTransitionException` (400), `DataIntegrityViolationException` (409), `ArithmeticException` (500)

### Domain relationships

```
Customer  1──N  Order  N──1  DeliveryAgent
Customer  M──N  Restaurant   (customer_fav join table)
Restaurant 1──N  MenuItem
Order      1──N  OrderItem  N──1  MenuItem
```

### Key patterns

**Criteria API** — Use `JpaSpecificationExecutor` + `Specification` lambda, not `EntityManager` directly:
```java
Specification<Order> spec = (root, query, cb) -> cb.equal(root.get("status"), status);
orderRepository.findAll(spec);
```

**Hibernate dirty checking** — In `@Transactional` methods, modifying a managed entity (e.g. `order.setStatus(...)`, `agent.setIsAvailable(false)`) automatically issues `UPDATE` SQL without an explicit `save()` call. Used in QB-7 (assign agent) and QB-16 (update status).

**JPQL SELECT NEW** — Used for aggregation DTOs; the DTO must have a matching constructor:
```java
@Query("SELECT NEW com.sit.qb.dtos.TopItemDto(m.name, COUNT(oi)) FROM OrderItem oi JOIN oi.menuItem m GROUP BY m.id ORDER BY COUNT(oi) DESC")
List<TopItemDto> findTop3OrderedItems(Pageable pageable);
```

### All API routes (17 total, QB-1 through QB-17)

| QB | Method | Path | Key behaviour |
|----|--------|------|---------------|
| 1 | POST | `/api/customers` | 409 on duplicate email via `DuplicateEmailException` |
| 2 | POST | `/api/restaurants` | `@NotBlank` on name required |
| 3 | POST | `/api/restaurants/{id}/menu` | 404 if restaurant missing |
| 4 | GET | `/api/restaurants` | Menu items excluded (LAZY fetch) |
| 5 | POST | `/api/agents` | name + phone both required |
| 6 | POST | `/api/orders` | `@Transactional`; 404 on bad customer/item |
| 7 | PUT | `/api/orders/{orderId}/assign-agent/{agentId}` | 409 if agent busy; sets `isAvailable=false` |
| 8 | GET | `/api/orders/{orderId}` | JOIN FETCH on orderItems to avoid N+1 |
| 9 | GET | `/api/menu/search?keyword=` | Case-insensitive LIKE via JPQL |
| 10 | GET | `/api/customers/{customerId}/orders` | Ordered by `orderDate DESC` |
| 11 | GET | `/api/orders?status=` | Criteria API via `Specification`; 400 on invalid enum |
| 12 | GET | `/api/menu?maxPrice=` | Criteria API; only `isAvailable=true` items |
| 13 | GET | `/api/customers/summary` | Interface projection — returns only id/name/phone |
| 14 | GET | `/api/orders/{orderId}/total` | JPQL `SUM(unitPrice * quantity)` |
| 15 | GET | `/api/menu/top3` | GROUP BY + `PageRequest.of(0,3)` |
| 16 | PATCH | `/api/orders/{orderId}/status` | Validates transition; frees agent on DELIVERED |
| 17 | GET | `/api/delivery/report` | 3-table JOIN; only orders with assigned agents |

Also present (not in TDD): `GET /api/customers/{id}`, `GET /api/customers`, `DELETE /api/customers/{id}`, `GET /api/customers/byname/{name}`, `GET /api/customers/{email}/{phone}`, `GET /api/restaurants/{id}`.

### Security & Auth

HTTP Basic Auth (no JWT). `User` entity implements `UserDetails` and is stored in the `app_user` table (not `user`, which is a reserved word in MySQL).

**Roles** (`UserRole` enum): `CUSTOMER`, `RESTAURANT`, `DELIVERY_AGENT`, `ADMIN`

**Two-step registration flow:**
1. `POST /auth/register/<role>` — creates a `User` record with BCrypt-hashed password; returns `userId` + `email` + `role`.
2. `POST /api/customers` / `POST /api/agents` / `POST /api/restaurants` — creates the profile (name, phone, etc.) linked to that `User`; requires the matching role.

`/auth/**` is `permitAll`; everything else requires authentication. RBAC is enforced via `@PreAuthorize` on every controller method (`@EnableMethodSecurity` is on `SecurityConfig`). `AccessDeniedException` → 403, `InvalidCredentialsException` / `UsernameNotFoundException` → 401.

**Domain rename:** old `Customer` / `DeliveryAgent` / `Restaurant` entities are now `CustomerProfile` / `DeliveryAgentProfile` / `RestaurantProfile`, each with a `@OneToOne` FK to `User`.

Exception handling additions (on top of what was there before): `InvalidCredentialsException` (401), `UsernameNotFoundException` (401), `AccessDeniedException` (403).

### Known intentional behaviour

- `CustomerServiceImpl.getCustomerByName()` contains `int res = 10/0` — deliberate test for the `ArithmeticException` handler. Do not remove it.
- `StanderedSuccessResponse` / `StanderedErrorResponse` are intentionally misspelled. Do not rename them.
