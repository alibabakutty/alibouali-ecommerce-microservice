package com.alibou.ecommerce.kafka;

import com.alibou.ecommerce.customer.CustomerResponse;
import com.alibou.ecommerce.dto.PurchaseResponse;
import com.alibou.ecommerce.entity.PaymentMethod;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import org.mockito.ArgumentCaptor;

class OrderProducerTest {

    @SuppressWarnings("unchecked")
    private final KafkaTemplate<String, OrderConfirmation> kafkaTemplate =
            mock(KafkaTemplate.class);

    private final OrderProducer orderProducer =
            new OrderProducer(kafkaTemplate);

    private OrderConfirmation createConfirmation() {
        CustomerResponse customer = new CustomerResponse(
                "customer-1",
                "John",
                "Doe",
                "john@example.com"
        );

        PurchaseResponse product = new PurchaseResponse(
                1,
                "Laptop",
                "Business laptop",
                new BigDecimal("1250.00"),
                2.0
        );

        return new OrderConfirmation(
                "order-123",
                new BigDecimal("2500.00"),
                PaymentMethod.values()[0],
                customer,
                List.of(product)
        );
    }

    @Test
    void shouldSendOrderConfirmationToKafka() {
        OrderConfirmation confirmation = createConfirmation();

        orderProducer.sendOrderConfirmation(confirmation);

        ArgumentCaptor<Message<OrderConfirmation>> captor =
                ArgumentCaptor.forClass(Message.class);

        verify(kafkaTemplate).send(captor.capture());

        Message<OrderConfirmation> message = captor.getValue();

        assertSame(confirmation, message.getPayload());
        assertEquals(
                "order-topic",
                message.getHeaders().get(KafkaHeaders.TOPIC)
        );
    }

    @Test
    void shouldPreserveOrderConfirmationPayload() {
        OrderConfirmation confirmation = createConfirmation();

        orderProducer.sendOrderConfirmation(confirmation);

        ArgumentCaptor<Message<OrderConfirmation>> captor =
                ArgumentCaptor.forClass(Message.class);

        verify(kafkaTemplate).send(captor.capture());

        OrderConfirmation actual = captor.getValue().getPayload();

        assertEquals("order-123", actual.orderReference());
        assertEquals(
                new BigDecimal("2500.00"),
                actual.totalAmount()
        );
        assertEquals(PaymentMethod.values()[0], actual.paymentMethod());
        assertEquals("customer-1", actual.customer().id());
        assertEquals(1, actual.products().size());
        assertEquals("Laptop", actual.products().get(0).name());
    }

    @Test
    void shouldSendExactlyOneMessagePerInvocation() {
        OrderConfirmation confirmation = createConfirmation();

        orderProducer.sendOrderConfirmation(confirmation);

        verify(kafkaTemplate, times(1)).send(any(Message.class));
        verifyNoMoreInteractions(kafkaTemplate);
    }
}