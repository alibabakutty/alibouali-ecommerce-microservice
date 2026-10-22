
package com.alibou.gateway.config;

import com.alibou.gateway.security.CustomAccessDeniedHandler;
import com.alibou.gateway.security.CustomAuthenticationEntryPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import java.time.Instant;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class SecurityConfigTest {

    private SecurityConfig config;

    @BeforeEach
    void setUp() {
        config = new SecurityConfig(
                mock(CustomAuthenticationEntryPoint.class),
                mock(CustomAccessDeniedHandler.class)
        );
    }

    private Jwt createJwt(String role, boolean includeRole) {
        var builder = Jwt.withTokenValue("test-token")
                .header("alg", "HS512")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(300))
                .subject("test-user");

        if (includeRole) {
            builder.claim("role", role);
        }

        return builder.build();
    }

    @Test
    void converterShouldReturnNoAuthoritiesWhenRoleIsMissing() {
        var authentication = config.jwtAuthenticationConverter()
                .convert(createJwt(null, false))
                .block();

        assertNotNull(authentication);
        assertInstanceOf(JwtAuthenticationToken.class, authentication);
        assertTrue(authentication.getAuthorities().isEmpty());
    }

    @Test
    void converterShouldReturnNoAuthoritiesWhenRoleIsBlank() {
        var authentication = config.jwtAuthenticationConverter()
                .convert(createJwt("   ", true))
                .block();

        assertNotNull(authentication);
        assertTrue(authentication.getAuthorities().isEmpty());
    }

    @Test
    void converterShouldReturnAuthorityWhenRoleIsPresent() {
        var authentication = config.jwtAuthenticationConverter()
                .convert(createJwt("ROLE_USER", true))
                .block();

        assertNotNull(authentication);
        assertEquals(
                List.of("ROLE_USER"),
                authentication.getAuthorities().stream()
                        .map(authority -> authority.getAuthority())
                        .toList()
        );
    }
}