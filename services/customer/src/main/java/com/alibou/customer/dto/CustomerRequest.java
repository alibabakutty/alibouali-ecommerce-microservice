package com.alibou.customer.dto;

import com.alibou.customer.entity.Address;
import com.alibou.customer.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record CustomerRequest(
        String id,
        @NotNull(message = "Customer firstname is required")
        String firstName,
        @NotNull(message = "Customer lastname is required")
        String lastName,
        @NotNull(message = "Customer email is required")
        @Email(message = "Customer email is not a valid email address")
        String email,
        Address address,
        Role role,
        @NotNull(message = "Customer username is required")
        String username,
        @NotNull(message = "Customer password is required")
        String password
) {
}
