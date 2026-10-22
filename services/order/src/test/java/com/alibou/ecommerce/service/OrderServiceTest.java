package com.alibou.ecommerce.service;

import com.alibou.ecommerce.customer.CustomerClient;
import com.alibou.ecommerce.customer.CustomerResponse;
import com.alibou.ecommerce.dto.*;
import com.alibou.ecommerce.entity.PaymentMethod;
import com.alibou.ecommerce.exception.BusinessException;
import com.alibou.ecommerce.kafka.OrderConfirmation;
import com.alibou.ecommerce.kafka.OrderProducer;
import com.alibou.ecommerce.mapper.OrderMapper;
import com.alibou.ecommerce.orderline.OrderLineService;
import com.alibou.ecommerce.payment.PaymentClient;
import com.alibou.ecommerce.payment.PaymentRequest;
import com.alibou.ecommerce.product.ProductClient;
import com.alibou.ecommerce.repository.OrderRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private CustomerClient customerClient;

    @Mock
    private ProductClient productClient;

    @Mock
    private OrderRepository repository;

    @Mock
    private OrderMapper mapper;

    @Mock
    private OrderLineService orderLineService;

    @Mock
    private OrderProducer orderProducer;

    @Mock
    private PaymentClient paymentClient;

    @InjectMocks
    private OrderService orderService;

    private OrderRequest orderRequest;

    private com.alibou.ecommerce.entity.Order order;

    @BeforeEach
    void setUp() {
        orderRequest = mock(OrderRequest.class);
        order = mock(com.alibou.ecommerce.entity.Order.class);
    }



    @Test
    void createOrder_ShouldCreateOrderAndTriggerPaymentAndConfirmation() {
        CustomerResponse customer = mock(CustomerResponse.class);
        PurchaseRequest purchaseRequest = mock(PurchaseRequest.class);

        PurchaseResponse purchasedProduct = new PurchaseResponse(
                10,
                "Test Product",
                "Test product description",
                new BigDecimal("50.00"),
                2.0
        );

        List<PurchaseRequest> purchaseRequests = List.of(purchaseRequest);
        List<PurchaseResponse> purchasedProducts = List.of(purchasedProduct);

        String customerId = "6ab643363d807c154c3f559a";

        when(orderRequest.customerId()).thenReturn(customerId);
        when(orderRequest.products()).thenReturn(purchaseRequests);
        when(orderRequest.amount()).thenReturn(new BigDecimal("100.00"));
        when(orderRequest.paymentMethod()).thenReturn(PaymentMethod.CREDIT_CARD);
        when(orderRequest.reference()).thenReturn("ORDER-001");

        when(purchaseRequest.productId()).thenReturn(10);
        when(purchaseRequest.quantity()).thenReturn(2.0);

        when(customerClient.findCustomerById(customerId))
                .thenReturn(Optional.of(customer));

        when(productClient.purchaseProducts(purchaseRequests))
                .thenReturn(purchasedProducts);

        when(mapper.toOrder(orderRequest)).thenReturn(order);
        when(repository.save(order)).thenReturn(order);

        when(order.getId()).thenReturn(100);
        when(order.getReference()).thenReturn("ORDER-001");

        Integer result = orderService.createOrder(orderRequest);

        assertEquals(100, result);

        verify(customerClient).findCustomerById(customerId);
        verify(productClient).purchaseProducts(purchaseRequests);
        verify(mapper).toOrder(orderRequest);
        verify(repository).save(order);

        verify(orderLineService).saveOrderLine(
                new OrderLineRequest(null, 100, 10, 2.0)
        );

        verify(paymentClient).requestOrderPayment(any(PaymentRequest.class));
        verify(orderProducer).sendOrderConfirmation(any(OrderConfirmation.class));
    }

    @Test
    void createOrder_ShouldThrowBusinessExceptionWhenCustomerDoesNotExist() {
        String missingCustomerId = "nonexistent-customer-id";

        when(orderRequest.customerId()).thenReturn(missingCustomerId);

        when(customerClient.findCustomerById(missingCustomerId))
                .thenReturn(Optional.empty());

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> orderService.createOrder(orderRequest)
        );

        assertEquals(
                "Cannot create order:: No customer exists with the provided ID",
                exception.getMessage()
        );

        verify(customerClient).findCustomerById(missingCustomerId);

        verifyNoInteractions(
                productClient,
                repository,
                mapper,
                orderLineService,
                paymentClient,
                orderProducer
        );
    }

    @Test
    void findAll_ShouldReturnMappedOrders() {
        var firstOrder =
                mock(com.alibou.ecommerce.entity.Order.class);
        var secondOrder =
                mock(com.alibou.ecommerce.entity.Order.class);

        var firstResponse = mock(OrderResponse.class);
        var secondResponse = mock(OrderResponse.class);

        when(repository.findAll())
                .thenReturn(List.of(firstOrder, secondOrder));

        when(mapper.fromOrder(firstOrder)).thenReturn(firstResponse);
        when(mapper.fromOrder(secondOrder)).thenReturn(secondResponse);

        List<OrderResponse> result = orderService.findAll();

        assertEquals(2, result.size());
        assertSame(firstResponse, result.get(0));
        assertSame(secondResponse, result.get(1));

        verify(repository).findAll();
        verify(mapper).fromOrder(firstOrder);
        verify(mapper).fromOrder(secondOrder);
    }

    @Test
    void findAll_ShouldReturnEmptyListWhenNoOrdersExist() {
        when(repository.findAll()).thenReturn(List.of());

        List<OrderResponse> result = orderService.findAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(repository).findAll();
        verifyNoInteractions(mapper);
    }

    @Test
    void findById_ShouldReturnOrderWhenItExists() {
        var response = mock(OrderResponse.class);

        when(repository.findById(100))
                .thenReturn(Optional.of(order));

        when(mapper.fromOrder(order)).thenReturn(response);

        OrderResponse result = orderService.findById(100);

        assertSame(response, result);

        verify(repository).findById(100);
        verify(mapper).fromOrder(order);
    }

    @Test
    void findById_ShouldThrowExceptionWhenOrderDoesNotExist() {
        when(repository.findById(999))
                .thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> orderService.findById(999)
        );

        assertEquals(
                "No order found with the provided ID: 999",
                exception.getMessage()
        );

        verify(repository).findById(999);
        verifyNoInteractions(mapper);
    }
}