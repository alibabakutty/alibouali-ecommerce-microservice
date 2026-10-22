
package com.alibou.gateway.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import java.net.InetAddress;
import java.net.InetSocketAddress;

import static org.junit.jupiter.api.Assertions.*;

class RateLimitConfigTest {

    private RateLimitConfig config;

    @BeforeEach
    void setUp() {
        config = new RateLimitConfig();
    }


    @Test
    void userKeyResolverShouldReturnPrincipalName() {
        var principal = (java.security.Principal) () -> "test-user";

        var exchange = org.mockito.Mockito.mock(
                org.springframework.web.server.ServerWebExchange.class
        );

        org.mockito.Mockito.when(exchange.getPrincipal())
                .thenReturn(reactor.core.publisher.Mono.just(principal));

        assertEquals(
                "test-user",
                config.userKeyResolver().resolve(exchange).block()
        );
    }

    @Test
    void userKeyResolverShouldReturnAnonymousWithoutPrincipal() {
        var exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/api/v1/products").build());

        assertEquals(
                "anonymous",
                config.userKeyResolver().resolve(exchange).block()
        );
    }

    @Test
    void ipKeyResolverShouldReturnRemoteIpAddress() throws Exception {
        var exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/api/v1/products")
                        .remoteAddress(new InetSocketAddress(
                                InetAddress.getByName("192.168.1.10"), 8080))
                        .build());

        assertEquals(
                "192.168.1.10",
                config.ipKeyResolver().resolve(exchange).block()
        );
    }

    @Test
    void ipKeyResolverShouldReturnUnknownWhenRemoteAddressMissing() {
        var exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/api/v1/products").build());

        assertEquals(
                "unknown-ip",
                config.ipKeyResolver().resolve(exchange).block()
        );
    }


    @Test
    void ipKeyResolverShouldReturnUnknownWhenUnderlyingIpIsMissing() {
        var exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/api/v1/products")
                        .remoteAddress(
                                java.net.InetSocketAddress.createUnresolved(
                                        "unresolved-host", 8080
                                )
                        )
                        .build()
        );

        assertEquals(
                "unknown-ip",
                config.ipKeyResolver().resolve(exchange).block()
        );
    }
}