package com.alibou.customer.dto;

import com.alibou.customer.entity.Role;

public record CustomerLoginResponse(
        String username,
        String password,
        Role role
) {
}
