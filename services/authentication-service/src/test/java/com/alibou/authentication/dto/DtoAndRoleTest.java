
package com.alibou.authentication.dto;

import com.alibou.authentication.enums.Role;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DtoAndRoleTest {

    @Test
    void loginRequestShouldExposeUsernameAndPassword() {
        LoginRequest request = new LoginRequest("alice", "password123");

        assertEquals("alice", request.username());
        assertEquals("password123", request.password());
    }

    @Test
    void loginResponseShouldExposeToken() {
        LoginResponse response = new LoginResponse("test-jwt");

        assertEquals("test-jwt", response.token());
    }

    @Test
    void customerAuthResponseShouldExposeAllFields() {
        CustomerAuthResponse response = new CustomerAuthResponse(
                "customer-1",
                "alice@example.com",
                "alice",
                "test-password",
                Role.USER
        );

        assertEquals("customer-1", response.id());
        assertEquals("alice@example.com", response.email());
        assertEquals("alice", response.username());
        assertEquals("test-password", response.password());
        assertEquals(Role.USER, response.role());
    }

    @Test
    void roleShouldContainExpectedValues() {
        assertArrayEquals(
                new Role[]{Role.USER, Role.EMPLOYEE, Role.ADMIN},
                Role.values()
        );

        assertEquals(Role.USER, Role.valueOf("USER"));
        assertEquals(Role.EMPLOYEE, Role.valueOf("EMPLOYEE"));
        assertEquals(Role.ADMIN, Role.valueOf("ADMIN"));
    }
}
