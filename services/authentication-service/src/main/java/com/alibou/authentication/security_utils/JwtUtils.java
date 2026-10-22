package com.alibou.authentication.security_utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtils {

    private static final String JWT_COOKIE_NAME = "jwtToken";

    private final String jwtSecret;
    private final long jwtExpirationMs;
    private final String jwtIssuer;
    private final boolean jwtCookieSecure;

    public JwtUtils(
            @Value("${jwt.secret}") String jwtSecret,
            @Value("${jwt.expiration}") long jwtExpirationMs,
            @Value("${jwt.issuer}") String jwtIssuer,
            @Value("${jwt.cookie.secure:true}") boolean jwtCookieSecure
    ) {
        this.jwtSecret = jwtSecret;
        this.jwtExpirationMs = jwtExpirationMs;
        this.jwtIssuer = jwtIssuer;
        this.jwtCookieSecure = jwtCookieSecure;
    }

    // =========================================================
    // SIGNING KEY
    // =========================================================

    private SecretKey getSigningKey() {

        return Keys.hmacShaKeyFor(
                jwtSecret.getBytes(StandardCharsets.UTF_8)
        );
    }

    // =========================================================
    // GET JWT FROM REQUEST
    // =========================================================

    public String getJwtFromRequest(HttpServletRequest request) {

        String headerAuth = request.getHeader("Authorization");

        if (headerAuth != null && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }

        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {

                if (JWT_COOKIE_NAME.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }

        return null;
    }

    // =========================================================
    // GENERATE JWT COOKIE
    // =========================================================

    public ResponseCookie generateJwtCookie(String username) {
        String jwt = generateToken(username);

        return ResponseCookie
                .from(JWT_COOKIE_NAME, jwt)
                .path("/")
                .maxAge(jwtExpirationMs / 1000)
                .httpOnly(true)
                .secure(jwtCookieSecure)
                .sameSite("Lax")
                .build();
    }

    // =========================================================
    // GENERATE JWT
    // =========================================================

    public String generateToken(String username) {

        return Jwts.builder()
                .subject(username)
                .issuer(jwtIssuer)
                .issuedAt(new Date())
                .expiration(
                        new Date(
                                System.currentTimeMillis()
                                        + jwtExpirationMs
                        )
                )
                .signWith(getSigningKey())
                .compact();
    }

    // =========================================================
    // GENERATE JWT FROM USERDETAILS
    // =========================================================

    public String generateToken(UserDetails userDetails) {

        return Jwts.builder()
                .subject(userDetails.getUsername())
                .issuer(jwtIssuer)
                .claim("role", userDetails.getAuthorities()
                        .stream()
                        .findFirst()
                        .map(Object::toString)
                        .orElse("USER"))
                .issuedAt(new Date())
                .expiration(
                        new Date(
                                System.currentTimeMillis()
                                        + jwtExpirationMs
                        )
                )
                .signWith(getSigningKey())
                .compact();
    }

    // =========================================================
    // DELETE JWT COOKIE
    // =========================================================
    public ResponseCookie getCleanJwtCookie() {
        return ResponseCookie.from(JWT_COOKIE_NAME, "")
                .path("/")
                .maxAge(0)
                .httpOnly(true)
                .secure(jwtCookieSecure)
                .sameSite("Lax")
                .build();
    }

    // =========================================================
    // VALIDATE JWT
    // =========================================================

    public boolean validateJwtToken(String authToken) {

        try {

            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(authToken);

            return true;

        } catch (Exception e) {

            System.err.println(
                    "JWT Validation Error: " + e.getMessage()
            );

            return false;
        }
    }

    // =========================================================
    // EXTRACT USERNAME
    // =========================================================

    public String getUserNameFromJwtToken(String token) {

        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.getSubject();
    }
}