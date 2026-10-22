package com.alibou.customer.dto;

import com.alibou.customer.entity.Address;
import com.alibou.customer.entity.Role;

public record CustomerAuthResponse(
        String id,
        String firstName,
        String lastName,
        String email,
        Address address,
        Role role,
        String username
) {
}
