package com.alibou.product.dto;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class ProductDtoTest {

    @Test
    void productRequestShouldStoreAllFields() {
        ProductRequest request = new ProductRequest(
                1, "Laptop", "Gaming laptop",
                10.0, new BigDecimal("1299.99"), 2
        );

        assertEquals(1, request.id());
        assertEquals("Laptop", request.name());
        assertEquals("Gaming laptop", request.description());
        assertEquals(10.0, request.availableQuantity());
        assertEquals(new BigDecimal("1299.99"), request.price());
        assertEquals(2, request.categoryId());
    }

    @Test
    void productResponseShouldStoreAllFields() {
        ProductResponse response = new ProductResponse(
                1, "Laptop", "Gaming laptop",
                10.0, new BigDecimal("1299.99"),
                2, "Electronics", "Electronic products"
        );

        assertEquals(1, response.id());
        assertEquals("Laptop", response.name());
        assertEquals("Gaming laptop", response.description());
        assertEquals(10.0, response.availableQuantity());
        assertEquals(new BigDecimal("1299.99"), response.price());
        assertEquals(2, response.categoryId());
        assertEquals("Electronics", response.categoryName());
        assertEquals("Electronic products", response.categoryDescription());
    }

    @Test
    void productPurchaseRequestShouldStoreFields() {
        ProductPurchaseRequest request =
                new ProductPurchaseRequest(5, 3.0);

        assertEquals(5, request.productId());
        assertEquals(3.0, request.quantity());
    }

    @Test
    void productPurchaseResponseShouldStoreFields() {
        ProductPurchaseResponse response =
                new ProductPurchaseResponse(
                        5, "Keyboard", "Mechanical keyboard",
                        new BigDecimal("79.99"), 3.0
                );

        assertEquals(5, response.productId());
        assertEquals("Keyboard", response.name());
        assertEquals("Mechanical keyboard", response.description());
        assertEquals(new BigDecimal("79.99"), response.price());
        assertEquals(3.0, response.quantity());
    }

    @Test
    void errorResponseShouldStoreErrors() {
        Map<String, String> errors =
                Map.of("name", "Product name is required");

        ErrorResponse response = new ErrorResponse(errors);

        assertEquals(errors, response.errors());
    }
}