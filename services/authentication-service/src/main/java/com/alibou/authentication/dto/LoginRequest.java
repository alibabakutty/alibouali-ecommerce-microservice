package com.alibou.authentication.dto;

public record LoginRequest(
        String username,
        String password
) {
}
