package com.alibou.ecommerce.exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BusinessExceptionTest {

    @Test
    void shouldStoreExceptionMessage() {
        BusinessException exception =
                new BusinessException("Insufficient product quantity");

        assertEquals(
                "Insufficient product quantity",
                exception.getMessage()
        );

        assertEquals(
                "Insufficient product quantity",
                exception.getMsg()
        );
    }

    @Test
    void shouldNotBeEqualToNull() {
        BusinessException exception =
                new BusinessException("Invalid purchase");

        assertNotEquals(null, exception);
    }

    @Test
    void shouldNotBeEqualToDifferentType() {
        BusinessException exception =
                new BusinessException("Invalid purchase");

        assertNotEquals("Invalid purchase", exception);
    }
}