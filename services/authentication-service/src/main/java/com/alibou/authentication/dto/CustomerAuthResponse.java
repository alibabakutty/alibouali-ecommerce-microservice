package com.alibou.authentication.dto;

import com.alibou.authentication.enums.Role;

public record CustomerAuthResponse(
        String id,
        String email,
        String username,
        String password,
        Role role
) {
}
