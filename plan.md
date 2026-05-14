# Plan: JWT Authentication Implementation

## Goal
Replace HTTP Basic auth with stateless JWT bearer-token auth. After successful `/auth/login`, the response must contain a JWT (HS256, **1-hour expiry**) that the client sends on every subsequent request as `Authorization: Bearer <token>`.

## Current state (verified)
- `/auth/login` returns `AuthResponseDto` (profileId, userId, name, email, role, phone, address) — **no token**.
- `SecurityConfig` uses `httpBasic(Customizer.withDefaults())` + STATELESS sessions.
- `UserDetailsServiceImpl` already loads `User` (which implements `UserDetails`) by email.
- RBAC enforced via `@PreAuthorize` on every endpoint — keeps working unchanged because the JWT filter will populate `SecurityContextHolder` with `ROLE_<role>` authorities, which is what `@PreAuthorize` reads.

## Files to add

### 1. `pom.xml` — add jjwt 0.12.x
```xml
<dependency><groupId>io.jsonwebtoken</groupId><artifactId>jjwt-api</artifactId><version>0.12.6</version></dependency>
<dependency><groupId>io.jsonwebtoken</groupId><artifactId>jjwt-impl</artifactId><version>0.12.6</version><scope>runtime</scope></dependency>
<dependency><groupId>io.jsonwebtoken</groupId><artifactId>jjwt-jackson</artifactId><version>0.12.6</version><scope>runtime</scope></dependency>
```

### 2. `security/JwtConstants.java` — **separate file for JWT constants (per requirement)**
- `SECRET` — Base64-encoded 256-bit HMAC secret (default in-file; overridable via property).
- `EXPIRATION_MS = 3_600_000L` (1 hour).
- `TOKEN_PREFIX = "Bearer "`.
- `HEADER_NAME = "Authorization"`.
- `CLAIM_ROLE = "role"`, `CLAIM_USER_ID = "userId"`.

### 3. `security/JwtUtil.java` — token generator/parser (`@Component`)
- `generateToken(User user)` → JWT with `sub=email`, claims `role` + `userId`, `iat` + `exp = now + 1h`.
- `extractEmail(token)`, `extractRole(token)`, `extractUserId(token)`, `isValid(token)`.
- Reads secret + expiry from `@Value("${quickbite.jwt.secret:<default>}")` falling back to `JwtConstants`.

### 4. `security/JwtAuthFilter.java` — `OncePerRequestFilter` (`@Component`)
- Reads `Authorization: Bearer ...`. If absent → continue chain (Spring handles 401 via entry point).
- If present + valid → load `UserDetails` via `UserDetailsServiceImpl`, build `UsernamePasswordAuthenticationToken`, set on `SecurityContextHolder`.
- If present + invalid/expired → leave context empty so Spring returns 401.
- Skips `/auth/**` paths via `shouldNotFilter`.

## Files to modify

### 5. `dtos/AuthResponseDto.java` — **add `token` field**
Login response is the only DTO that needs the token. Other response DTOs (`RegistrationResponseDto`, `ProfileResponseDto`, etc.) intentionally **do not** carry tokens because:
- Registration is two-step (user → profile); user has no profile yet at register time.
- Profile creation requires an already-authenticated request, so the client already holds a token.

Add `token` as the first field in the constructor + getter. Update `AuthServiceImpl.buildLoginResponse()` to pass the token into every branch (CUSTOMER, DELIVERY_AGENT, RESTAURANT, ADMIN).

### 6. `service/AuthServiceImpl.java`
- `@Autowired JwtUtil jwtUtil;`
- In `login()`: after password check, `String token = jwtUtil.generateToken(user);` and thread it into `buildLoginResponse(user, token)`.

### 7. `security/SecurityConfig.java`
- Remove `httpBasic(Customizer.withDefaults())`.
- `@Autowired JwtAuthFilter jwtAuthFilter;`
- `.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)`
- Add an `AuthenticationEntryPoint` bean that writes a `StanderedErrorResponse` JSON 401 (so unauthenticated calls return a consistent error shape).

### 8. `application.properties` — optional overrides (no behavior change if absent):
```
quickbite.jwt.secret=<base64-256bit>
quickbite.jwt.expiration-ms=3600000
```
`JwtConstants` provides safe defaults if these are missing.

## Acceptance criteria
1. `POST /auth/login` 200 response body contains `data.token` (JWT string) **plus** all existing fields.
2. Hitting any non-`/auth/**` endpoint without `Authorization: Bearer <token>` → 401.
3. Hitting with a valid token → endpoint executes; `@PreAuthorize("hasRole('CUSTOMER')")` etc. still works.
4. After 1 hour, the same token → 401 (expired).
5. Existing HTTP Basic auth header on requests no longer works — clients must switch to Bearer.

## Out of scope
- Refresh tokens / token revocation / blacklist.
- Postman collection update (will flag in summary — you can request as a follow-up).
- Renaming `AuthResponseDto` → `LoginResponseDto`.
- Returning a token from `/auth/register/*` (registration is two-step; profile doesn't exist yet — login is the canonical mint point).

## Confirm before I implement
1. **Breaking change**: every existing Postman/API client using Basic auth will break and must switch to Bearer. OK?
2. **Secret storage**: default Base64 secret hard-coded in `JwtConstants.java`, overridable via `application.properties`. Acceptable, or do you want the default to be a placeholder that forces a property override?
3. **Token on register endpoints**: confirm we do **not** return a token from `/auth/register/*` (only from `/auth/login`).
