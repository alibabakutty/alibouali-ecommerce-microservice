package com.alibou.customer.dto;

import java.util.Map;

public record ErrorResponse(
        Map<String, String> errors
) {
}
