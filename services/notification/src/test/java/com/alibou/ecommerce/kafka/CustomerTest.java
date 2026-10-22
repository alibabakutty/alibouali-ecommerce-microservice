package com.alibou.ecommerce.kafka;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CustomerTest {

    @Test
    void shouldCreateCustomerAndExposeAllFields() {
        Customer customer = new Customer(
                "customer-101",
                "John",
                "Smith",
                "john.smith@example.com"
        );

        assertEquals("customer-101", customer.id());
        assertEquals("John", customer.firstName());
        assertEquals("Smith", customer.lastName());
        assertEquals("john.smith@example.com", customer.email());
    }
}