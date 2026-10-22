
package com.alibou.authentication.dto;

import com.alibou.authentication.enums.Role;

public record CustomerLoginResponse(
        String username,
        String password,
        Role role
) {
}
