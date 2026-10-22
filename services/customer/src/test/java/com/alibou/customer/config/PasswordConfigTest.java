
package com.alibou.customer.config;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import static org.junit.jupiter.api.Assertions.*;

class PasswordConfigTest {

    @Test
    void passwordEncoderShouldReturnBCryptPasswordEncoder() {
        PasswordConfig config = new PasswordConfig();

        PasswordEncoder encoder = config.passwordEncoder();

        assertNotNull(encoder);
        assertInstanceOf(BCryptPasswordEncoder.class, encoder);
    }

    @Test
    void passwordEncoderShouldEncodeAndVerifyPassword() {
        PasswordEncoder encoder = new PasswordConfig().passwordEncoder();

        String rawPassword = "TestPassword@123";
        String encodedPassword = encoder.encode(rawPassword);

        assertNotEquals(rawPassword, encodedPassword);
        assertTrue(encoder.matches(rawPassword, encodedPassword));
        assertFalse(encoder.matches("WrongPassword", encodedPassword));
    }
}
