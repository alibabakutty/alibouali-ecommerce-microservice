
package com.alibou.gateway.config;

import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import static org.junit.jupiter.api.Assertions.*;

class JwtConfigTest {

    private final JwtConfig config = new JwtConfig();

    @Test
    void jwtDecoderShouldCreateDecoder() {
        String secret =
                "test-secret-for-hmac-sha512-must-be-at-least-64-bytes-long-0123456789";

        ReactiveJwtDecoder decoder =
                config.jwtDecoder(secret, "http://localhost:8080");

        assertNotNull(decoder);
        assertInstanceOf(NimbusReactiveJwtDecoder.class, decoder);
    }
}