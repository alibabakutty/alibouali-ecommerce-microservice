package com.alibou.ecommerce.handler;

import com.alibou.ecommerce.dto.ErrorResponse;
import com.alibou.ecommerce.exception.BusinessException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler =
            new GlobalExceptionHandler();

    @Test
    void shouldHandleBusinessException() {
        BusinessException exception =
                new BusinessException("Insufficient quantity");

        var response = handler.handle(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Insufficient quantity", response.getBody());
    }

    @Test
    void shouldHandleEntityNotFoundException() {
        EntityNotFoundException exception =
                new EntityNotFoundException("Order not found");

        var response = handler.handle(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Order not found", response.getBody());
    }

    @Test
    void shouldHandleValidationException() {
        BindingResult bindingResult = mock(BindingResult.class);

        FieldError fieldError = new FieldError(
                "purchaseRequest",
                "quantity",
                "Quantity is mandatory"
        );

        when(bindingResult.getAllErrors())
                .thenReturn(List.of(fieldError));

        MethodArgumentNotValidException exception =
                mock(MethodArgumentNotValidException.class);

        when(exception.getBindingResult()).thenReturn(bindingResult);

        var response = handler.handle(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertInstanceOf(ErrorResponse.class, response.getBody());

        verify(bindingResult).getAllErrors();
    }

    @Test
    void shouldHandleValidationExceptionWithMultipleFieldErrors() {
        BindingResult bindingResult = mock(BindingResult.class);

        FieldError productError = new FieldError(
                "purchaseRequest",
                "productId",
                "Product is mandatory"
        );

        FieldError quantityError = new FieldError(
                "purchaseRequest",
                "quantity",
                "Quantity is mandatory"
        );

        when(bindingResult.getAllErrors())
                .thenReturn(List.of(productError, quantityError));

        MethodArgumentNotValidException exception =
                mock(MethodArgumentNotValidException.class);

        when(exception.getBindingResult()).thenReturn(bindingResult);

        var response = handler.handle(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());

        verify(bindingResult).getAllErrors();
    }

    @Test
    void shouldHandleValidationExceptionWithNoFieldErrors() {
        BindingResult bindingResult = mock(BindingResult.class);

        when(bindingResult.getAllErrors()).thenReturn(List.of());

        MethodArgumentNotValidException exception =
                mock(MethodArgumentNotValidException.class);

        when(exception.getBindingResult()).thenReturn(bindingResult);

        var response = handler.handle(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());

        verify(bindingResult).getAllErrors();
    }
}