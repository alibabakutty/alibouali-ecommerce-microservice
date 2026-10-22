
package com.alibou.customer.handler;

import com.alibou.customer.exception.CustomerNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler =
            new GlobalExceptionHandler();

    @Test
    void handleCustomerNotFoundReturnsNotFoundAndMessage() {
        CustomerNotFoundException exception =
                new CustomerNotFoundException("Customer not found");

        ResponseEntity<String> response = handler.handle(exception);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Customer not found", response.getBody());
    }
}