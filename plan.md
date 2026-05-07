# Spring Security + RBAC Restructure Plan (v2 — corrected)

## Overview

Restructure QuickBite-BE to add Spring Security 6 with RBAC, clean architecture, BCrypt password encoding, and proper separation of auth data from profile data.

**Two-step user creation flow (corrected):**
```
Step 1 — POST /auth/register/*   →  creates User row only  (email + password + role)
Step 2 — POST /api/customers     →  creates CustomerProfile linked via userId
          POST /api/restaurants  →  creates RestaurantProfile linked via userId
          POST /api/agents       →  creates DeliveryAgentProfile linked via userId
```
Admin has no profile — Step 1 is sufficient.

---

## ✅ Already Done (previous commits)

- Added `spring-boot-starter-security` to `pom.xml`
- Created `User.java` (implements `UserDetails`, table `app_user`, BCrypt-ready)
- Created `CustomerProfile.java`, `RestaurantProfile.java`, `DeliveryAgentProfile.java`
- Updated `Order.java`, `MenuItem.java` to use new entity types
- Created `UserRepository`, `CustomerProfileRepository`, `RestaurantProfileRepository`, `DeliveryAgentProfileRepository`
- Created `security/SecurityConfig.java` (BCrypt bean, STATELESS, CSRF off)
- Created `security/UserDetailsServiceImpl.java`
- Updated `UserRole` enum: `CUSTOMER`, `RESTAURANT`, `DELIVERY_AGENT`, `ADMIN`
- Added `UsernameNotFoundException` handler to `GlobalExceptionHandler`
- Deleted old stub files

---

## ❌ Problem with Current Implementation

`POST /auth/register/customer` currently does **both** in one shot:
```
creates User  +  creates CustomerProfile
```
This violates the clean separation requirement. Auth and profile are coupled.

---

## 🔧 Changes Required

### 1. Auth Registration → User Only

**File:** `AuthServiceImpl.java` — update `registerCustomer`, `registerRestaurant`, `registerAgent`

Each method should **only** create a `User` row and return `{userId, email, role}`.
Profile fields (name, phone, address, restaurantName, city, cuisineType) are **removed** from registration.

**Updated registration DTOs** (email + password only):

| DTO | Fields |
|-----|--------|
| `CustomerRegisterDto` | email, password |
| `RestaurantRegisterDto` | email, password |
| `AgentRegisterDto` | email, password |
| `AdminRegisterDto` | email, password ← already correct |

> `name`, `phone`, `address`, `restaurantName`, `city`, `cuisineType` fields are removed from these DTOs — they belong in profile creation.

---

### 2. New Profile Creation DTOs

| New DTO | Fields | Used by |
|---------|--------|---------|
| `CustomerProfileCreateDto` | userId, name, phone, address | `POST /api/customers` |
| `RestaurantProfileCreateDto` | userId, restaurantName, city, cuisineType | `POST /api/restaurants` |
| `AgentProfileCreateDto` | userId, name, phone | `POST /api/agents` |

All `userId` fields: `@NotNull` — links profile to an existing User.

---

### 3. Profile Creation Response DTO

**New:** `ProfileResponseDto.java`

| Field | Type | Notes |
|-------|------|-------|
| profileId | Long | the created profile's id |
| userId | Long | the linked User's id |
| name | String | profile name |
| phone | String | null for restaurant |
| address | String | null for agent/restaurant |
| role | String | from User |

---

### 4. Updated AuthResponseDto (registration response)

Registration now only creates a User → response is simpler:

| Field | Type | Notes |
|-------|------|-------|
| userId | Long | created User id |
| email | String | |
| role | String | CUSTOMER / RESTAURANT / DELIVERY_AGENT / ADMIN |

Login response remains full (fetches profile at login time):

| Field | Type | Notes |
|-------|------|-------|
| profileId | Long | null for ADMIN |
| userId | Long | |
| name | String | null for ADMIN |
| email | String | |
| role | String | |
| phone | String | |
| address | String | |

> Use two separate response DTOs or add a factory method: `AuthResponseDto.forRegistration(user)` vs `AuthResponseDto.forLogin(user, profile)`.

---

### 5. Service Changes

#### `AuthServiceImpl`
- `registerCustomer(CustomerRegisterDto)` → create User only, return `{userId, email, role}`
- `registerRestaurant(RestaurantRegisterDto)` → create User only
- `registerAgent(AgentRegisterDto)` → create User only
- `registerAdmin(AdminRegisterDto)` → create User only (already correct)
- `login(LoginRequestDto)` → unchanged (fetches profile for full response)

#### `CustomerServiceImpl`
- Add `createProfile(CustomerProfileCreateDto dto)` →
  - Lookup `User` by `dto.getUserId()` — throw `ResourceNotFoundException` if missing
  - Verify user role is `CUSTOMER` — throw `ConflictException` if wrong role
  - Check if profile already exists for this userId — throw `ConflictException` if duplicate
  - Save and return `ProfileResponseDto`

#### `RestaurantServiceImpl`
- Add `createProfile(RestaurantProfileCreateDto dto)` → same pattern

#### `DeliveryAgentServiceImpl`
- Add `createProfile(AgentProfileCreateDto dto)` → same pattern

---

### 6. Controller Changes

#### `AuthController` — registration returns user-only response
No path changes. Just the response body is leaner (no profile fields).

#### `CustomerController`
```
POST /api/customers   →  createProfile(@RequestBody CustomerProfileCreateDto)
```
Returns 201 + `ProfileResponseDto`

#### `RestaurantController`
```
POST /api/restaurants   →  createProfile(@RequestBody RestaurantProfileCreateDto)
```
Returns 201 + `ProfileResponseDto`

#### `DeliveryAgentController`
```
POST /api/agents   →  createProfile(@RequestBody AgentProfileCreateDto)
```
Returns 201 + `ProfileResponseDto`

---

### 7. Updated API Flow (end-to-end example)

```
# Step 1: Create user account
POST /auth/register/customer
{ "email": "ram@gmail.com", "password": "ram123" }
→ 201 { "userId": 1, "email": "ram@gmail.com", "role": "CUSTOMER" }

# Step 2: Create customer profile
POST /api/customers
{ "userId": 1, "name": "Ram", "phone": "9876543210", "address": "Pune" }
→ 201 { "profileId": 1, "userId": 1, "name": "Ram", "phone": "9876543210", "role": "CUSTOMER" }

# Login (returns full data)
POST /auth/login
{ "email": "ram@gmail.com", "password": "ram123" }
→ 200 { "profileId": 1, "userId": 1, "name": "Ram", "email": "ram@gmail.com", "role": "CUSTOMER", ... }
```

---

### 8. Files to Create

| File | Purpose |
|------|---------|
| `dtos/CustomerProfileCreateDto.java` | profile creation request |
| `dtos/RestaurantProfileCreateDto.java` | profile creation request |
| `dtos/AgentProfileCreateDto.java` | profile creation request |
| `dtos/ProfileResponseDto.java` | profile creation response |

### 9. Files to Modify

| File | Change |
|------|--------|
| `dtos/CustomerRegisterDto.java` | remove name, phone, address — keep email + password only |
| `dtos/RestaurantRegisterDto.java` | remove restaurantName, city, cuisineType — keep email + password only |
| `dtos/AgentRegisterDto.java` | remove name, phone — keep email + password only |
| `dtos/AuthResponseDto.java` | split into registration (lean) vs login (full) response |
| `service/AuthServiceImpl.java` | register methods create User only |
| `service/CustomerServiceImpl.java` | add createProfile() |
| `service/RestaurantServiceImpl.java` | add createProfile() |
| `service/DeliveryAgentServiceImpl.java` | add createProfile() |
| `controller/CustomerController.java` | add POST /api/customers |
| `controller/RestaurantController.java` | add POST /api/restaurants |
| `controller/DeliveryAgentController.java` | add POST /api/agents |

---

## 10. What Does NOT Change

- All QB-3 to QB-17 endpoints
- `Order.java`, `MenuItem.java`, `OrderItem.java`
- All JPQL queries
- `SecurityConfig.java`, `UserDetailsServiceImpl.java`
- `UserRole`, `UserRepository`, `*ProfileRepository` classes
- `StanderedSuccessResponse`, `StanderedErrorResponse` (intentional spelling)
- `CustomerSummary` projection, `GlobalExceptionHandler`
