package com.alibou.product.dto;

import java.util.Map;

public record ErrorResponse(
        Map<String, String> errors
) {
}
