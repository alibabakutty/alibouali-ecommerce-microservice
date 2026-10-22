package com.alibou.authentication.security_utils;

import com.alibou.authentication.client.CustomerClient;
import com.alibou.authentication.dto.CustomerLoginResponse;
import com.alibou.authentication.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserDetailsServiceImplTest {

    private CustomerClient customerClient;
    private UserDetailsServiceImpl service;

    @BeforeEach
    void setUp() {
        customerClient = mock(CustomerClient.class);
        service = new UserDetailsServiceImpl(customerClient);
    }

    @Test
    void shouldLoadUserWhenCustomerExists() {
        CustomerLoginResponse customer = new CustomerLoginResponse(
                "alice",
                "encoded-password",
                Role.USER
        );

        when(customerClient.getCustomerByUsername("alice"))
                .thenReturn(customer);

        UserDetails result = service.loadUserByUsername("alice");

        assertEquals("alice", result.getUsername());
        assertEquals("encoded-password", result.getPassword());
        assertTrue(result.getAuthorities().stream()
                .anyMatch(authority ->
                        authority.getAuthority().equals("ROLE_USER")));

        verify(customerClient).getCustomerByUsername("alice");
    }

    @Test
    void shouldMapAdminRoleCorrectly() {
        CustomerLoginResponse customer = new CustomerLoginResponse(
                "admin",
                "encoded-admin-password",
                Role.ADMIN
        );

        when(customerClient.getCustomerByUsername("admin"))
                .thenReturn(customer);

        UserDetails result = service.loadUserByUsername("admin");

        assertEquals("admin", result.getUsername());
        assertTrue(result.getAuthorities().stream()
                .anyMatch(authority ->
                        authority.getAuthority().equals("ROLE_ADMIN")));
    }

    @Test
    void shouldThrowUsernameNotFoundWhenCustomerClientFails() {
        when(customerClient.getCustomerByUsername("missing"))
                .thenThrow(new RuntimeException(
                        "Customer service unavailable"
                ));

        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> service.loadUserByUsername("missing")
        );

        assertEquals(
                "Customer lookup failed for username: missing",
                exception.getMessage()
        );

        verify(customerClient).getCustomerByUsername("missing");
    }

    @Test
    void shouldThrowUsernameNotFoundWhenCustomerIsNull() {
        when(customerClient.getCustomerByUsername("missing"))
                .thenReturn(null);

        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> service.loadUserByUsername("missing")
        );

        assertEquals(
                "User not found with username: missing",
                exception.getMessage()
        );

        verify(customerClient).getCustomerByUsername("missing");
    }
}