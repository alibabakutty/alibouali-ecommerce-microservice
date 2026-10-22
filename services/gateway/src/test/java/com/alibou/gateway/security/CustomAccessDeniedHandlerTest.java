
package com.alibou.gateway.security;

import com.alibou.gateway.security_dto.SecurityErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.security.access.AccessDeniedException;
import static org.junit.jupiter.api.Assertions.*;

class CustomAccessDeniedHandlerTest {

    private ObjectMapper objectMapper;
    private CustomAccessDeniedHandler handler;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        handler = new CustomAccessDeniedHandler(objectMapper);
    }

    @Test
    void handleShouldReturnForbiddenJsonResponse() throws Exception {
        var exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/api/v1/products").build());

        handler.handle(
                exchange,
                new AccessDeniedException("Access denied")
        ).block();

        assertEquals(
                HttpStatus.FORBIDDEN,
                exchange.getResponse().getStatusCode()
        );
        assertEquals(
                MediaType.APPLICATION_JSON,
                exchange.getResponse().getHeaders().getContentType()
        );

        String body = exchange.getResponse().getBodyAsString().block();
        assertNotNull(body);

        SecurityErrorResponse response =
                objectMapper.readValue(body, SecurityErrorResponse.class);

        assertEquals(403, response.status());
        assertEquals("Forbidden", response.error());
        assertEquals(
                "You do not have permission to access this resource",
                response.message()
        );
    }


    @Test
    void handleShouldPropagateSerializationFailure() throws Exception {
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

        CustomAccessDeniedHandler failingHandler =
                new CustomAccessDeniedHandler(failingMapper);

        var exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/private").build()
        );

        org.junit.jupiter.api.Assertions.assertThrows(
                RuntimeException.class,
                () -> failingHandler.handle(
                        exchange,
                        new org.springframework.security.access.AccessDeniedException(
                                "Access denied"
                        )
                ).block()
        );
    }
}