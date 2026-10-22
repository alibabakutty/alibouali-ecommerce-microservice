
package com.alibou.ecommerce.controller;

import com.alibou.ecommerce.dto.PaymentRequest;
import com.alibou.ecommerce.entity.PaymentMethod;
import com.alibou.ecommerce.service.PaymentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentControllerTest {

    @Mock
    private PaymentService service;

    @InjectMocks
    private PaymentController controller;

    @Test
    void shouldCreatePaymentAndReturnId() {
        PaymentRequest request = new PaymentRequest(
                1,
                new BigDecimal("250.00"),
                PaymentMethod.values()[0],
                42,
                "order-reference-42",
                null
        );

        when(service.createPayment(request)).thenReturn(100);

        var response = controller.createPayment(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(100, response.getBody());
        verify(service).createPayment(request);
    }

    @Test
    void shouldPropagateServiceException() {
        PaymentRequest request = new PaymentRequest(
                1,
                new BigDecimal("250.00"),
                PaymentMethod.values()[0],
                42,
                "order-reference-42",
                null
        );

        when(service.createPayment(request))
                .thenThrow(new RuntimeException("Payment failed"));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> controller.createPayment(request)
        );

        assertEquals("Payment failed", exception.getMessage());
        verify(service).createPayment(request);
    }
}