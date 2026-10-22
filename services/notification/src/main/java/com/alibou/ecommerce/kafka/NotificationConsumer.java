package com.alibou.ecommerce.kafka;

import com.alibou.ecommerce.email.gmail.GmailEmailService;
import com.alibou.ecommerce.entity.Notification;
import com.alibou.ecommerce.kafka.payment.PaymentConfirmation;
import com.alibou.ecommerce.repository.NotificationRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static com.alibou.ecommerce.entity.NotificationType.ORDER_CONFIRMATION;
import static com.alibou.ecommerce.entity.NotificationType.PAYMENT_CONFIRMATION;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationConsumer {

    private final NotificationRepository repository;
    private final GmailEmailService gmailEmailService;

    @KafkaListener(topics = "payment-topic")
    public void consumePaymentSuccessNotification(
            PaymentConfirmation paymentConfirmation
    ) throws MessagingException {

        log.info(
                "Consuming message from payment-topic: {}",
                paymentConfirmation
        );

        repository.save(
                Notification.builder()
                        .type(PAYMENT_CONFIRMATION)
                        .notificationDate(LocalDateTime.now())
                        .paymentConfirmation(paymentConfirmation)
                        .build()
        );

        var customerName =
                paymentConfirmation.customerFirstName()
                        + " "
                        + paymentConfirmation.customerLastName();

        gmailEmailService.sendPaymentSuccessEmail(
                paymentConfirmation.customerEmail(),
                customerName,
                paymentConfirmation.amount(),
                paymentConfirmation.orderReference()
        );
    }

    @KafkaListener(topics = "order-topic")
    public void consumeOrderConfirmationNotification(
            OrderConfirmation orderConfirmation
    ) throws MessagingException {

        log.info(
                "Consuming message from order-topic: {}",
                orderConfirmation
        );

        repository.save(
                Notification.builder()
                        .type(ORDER_CONFIRMATION)
                        .notificationDate(LocalDateTime.now())
                        .orderConfirmation(orderConfirmation)
                        .build()
        );

        var customerName =
                orderConfirmation.customer().firstName()
                        + " "
                        + orderConfirmation.customer().lastName();

        gmailEmailService.sendOrderConfirmationEmail(
                orderConfirmation.customer().email(),
                customerName,
                orderConfirmation.totalAmount(),
                orderConfirmation.orderReference(),
                orderConfirmation.products()
        );
    }
}