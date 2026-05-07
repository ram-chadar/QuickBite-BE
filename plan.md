# Spring Security + RBAC Restructure Plan

## Overview

Restructure QuickBite-BE to add Spring Security 6 with RBAC, clean architecture, BCrypt password encoding, and proper separation of auth data from profile data.

---

## 1. Dependency Change

**File:** `pom.xml`

Add:
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

---

## 2. Entity Layer Changes

### 2a. Rename `AppUser` → `User`

**Create:** `src/main/java/com/sit/qb/entity/User.java`  
**Empty out:** `src/main/java/com/sit/qb/entity/AppUser.java` (keep package declaration only)

| Field    | Type      | Notes                          |
|----------|-----------|--------------------------------|
| id       | Long      | PK, auto-generated             |
| email    | String    | unique, not null               |
| password | String    | BCrypt hash, not null          |
| role     | UserRole  | enum, not null                 |
| enabled  | Boolean   | default true                   |

- `@Table(name = "app_user")` — DB table name unchanged
- Implements `UserDetails` (Spring Security)
  - `getUsername()` → returns `email`
  - `getAuthorities()` → returns `[ROLE_{role}]`
  - `isEnabled()`, `isAccountNonExpired()`, `isAccountNonLocked()`, `isCredentialsNonExpired()` → true

---

### 2b. Update `UserRole` enum

**File:** `src/main/java/com/sit/qb/entity/UserRole.java`

```
Before: CUSTOMER, DELIVERY_AGENT, RESTAURANT_OWNER
After:  CUSTOMER, RESTAURANT, DELIVERY_AGENT, ADMIN
```

> **Note:** `RESTAURANT_OWNER` → renamed to `RESTAURANT`

---

### 2c. Rename `Customer` → `CustomerProfile`

**Create:** `src/main/java/com/sit/qb/entity/CustomerProfile.java`  
**Empty out:** `src/main/java/com/sit/qb/entity/Customer.java`

| Field      | Type                  | Notes                            |
|------------|-----------------------|----------------------------------|
| id         | Long                  | PK                               |
| name       | String                | not null, @NotBlank              |
| phone      | String                | max 15                           |
| address    | String                | max 255                          |
| user       | User (OneToOne)       | @JoinColumn(name = "user_id")    |
| orders     | List\<Order\>         | @OneToMany mappedBy="customer"   |
| favourites | Set\<RestaurantProfile\> | @ManyToMany via customer_fav  |

- `@Table(name = "customer_profile")`
- **Removed from profile:** `email`, `password` (auth data now lives in `User`)
- `appUser` field renamed to `user` (type: `User`)

---

### 2d. Rename `Restaurant` → `RestaurantProfile`

**Create:** `src/main/java/com/sit/qb/entity/RestaurantProfile.java`  
**Empty out:** `src/main/java/com/sit/qb/entity/Restaurant.java`

| Field       | Type              | Notes                          |
|-------------|-------------------|--------------------------------|
| id          | Long              | PK                             |
| name        | String            | @NotBlank, max 150             |
| city        | String            | max 100                        |
| cuisineType | String            | max 80                         |
| isOpen      | Boolean           | default true                   |
| user        | User (OneToOne)   | @JoinColumn(name = "user_id")  |
| menuItems   | List\<MenuItem\>  | @OneToMany mappedBy="restaurant" |

- `@Table(name = "restaurant_profile")`
- `appUser` field renamed to `user` (type: `User`)

---

### 2e. Rename `DeliveryAgent` → `DeliveryAgentProfile`

**Create:** `src/main/java/com/sit/qb/entity/DeliveryAgentProfile.java`  
**Empty out:** `src/main/java/com/sit/qb/entity/DeliveryAgent.java`

| Field       | Type            | Notes                          |
|-------------|-----------------|--------------------------------|
| id          | Long            | PK                             |
| name        | String          | @NotBlank, max 100             |
| phone       | String          | @NotBlank, max 15              |
| isAvailable | Boolean         | default true                   |
| user        | User (OneToOne) | @JoinColumn(name = "user_id")  |
| orders      | List\<Order\>   | @OneToMany mappedBy="deliveryAgent" |

- `@Table(name = "delivery_agent_profile")`
- `appUser` field renamed to `user` (type: `User`)

---

### 2f. Update `Order.java` (field types only)

**File:** `src/main/java/com/sit/qb/entity/Order.java`

| Change | Before | After |
|--------|--------|-------|
| customer field type | `Customer` | `CustomerProfile` |
| deliveryAgent field type | `DeliveryAgent` | `DeliveryAgentProfile` |

> Field **names** (`customer`, `deliveryAgent`) stay the same — JPQL queries are unaffected.

---

### 2g. Update `MenuItem.java` (field type only)

**File:** `src/main/java/com/sit/qb/entity/MenuItem.java`

| Change | Before | After |
|--------|--------|-------|
| restaurant field type | `Restaurant` | `RestaurantProfile` |

> Field **name** (`restaurant`) stays the same — `@JsonBackReference` and JPQL unaffected.

---

## 3. Repository Layer Changes

### New repositories (create):

| New File | Replaces | Entity |
|----------|----------|--------|
| `UserRepository.java` | `AppUserRepository.java` | `User` |
| `CustomerProfileRepository.java` | `CustomerRepository.java` | `CustomerProfile` |
| `RestaurantProfileRepository.java` | `RestaurantRepository.java` | `RestaurantProfile` |
| `DeliveryAgentProfileRepository.java` | `DeliveryAgentRepository.java` | `DeliveryAgentProfile` |

### Old repositories (empty out):
`AppUserRepository.java`, `CustomerRepository.java`, `RestaurantRepository.java`, `DeliveryAgentRepository.java`

### `CustomerProfileRepository` methods:
```java
Optional<CustomerProfile> findByName(String name);
Optional<CustomerProfile> findByUser_EmailAndPhone(String email, String phone);  // was findByEmailAndPhone
Optional<CustomerProfile> findByUser_Id(Long userId);                             // was findByAppUser_Id
List<CustomerSummary> findAllProjectedBy();                                        // projection — unchanged
```

### `RestaurantProfileRepository` methods:
```java
Optional<RestaurantProfile> findByUser_Id(Long userId);  // was findByAppUser_Id
```

### `DeliveryAgentProfileRepository` methods:
```java
Optional<DeliveryAgentProfile> findByUser_Id(Long userId);  // was findByAppUser_Id
```

---

## 4. Security Layer (New Package: `security/`)

### 4a. Create `SecurityConfig.java`

**File:** `src/main/java/com/sit/qb/security/SecurityConfig.java`

```
- @Configuration + @EnableWebSecurity
- BCryptPasswordEncoder @Bean
- AuthenticationManager @Bean
- SecurityFilterChain:
    - CSRF disabled (REST API)
    - Session: STATELESS (ready for JWT in V2)
    - /auth/**  → permitAll
    - anyRequest → permitAll  ← V1 placeholder; V2 will enforce auth + roles
    - formLogin disabled
    - httpBasic disabled
```

> **V2 note:** When JWT is added, `anyRequest().permitAll()` becomes `anyRequest().authenticated()` and role-based rules are added per endpoint.

---

### 4b. Create `UserDetailsServiceImpl.java`

**File:** `src/main/java/com/sit/qb/security/UserDetailsServiceImpl.java`

```
- Implements UserDetailsService
- loadUserByUsername(email) → UserRepository.findByEmail(email)
- Used by Spring Security's AuthenticationManager
```

---

## 5. DTO Changes

### 5a. Update `AuthResponseDto.java`

| Field | Before | After |
|-------|--------|-------|
| `id` | profile id | `profileId` |
| `appUserId` | User id | `userId` |

### 5b. Create `AdminRegisterDto.java`

Fields: `email` (@NotBlank, @Email), `password` (@NotBlank, @Size(min=6))

---

## 6. Service Layer Changes

### 6a. Rewrite `AuthServiceImpl.java`

- Inject `PasswordEncoder` — use `passwordEncoder.encode()` on register, `passwordEncoder.matches()` on login
- Replace `AppUserRepository` → `UserRepository`
- Replace `CustomerRepository` → `CustomerProfileRepository`
- Replace `RestaurantRepository` → `RestaurantProfileRepository`
- Replace `DeliveryAgentRepository` → `DeliveryAgentProfileRepository`
- Replace all `AppUser` → `User`, `Customer` → `CustomerProfile`, etc.
- Replace `UserRole.RESTAURANT_OWNER` → `UserRole.RESTAURANT`
- Add `registerAdmin(AdminRegisterDto)` method — creates only a `User` row (no profile)
- In `buildLoginResponse()` — add `ADMIN` case, update `RESTAURANT_OWNER` → `RESTAURANT`

### 6b. Update `CustomerServiceImpl.java`

- Replace `CustomerRepository` → `CustomerProfileRepository`
- Replace `Customer` → `CustomerProfile` (all method signatures and return types)
- `getCustomerByEmail_Phone()` — use `findByUser_EmailAndPhone(email, phone)`
- **Keep the intentional `int res = 10/0` in `getCustomerByName()`**
- Remove `register()` method (registration now exclusively through AuthService)

### 6c. Update `RestaurantServiceImpl.java`

- Replace `RestaurantRepository` → `RestaurantProfileRepository`
- Replace `Restaurant` → `RestaurantProfile` (all method signatures)
- Remove `register()` method

### 6d. Update `DeliveryAgentServiceImpl.java`

- Replace `DeliveryAgentRepository` → `DeliveryAgentProfileRepository`
- Replace `DeliveryAgent` → `DeliveryAgentProfile`

### 6e. Update `OrderServiceImpl.java`

- Replace `CustomerRepository` → `CustomerProfileRepository`
- Replace `DeliveryAgentRepository` → `DeliveryAgentProfileRepository`
- Replace `Customer` → `CustomerProfile`, `DeliveryAgent` → `DeliveryAgentProfile`
- All field access (`agent.getIsAvailable()`, `agent.getName()`, etc.) — unchanged

---

## 7. Controller Layer Changes

### 7a. Rewrite `AuthController.java`

| Before | After |
|--------|-------|
| `@RequestMapping("/api/auth")` | `@RequestMapping("/auth")` |
| `POST /api/auth/register/customer` | `POST /auth/register/customer` |
| `POST /api/auth/register/agent` | `POST /auth/register/delivery-agent` |
| `POST /api/auth/register/restaurant` | `POST /auth/register/restaurant` |
| _(missing)_ | `POST /auth/register/admin` ← new |
| `POST /api/auth/login` | `POST /auth/login` |

### 7b. Update `CustomerController.java`

- Replace `Customer` → `CustomerProfile` (variable types only)

### 7c. Update `RestaurantController.java`

- Replace `Restaurant` → `RestaurantProfile` (variable types only)

---

## 8. Exception Handler Update

**File:** `src/main/java/com/sit/qb/exceptions/GlobalExceptionHandler.java`

Add handler for `org.springframework.security.core.userdetails.UsernameNotFoundException` (400 or 401).

---

## 9. What Does NOT Change

- All QB-1 to QB-17 endpoint paths and behaviour
- `Order.java` field names (`customer`, `deliveryAgent`) — JPQL queries unaffected
- `MenuItem.java` field name (`restaurant`) — JPQL unaffected
- All JPQL queries in `OrderRepository`, `MenuItemRepository`
- `OrderItem.java`, `OrderStatus.java`, `OrderController.java`
- `MenuController.java`, `MenuItemServiceImpl.java`
- `DeliveryController.java`, `DeliveryServiceImpl.java`
- `StanderedSuccessResponse`, `StanderedErrorResponse` (intentional spelling preserved)
- `CustomerSummary` projection interface
- `GlobalExceptionHandler` existing handlers
- All other existing DTOs (`OrderRequestDto`, `Menu_Qty`, `OrderDetailDto`, etc.)

---

## 10. DB Impact

Since `spring.jpa.hibernate.ddl-auto=update`, Hibernate will:
- Create new tables: `customer_profile`, `restaurant_profile`, `delivery_agent_profile`
- Add `enabled` column to `app_user`
- Old tables (`customer`, `restaurant`, `delivery_agent`) will remain but be unused

> **Recommendation:** Drop and recreate the `quickbite` DB before first run after this change, since old tables will have stale data and new profile tables will be empty.

---

## 11. Final Package Structure (additions)

```
com.sit.qb
├── entity/
│   ├── User.java                    ← new (was AppUser)
│   ├── CustomerProfile.java         ← new (was Customer)
│   ├── RestaurantProfile.java       ← new (was Restaurant)
│   ├── DeliveryAgentProfile.java    ← new (was DeliveryAgent)
│   ├── AppUser.java                 ← emptied
│   ├── Customer.java                ← emptied
│   ├── Restaurant.java              ← emptied
│   └── DeliveryAgent.java           ← emptied
├── repository/
│   ├── UserRepository.java                  ← new
│   ├── CustomerProfileRepository.java       ← new
│   ├── RestaurantProfileRepository.java     ← new
│   ├── DeliveryAgentProfileRepository.java  ← new
│   ├── AppUserRepository.java               ← emptied
│   ├── CustomerRepository.java              ← emptied
│   ├── RestaurantRepository.java            ← emptied
│   └── DeliveryAgentRepository.java         ← emptied
├── security/                        ← new package
│   ├── SecurityConfig.java
│   └── UserDetailsServiceImpl.java
└── dtos/
    └── AdminRegisterDto.java        ← new
```
