
package com.alibou.product.handler;

import com.alibou.product.dto.ErrorResponse;
import com.alibou.product.exception.ProductPurchaseException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler =
            new GlobalExceptionHandler();

    @Test
    void shouldHandleProductPurchaseException() {
        ProductPurchaseException exception =
                new ProductPurchaseException("Purchase failed");

        ResponseEntity<String> response = handler.handle(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Purchase failed", response.getBody());
    }

    @Test
    void shouldHandleEntityNotFoundException() {
        EntityNotFoundException exception =
                new EntityNotFoundException("Product not found");

        ResponseEntity<String> response = handler.handle(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Product not found", response.getBody());
    }

    @Test
    void shouldHandleValidationErrors() {
        Object target = new Object();
        BeanPropertyBindingResult bindingResult =
                new BeanPropertyBindingResult(target, "productRequest");

        bindingResult.addError(
                new FieldError(
                        "productRequest",
                        "name",
                        target,
                        false,
                        null,
                        null,
                        "Product name is required"
                )
        );

        MethodArgumentNotValidException exception =
                new MethodArgumentNotValidException(null, bindingResult);

        ResponseEntity<ErrorResponse> response = handler.handle(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(
                "Product name is required",
                response.getBody().errors().get("name")
        );
    }
}