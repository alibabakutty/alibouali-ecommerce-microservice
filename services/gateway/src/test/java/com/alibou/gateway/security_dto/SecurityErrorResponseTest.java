
package com.alibou.gateway.security_dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SecurityErrorResponseTest {

    @Test
    void shouldStoreAndReturnAllFields() {
        SecurityErrorResponse response = new SecurityErrorResponse(
                403,
                "Forbidden",
                "You do not have permission to access this resource"
        );

        assertEquals(403, response.status());
        assertEquals("Forbidden", response.error());
        assertEquals(
                "You do not have permission to access this resource",
                response.message()
        );
    }
}