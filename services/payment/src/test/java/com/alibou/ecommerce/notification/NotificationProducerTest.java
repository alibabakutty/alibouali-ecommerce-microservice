
package com.alibou.ecommerce.notification;

import com.alibou.ecommerce.entity.PaymentMethod;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.mockito.ArgumentCaptor;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class NotificationProducerTest {

    @SuppressWarnings("unchecked")
    private final KafkaTemplate<String, PaymentConfirmation> kafkaTemplate =
            mock(KafkaTemplate.class);

    private final NotificationProducer producer =
            new NotificationProducer(kafkaTemplate);

    private PaymentConfirmation createConfirmation() {
        return new PaymentConfirmation(
                "order-123",
                new BigDecimal("250.00"),
                PaymentMethod.values()[0],
                "John",
                "Doe",
                "john@example.com"
        );
    }

    @Test
    void shouldSendPaymentConfirmationToKafka() {
        PaymentConfirmation confirmation = createConfirmation();

        producer.sendNotification(confirmation);

        ArgumentCaptor<Message<PaymentConfirmation>> captor =
                ArgumentCaptor.forClass(Message.class);

        verify(kafkaTemplate).send(captor.capture());

        Message<PaymentConfirmation> message = captor.getValue();

        assertSame(confirmation, message.getPayload());
        assertEquals(
                "payment-topic",
                message.getHeaders().get(KafkaHeaders.TOPIC)
        );
    }

    @Test
    void shouldPreservePaymentConfirmationFields() {
        PaymentConfirmation confirmation = createConfirmation();

        producer.sendNotification(confirmation);

        ArgumentCaptor<Message<PaymentConfirmation>> captor =
                ArgumentCaptor.forClass(Message.class);

        verify(kafkaTemplate).send(captor.capture());

        PaymentConfirmation actual = captor.getValue().getPayload();

        assertEquals("order-123", actual.orderReference());
        assertEquals(new BigDecimal("250.00"), actual.amount());
        assertEquals(PaymentMethod.values()[0], actual.paymentMethod());
        assertEquals("John", actual.customerFirstName());
        assertEquals("Doe", actual.customerLastName());
        assertEquals("john@example.com", actual.customerEmail());
    }

    @Test
    void shouldSendExactlyOneMessagePerInvocation() {
        producer.sendNotification(createConfirmation());

        verify(kafkaTemplate, times(1)).send(any(Message.class));
        verifyNoMoreInteractions(kafkaTemplate);
    }
}