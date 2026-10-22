package com.alibou.ecommerce.dto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

class PurchaseRequestTest {

    private static ValidatorFactory factory;
    private static Validator validator;

    @BeforeAll
    static void setUp() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    static void tearDown() {
        factory.close();
    }

    @Test
    void shouldAcceptValidPurchaseRequest() {
        PurchaseRequest request = new PurchaseRequest(1, 2.0);

        Set<?> violations = validator.validate(request);

        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldRejectNullProductId() {
        PurchaseRequest request = new PurchaseRequest(null, 2.0);

        var violations = validator.validate(request);

        assertTrue(
                violations.stream().anyMatch(v ->
                        v.getMessage().equals("Product is mandatory"))
        );
    }

    @Test
    void shouldRejectZeroQuantity() {
        PurchaseRequest request = new PurchaseRequest(1, 0.0);

        var violations = validator.validate(request);

        assertTrue(
                violations.stream().anyMatch(v ->
                        v.getMessage().equals("Quantity is mandatory"))
        );
    }

    @Test
    void shouldRejectNegativeQuantity() {
        PurchaseRequest request = new PurchaseRequest(1, -1.0);

        var violations = validator.validate(request);

        assertTrue(
                violations.stream().anyMatch(v ->
                        v.getMessage().equals("Quantity is mandatory"))
        );
    }
}