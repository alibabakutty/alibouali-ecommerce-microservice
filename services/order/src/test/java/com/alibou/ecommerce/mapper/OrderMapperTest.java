
package com.alibou.ecommerce.mapper;

import com.alibou.ecommerce.dto.OrderRequest;
import com.alibou.ecommerce.dto.OrderResponse;
import com.alibou.ecommerce.dto.PurchaseRequest;
import com.alibou.ecommerce.entity.Order;
import com.alibou.ecommerce.entity.PaymentMethod;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;

class OrderMapperTest {

    private final OrderMapper mapper = new OrderMapper();

    @Test
    void shouldMapRequestToOrder() {
        OrderRequest request = new OrderRequest(
                1,
                "ORDER-001",
                new BigDecimal("250.00"),
                PaymentMethod.CREDIT_CARD,
                "customer-1",
                List.of(new PurchaseRequest(100, 2.0))
        );

        Order result = mapper.toOrder(request);

        assertEquals(1, result.getId());
        assertEquals("ORDER-001", result.getReference());
        assertEquals(new BigDecimal("250.00"), result.getTotalAmount());
        assertEquals(PaymentMethod.CREDIT_CARD, result.getPaymentMethod());
        assertEquals("customer-1", result.getCustomerId());
    }

    @Test
    void shouldMapOrderToResponse() {
        Order entity = Order.builder()
                .id(2)
                .reference("ORDER-002")
                .totalAmount(new BigDecimal("99.50"))
                .paymentMethod(PaymentMethod.PAYPAL)
                .customerId("customer-2")
                .build();

        OrderResponse result = mapper.fromOrder(entity);

        assertEquals(2, result.id());
        assertEquals("ORDER-002", result.reference());
        assertEquals(new BigDecimal("99.50"), result.amount());
        assertEquals(PaymentMethod.PAYPAL, result.paymentMethod());
        assertEquals("customer-2", result.customerId());
    }
}