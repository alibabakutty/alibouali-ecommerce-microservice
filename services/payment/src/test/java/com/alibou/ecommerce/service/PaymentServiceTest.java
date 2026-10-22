
package com.alibou.ecommerce.service;

import com.alibou.ecommerce.dto.Customer;
import com.alibou.ecommerce.dto.PaymentRequest;
import com.alibou.ecommerce.entity.Payment;
import com.alibou.ecommerce.entity.PaymentMethod;
import com.alibou.ecommerce.mapper.PaymentMapper;
import com.alibou.ecommerce.notification.NotificationProducer;
import com.alibou.ecommerce.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository repository;

    @Mock
    private PaymentMapper mapper;

    @Mock
    private NotificationProducer notificationProducer;

    @InjectMocks
    private PaymentService paymentService;

    private PaymentRequest paymentRequest;
    private Payment payment;

    @BeforeEach
    void setUp() {
        paymentRequest = mock(PaymentRequest.class);
        payment = mock(Payment.class);
    }

    @Test
    void createPayment_ShouldSavePaymentAndSendNotification() {
        Customer customer = new Customer(
                "6ab643363d807c154c3f559a",
                "Michael",
                "Anderson",
                "michael.anderson@gmail.com"
        );

        when(paymentRequest.orderReference()).thenReturn("ORDER-001");
        when(paymentRequest.amount()).thenReturn(new BigDecimal("100.00"));
        when(paymentRequest.paymentMethod()).thenReturn(PaymentMethod.CREDIT_CARD);
        when(paymentRequest.customer()).thenReturn(customer);

        when(mapper.toPayment(paymentRequest)).thenReturn(payment);
        when(repository.save(payment)).thenReturn(payment);
        when(payment.getId()).thenReturn(1);

        Integer result = paymentService.createPayment(paymentRequest);

        assertEquals(1, result);

        verify(mapper).toPayment(paymentRequest);
        verify(repository).save(payment);

        verify(notificationProducer).sendNotification(
                argThat(notification ->
                        notification != null
                                && notification.orderReference().equals("ORDER-001")
                                && notification.amount().compareTo(new BigDecimal("100.00")) == 0
                                && notification.paymentMethod() == PaymentMethod.CREDIT_CARD
                                && notification.customerFirstName().equals("Michael")
                                && notification.customerLastName().equals("Anderson")
                                && notification.customerEmail().equals("michael.anderson@gmail.com")
                )
        );
    }
}
