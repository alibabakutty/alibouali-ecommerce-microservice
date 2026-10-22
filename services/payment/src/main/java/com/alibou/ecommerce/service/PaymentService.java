package com.alibou.ecommerce.service;

import com.alibou.ecommerce.dto.PaymentRequest;
import com.alibou.ecommerce.mapper.PaymentMapper;
import com.alibou.ecommerce.notification.NotificationProducer;
import com.alibou.ecommerce.notification.PaymentConfirmation;
import com.alibou.ecommerce.repository.PaymentRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository repository;
    private final PaymentMapper mapper;
    private final NotificationProducer notificationProducer;

    public Integer createPayment(@Valid PaymentRequest request) {
        var payment = repository.save(mapper.toPayment(request));

        notificationProducer.sendNotification(
                new PaymentConfirmation(
                        request.orderReference(),
                        request.amount(),
                        request.paymentMethod(),
                        request.customer().firstName(),
                        request.customer().lastName(),
                        request.customer().email()
                )
        );
        return payment.getId();
    }
}
