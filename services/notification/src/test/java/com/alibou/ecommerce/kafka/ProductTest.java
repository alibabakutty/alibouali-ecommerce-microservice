package com.alibou.ecommerce.kafka;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class ProductTest {

    @Test
    void shouldCreateProductAndExposeAllFields() {
        Product product = new Product(
                101,
                "Laptop",
                "Business laptop",
                new BigDecimal("500.00"),
                2.0
        );

        assertEquals(101, product.productId());
        assertEquals("Laptop", product.name());
        assertEquals("Business laptop", product.description());
        assertEquals(new BigDecimal("500.00"), product.price());
        assertEquals(2.0, product.quantity());
    }
}