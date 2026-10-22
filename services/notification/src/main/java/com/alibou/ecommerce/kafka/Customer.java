package com.alibou.ecommerce.kafka;

public record Customer(
        String id,
        String firstName,
        String lastName,
        String email
) {
}
