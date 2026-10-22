package com.alibou.ecommerce.kafka;

import com.alibou.ecommerce.email.gmail.GmailEmailService;
import com.alibou.ecommerce.entity.Notification;
import com.alibou.ecommerce.entity.NotificationType;
import com.alibou.ecommerce.kafka.payment.PaymentConfirmation;
import com.alibou.ecommerce.kafka.payment.PaymentMethod;
import com.alibou.ecommerce.repository.NotificationRepository;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationConsumerTest {

    @Mock
    private NotificationRepository repository;

    @Mock
    private GmailEmailService gmailEmailService;

    @InjectMocks
    private NotificationConsumer notificationConsumer;

    @Test
    void consumePaymentSuccessNotification_ShouldSaveNotificationAndSendEmail()
            throws MessagingException {

        PaymentConfirmation confirmation = new PaymentConfirmation(
                "ORDER-001",
                new BigDecimal("100.00"),
                PaymentMethod.CREDIT_CARD,
                "Michael",
                "Anderson",
                "michael.anderson@gmail.com"
        );

        notificationConsumer.consumePaymentSuccessNotification(confirmation);

        ArgumentCaptor<Notification> captor =
                ArgumentCaptor.forClass(Notification.class);

        verify(repository).save(captor.capture());

        Notification saved = captor.getValue();

        assertEquals(NotificationType.PAYMENT_CONFIRMATION, saved.getType());
        assertNotNull(saved.getNotificationDate());
        assertTrue(saved.getNotificationDate().isBefore(
                LocalDateTime.now().plusSeconds(1)));
        assertEquals(confirmation, saved.getPaymentConfirmation());

        verify(gmailEmailService).sendPaymentSuccessEmail(
                "michael.anderson@gmail.com",
                "Michael Anderson",
                new BigDecimal("100.00"),
                "ORDER-001"
        );
    }

    @Test
    void consumeOrderConfirmationNotification_ShouldSaveNotificationAndSendEmail()
            throws MessagingException {

        Customer customer = mock(Customer.class);
        Product product = mock(Product.class);

        List<Product> products = List.of(product);

        OrderConfirmation confirmation = new OrderConfirmation(
                "ORDER-002",
                new BigDecimal("250.00"),
                PaymentMethod.CREDIT_CARD,
                customer,
                products
        );

        when(customer.firstName()).thenReturn("John");
        when(customer.lastName()).thenReturn("Smith");
        when(customer.email()).thenReturn("john.smith@example.com");

        notificationConsumer.consumeOrderConfirmationNotification(confirmation);

        ArgumentCaptor<Notification> captor =
                ArgumentCaptor.forClass(Notification.class);

        verify(repository).save(captor.capture());

        Notification saved = captor.getValue();

        assertEquals(NotificationType.ORDER_CONFIRMATION, saved.getType());
        assertNotNull(saved.getNotificationDate());
        assertEquals(confirmation, saved.getOrderConfirmation());

        verify(gmailEmailService).sendOrderConfirmationEmail(
                "john.smith@example.com",
                "John Smith",
                new BigDecimal("250.00"),
                "ORDER-002",
                products
        );
    }
}

