package com.alibou.ecommerce.kafka;

import com.alibou.ecommerce.customer.CustomerResponse;
import com.alibou.ecommerce.dto.PurchaseResponse;
import com.alibou.ecommerce.entity.PaymentMethod;
import java.math.BigDecimal;
import java.util.List;

public record OrderConfirmation(
        String orderReference,
        BigDecimal totalAmount,
        PaymentMethod paymentMethod,
        CustomerResponse customer,
        List<PurchaseResponse> products
) {
}
