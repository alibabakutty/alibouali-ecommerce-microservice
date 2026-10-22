
package com.alibou.ecommerce.orderline;

import com.alibou.ecommerce.dto.OrderLineRequest;
import com.alibou.ecommerce.entity.OrderLine;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class OrderLineMapperTest {

    private final OrderLineMapper mapper = new OrderLineMapper();

    @Test
    void shouldMapRequestToOrderLine() {
        OrderLineRequest request = new OrderLineRequest(
                1, 10, 100, 2.5
        );

        OrderLine result = mapper.toOrderLine(request);

        assertEquals(1, result.getId());
        assertEquals(10, result.getOrder().getId());
        assertEquals(100, result.getProductId());
        assertEquals(2.5, result.getQuantity());
    }

    @Test
    void shouldMapOrderLineToResponse() {
        OrderLine entity = OrderLine.builder()
                .id(5)
                .productId(100)
                .quantity(4.0)
                .build();

        OrderLineResponse result = mapper.toOrderLineResponse(entity);

        assertEquals(5, result.id());
        assertEquals(4.0, result.quantity());
    }
}