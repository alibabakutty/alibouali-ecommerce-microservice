
package com.alibou.ecommerce.orderline;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderLineControllerTest {

    private final OrderLineService service = mock(OrderLineService.class);
    private final OrderLineController controller =
            new OrderLineController(service);

    @Test
    void shouldReturnOrderLinesForOrderId() {
        Integer orderId = 10;
        List<OrderLineResponse> expected = List.of(
                new OrderLineResponse(1, 2.0),
                new OrderLineResponse(2, 3.0)
        );

        when(service.findAllByOrderId(orderId)).thenReturn(expected);

        var response = controller.findByOrderId(orderId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expected, response.getBody());
        verify(service).findAllByOrderId(orderId);
    }

    @Test
    void shouldReturnEmptyListWhenOrderHasNoLines() {
        Integer orderId = 99;
        when(service.findAllByOrderId(orderId)).thenReturn(List.of());

        var response = controller.findByOrderId(orderId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
        verify(service).findAllByOrderId(orderId);
    }
}