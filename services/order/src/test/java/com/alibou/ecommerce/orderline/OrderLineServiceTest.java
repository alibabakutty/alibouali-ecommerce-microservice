
package com.alibou.ecommerce.orderline;

import com.alibou.ecommerce.dto.OrderLineRequest;
import com.alibou.ecommerce.entity.Order;
import com.alibou.ecommerce.entity.OrderLine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderLineServiceTest {

    @Mock
    private OrderLineRepository repository;

    @Mock
    private OrderLineMapper mapper;

    @InjectMocks
    private OrderLineService service;

    private OrderLineRequest request;
    private OrderLine orderLine;

    @BeforeEach
    void setUp() {
        request = new OrderLineRequest(1, 10, 100, 2.0);

        orderLine = OrderLine.builder()
                .id(1)
                .order(Order.builder().id(10).build())
                .productId(100)
                .quantity(2.0)
                .build();
    }

    @Test
    void shouldSaveOrderLine() {
        when(mapper.toOrderLine(request)).thenReturn(orderLine);
        when(repository.save(orderLine)).thenReturn(orderLine);

        Integer result = service.saveOrderLine(request);

        assertEquals(1, result);
        verify(mapper).toOrderLine(request);
        verify(repository).save(orderLine);
    }

    @Test
    void shouldFindAllOrderLinesByOrderId() {
        OrderLine second = OrderLine.builder()
                .id(2)
                .order(Order.builder().id(10).build())
                .productId(200)
                .quantity(3.0)
                .build();

        when(repository.findAllByOrderId(10))
                .thenReturn(List.of(orderLine, second));

        when(mapper.toOrderLineResponse(orderLine))
                .thenReturn(new OrderLineResponse(1, 2.0));

        when(mapper.toOrderLineResponse(second))
                .thenReturn(new OrderLineResponse(2, 3.0));

        List<OrderLineResponse> result = service.findAllByOrderId(10);

        assertEquals(2, result.size());
        assertEquals(1, result.get(0).id());
        assertEquals(2.0, result.get(0).quantity());
        assertEquals(2, result.get(1).id());
        assertEquals(3.0, result.get(1).quantity());

        verify(repository).findAllByOrderId(10);
        verify(mapper).toOrderLineResponse(orderLine);
        verify(mapper).toOrderLineResponse(second);
    }

    @Test
    void shouldReturnEmptyListWhenOrderHasNoLines() {
        when(repository.findAllByOrderId(99)).thenReturn(List.of());

        List<OrderLineResponse> result = service.findAllByOrderId(99);

        assertEquals(0, result.size());
        verify(repository).findAllByOrderId(99);
    }
}