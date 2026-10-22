package com.alibou.ecommerce.controller;

import com.alibou.ecommerce.dto.OrderRequest;
import com.alibou.ecommerce.dto.OrderResponse;
import com.alibou.ecommerce.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderControllerTest {

    private final OrderService orderService = mock(OrderService.class);
    private final OrderController controller =
            new OrderController(orderService);

    @Test
    void shouldCreateOrder() {
        OrderRequest request = mock(OrderRequest.class);

        when(orderService.createOrder(request)).thenReturn(101);

        var response = controller.createOrder(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(101, response.getBody());

        verify(orderService).createOrder(request);
    }

    @Test
    void shouldFindAllOrders() {
        OrderResponse order = mock(OrderResponse.class);
        List<OrderResponse> orders = List.of(order);

        when(orderService.findAll()).thenReturn(orders);

        var response = controller.findAll();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(orders, response.getBody());

        verify(orderService).findAll();
    }

    @Test
    void shouldFindOrderById() {
        int orderId = 101;
        OrderResponse expected = mock(OrderResponse.class);

        when(orderService.findById(orderId)).thenReturn(expected);

        var response = controller.findById(orderId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(expected, response.getBody());

        verify(orderService).findById(orderId);
    }
}