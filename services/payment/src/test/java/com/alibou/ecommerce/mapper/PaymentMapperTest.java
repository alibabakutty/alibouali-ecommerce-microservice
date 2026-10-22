
package com.alibou.ecommerce.mapper;

import com.alibou.ecommerce.dto.PaymentRequest;
import com.alibou.ecommerce.entity.Payment;
import com.alibou.ecommerce.entity.PaymentMethod;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class PaymentMapperTest {

    private final PaymentMapper mapper = new PaymentMapper();

    @Test
    void shouldMapPaymentRequestToPayment() {
        PaymentMethod method = PaymentMethod.values()[0];

        PaymentRequest request = new PaymentRequest(
                1,
                new BigDecimal("250.00"),
                method,
                42,
                "order-reference-42",
                null
        );

        Payment result = mapper.toPayment(request);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals(42, result.getOrderId());
        assertEquals(method, result.getPaymentMethod());
        assertEquals(new BigDecimal("250.00"), result.getAmount());
    }

    @Test
    void shouldMapNullOptionalValues() {
        PaymentRequest request = new PaymentRequest(
                null,
                null,
                null,
                null,
                null,
                null
        );

        Payment result = mapper.toPayment(request);

        assertNotNull(result);
        assertNull(result.getId());
        assertNull(result.getOrderId());
        assertNull(result.getPaymentMethod());
        assertNull(result.getAmount());
    }
}