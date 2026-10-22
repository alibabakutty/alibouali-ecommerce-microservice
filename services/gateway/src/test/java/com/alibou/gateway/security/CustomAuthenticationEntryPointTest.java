
package com.alibou.gateway.security;

import com.alibou.gateway.security_dto.SecurityErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.security.authentication.BadCredentialsException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CustomAuthenticationEntryPointTest {

    private ObjectMapper objectMapper;
    private CustomAuthenticationEntryPoint entryPoint;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        entryPoint = new CustomAuthenticationEntryPoint(objectMapper);
    }

    @Test
    void commenceShouldReturnUnauthorizedJsonResponse() {
        var exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/api/v1/products").build()
        );

        entryPoint.commence(
                exchange,
                new BadCredentialsException("Invalid credentials")
        ).block();

        assertEquals(
                HttpStatus.UNAUTHORIZED,
                exchange.getResponse().getStatusCode()
        );
        assertEquals(
                MediaType.APPLICATION_JSON,
                exchange.getResponse().getHeaders().getContentType()
        );

        var body = exchange.getResponse().getBodyAsString().block();
        assertNotNull(body);

        assertTrue(body.contains("\"status\":401"));
        assertTrue(body.contains("\"error\":\"Unauthorized\""));
        assertTrue(body.contains("\"message\":\"Authentication is required\""));
    }

    @Test
    void responseDtoShouldMatchExpectedValues() throws Exception {
        var exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/private").build()
        );

        entryPoint.commence(
                exchange,
                new BadCredentialsException("Missing token")
        ).block();

        var body = exchange.getResponse().getBodyAsString().block();
        assertNotNull(body);

        SecurityErrorResponse response =
                objectMapper.readValue(body, SecurityErrorResponse.class);

        assertEquals(401, response.status());
        assertEquals("Unauthorized", response.error());
        assertEquals("Authentication is required", response.message());
    }



    @Test
    void commenceShouldPropagateSerializationFailure() throws Exception {
        ObjectMapper failingMapper = org.mockito.Mockito.mock(ObjectMapper.class);

        org.mockito.Mockito.when(
                failingMapper.writeValueAsBytes(
                        org.mockito.ArgumentMatchers.any(
                                SecurityErrorResponse.class
                        )
                )
        ).thenThrow(new com.fasterxml.jackson.core.JsonProcessingException(
                "Serialization failed"
        ) {});

        CustomAuthenticationEntryPoint failingEntryPoint =
                new CustomAuthenticationEntryPoint(failingMapper);

        var exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/private").build()
        );

        org.junit.jupiter.api.Assertions.assertThrows(
                RuntimeException.class,
                () -> failingEntryPoint.commence(
                        exchange,
                        new org.springframework.security.authentication
                                .BadCredentialsException("Invalid credentials")
                ).block()
        );
    }
}