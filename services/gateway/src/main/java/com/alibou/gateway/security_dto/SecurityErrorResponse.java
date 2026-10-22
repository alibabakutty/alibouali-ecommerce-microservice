package com.alibou.gateway.security_dto;

public record SecurityErrorResponse(
        int status,
        String error,
        String message
) {
}
