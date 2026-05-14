package com.sit.qb.security;

public final class JwtConstants {

    private JwtConstants() {}

    public static final String SECRET = "Y3VAamtMVnYzbTBPQVlpV1lUakx5b1NFcG8wYXBPM2NJWVRkRTBYbU9FOD0=";

    public static final long EXPIRATION_MS = 3_600_000L;

    public static final String HEADER_NAME = "Authorization";

    public static final String TOKEN_PREFIX = "Bearer ";

    public static final String CLAIM_ROLE = "role";

    public static final String CLAIM_USER_ID = "userId";
}
