package com.alibou.authentication.security_utils;

import com.alibou.authentication.client.CustomerClient;
import com.alibou.authentication.dto.CustomerLoginResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final CustomerClient customerClient;


    @Override
    public UserDetails loadUserByUsername(@NonNull String username)
            throws UsernameNotFoundException {

        final CustomerLoginResponse customer;

        try {
            customer = customerClient.getCustomerByUsername(username);
        } catch (Exception e) {
            throw new UsernameNotFoundException(
                    "Customer lookup failed for username: " + username, e
            );
        }

        if (customer == null) {
            throw new UsernameNotFoundException(
                    "User not found with username: " + username
            );
        }

        return User.builder()
                .username(customer.username())
                .password(customer.password())
                .roles(customer.role().name())
                .build();
    }

}