
package com.alibou.authentication.security_utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseCookie;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Collections;
import java.util.Date;
import static org.junit.jupiter.api.Assertions.*;

class JwtUtilsTest {

    private static final String SECRET =
            "KLCCwb8qqvO+nveS8CjeR3ASkOIRZnpSG8G/4EaxLBTsYBgnecaiQ962Ym/FnROf\n" +
                    "UBVkXiaNclp6FYLjQ0XjBw==";
    private static final String ISSUER = "authentication-service-test";
    private static final long EXPIRATION_MS = 3_600_000L;

    private JwtUtils jwtUtils;

    @BeforeEach
    void setUp() {
        jwtUtils = new JwtUtils(
                SECRET,
                EXPIRATION_MS,
                ISSUER,
                true
        );
    }

    @Test
    void generateTokenShouldContainUsernameIssuerAndExpiration() {
        String token = jwtUtils.generateToken("alice");

        Claims claims = parseClaims(token);

        assertEquals("alice", claims.getSubject());
        assertEquals(ISSUER, claims.getIssuer());
        assertNotNull(claims.getIssuedAt());
        assertNotNull(claims.getExpiration());
        assertTrue(claims.getExpiration().after(new Date()));
    }

    @Test
    void generateTokenFromUserDetailsShouldIncludeRole() {
        UserDetails user = User.withUsername("alice")
                .password("unused")
                .authorities("ROLE_ADMIN")
                .build();

        String token = jwtUtils.generateToken(user);
        Claims claims = parseClaims(token);

        assertEquals("alice", claims.getSubject());
        assertEquals("ROLE_ADMIN", claims.get("role", String.class));
        assertEquals(ISSUER, claims.getIssuer());
    }

    @Test
    void generateTokenFromUserDetailsShouldDefaultRoleWhenNoAuthorities() {
        UserDetails user = User.withUsername("alice")
                .password("unused")
                .authorities(Collections.emptyList())
                .build();

        String token = jwtUtils.generateToken(user);

        assertEquals("USER", parseClaims(token).get("role", String.class));
    }

    @Test
    void getUserNameFromJwtTokenShouldReturnSubject() {
        String token = jwtUtils.generateToken("alice");

        assertEquals("alice", jwtUtils.getUserNameFromJwtToken(token));
    }

    @Test
    void validateJwtTokenShouldAcceptValidToken() {
        String token = jwtUtils.generateToken("alice");

        assertTrue(jwtUtils.validateJwtToken(token));
    }

    @Test
    void validateJwtTokenShouldRejectMalformedToken() {
        assertFalse(jwtUtils.validateJwtToken("not-a-valid-jwt"));
    }

    @Test
    void validateJwtTokenShouldRejectTokenSignedWithAnotherKey() {
        SecretKey otherKey = Keys.hmacShaKeyFor(
                "another-test-secret-key-at-least-32-bytes-long"
                        .getBytes(StandardCharsets.UTF_8)
        );

        String token = Jwts.builder()
                .subject("alice")
                .issuer(ISSUER)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_MS))
                .signWith(otherKey)
                .compact();

        assertFalse(jwtUtils.validateJwtToken(token));
    }

    @Test
    void validateJwtTokenShouldRejectExpiredToken() {
        SecretKey key = Keys.hmacShaKeyFor(
                SECRET.getBytes(StandardCharsets.UTF_8)
        );

        long now = System.currentTimeMillis();

        String expiredToken = Jwts.builder()
                .subject("alice")
                .issuer(ISSUER)
                .issuedAt(new Date(now - 120_000))
                .expiration(new Date(now - 60_000))
                .signWith(key)
                .compact();

        assertFalse(jwtUtils.validateJwtToken(expiredToken));
    }

    @Test
    void getJwtFromRequestShouldPreferBearerHeader() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer header-token");
        request.setCookies(new Cookie("jwtToken", "cookie-token"));

        assertEquals("header-token", jwtUtils.getJwtFromRequest(request));
    }

    @Test
    void getJwtFromRequestShouldReturnCookieWhenHeaderIsMissing() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(
                new Cookie("otherCookie", "other-value"),
                new Cookie("jwtToken", "cookie-token")
        );

        assertEquals("cookie-token", jwtUtils.getJwtFromRequest(request));
    }

    @Test
    void getJwtFromRequestShouldReturnNullWhenNoTokenExists() {
        MockHttpServletRequest request = new MockHttpServletRequest();

        assertNull(jwtUtils.getJwtFromRequest(request));
    }

    @Test
    void getJwtFromRequestShouldReturnNullForNonBearerHeaderWithoutCookie() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Basic abc123");

        assertNull(jwtUtils.getJwtFromRequest(request));
    }

    @Test
    void generateJwtCookieShouldHaveExpectedSecuritySettings() {
        ResponseCookie cookie = jwtUtils.generateJwtCookie("alice");

        assertEquals("jwtToken", cookie.getName());
        assertTrue(jwtUtils.validateJwtToken(cookie.getValue()));
        assertEquals("/", cookie.getPath());
        assertEquals(Duration.ofSeconds(3600), cookie.getMaxAge());
        assertTrue(cookie.isHttpOnly());
        assertTrue(cookie.isSecure());
        assertEquals("Lax", cookie.getSameSite());
    }

    @Test
    void cleanJwtCookieShouldExpireCookieAndPreserveSecuritySettings() {
        ResponseCookie cookie = jwtUtils.getCleanJwtCookie();

        assertEquals("jwtToken", cookie.getName());
        assertEquals("", cookie.getValue());
        assertEquals("/", cookie.getPath());
        assertEquals(Duration.ZERO, cookie.getMaxAge());
        assertTrue(cookie.isHttpOnly());
        assertTrue(cookie.isSecure());
        assertEquals("Lax", cookie.getSameSite());
    }

    @Test
    void cookiesShouldRespectConfiguredSecureFlag() {
        JwtUtils insecureCookieUtils = new JwtUtils(
                SECRET,
                EXPIRATION_MS,
                ISSUER,
                false
        );

        assertFalse(insecureCookieUtils.generateJwtCookie("alice").isSecure());
        assertFalse(insecureCookieUtils.getCleanJwtCookie().isSecure());
    }

    private Claims parseClaims(String token) {
        SecretKey key = Keys.hmacShaKeyFor(
                SECRET.getBytes(StandardCharsets.UTF_8)
        );

        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }


    @Test
    void shouldReturnNullWhenCookiesDoNotContainJwtToken() {
        MockHttpServletRequest request = new MockHttpServletRequest();

        request.setCookies(
                new jakarta.servlet.http.Cookie("otherCookie", "some-value"),
                new jakarta.servlet.http.Cookie("sessionId", "session-123")
        );

        assertNull(jwtUtils.getJwtFromRequest(request));
    }

}
