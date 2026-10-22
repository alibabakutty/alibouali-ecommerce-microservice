package com.alibou.ecommerce.dto;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class PurchaseResponseTest {

    @Test
    void shouldCreatePurchaseResponseWithExpectedValues() {
        PurchaseResponse response = new PurchaseResponse(
                1,
                "Laptop",
                "Business laptop",
                new BigDecimal("1250.00"),
                2.0
        );

        assertEquals(1, response.productId());
        assertEquals("Laptop", response.name());
        assertEquals("Business laptop", response.description());
        assertEquals(new BigDecimal("1250.00"), response.price());
        assertEquals(2.0, response.quantity());
    }

    @Test
    void shouldSupportEqualRecords() {
        PurchaseResponse first = new PurchaseResponse(
                1, "Laptop", "Business laptop",
                new BigDecimal("1250.00"), 2.0
        );

        PurchaseResponse second = new PurchaseResponse(
                1, "Laptop", "Business laptop",
                new BigDecimal("1250.00"), 2.0
        );

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    void shouldReturnExpectedStringRepresentation() {
        PurchaseResponse response = new PurchaseResponse(
                1, "Laptop", "Business laptop",
                new BigDecimal("1250.00"), 2.0
        );

        assertTrue(response.toString().contains("Laptop"));
    }
}