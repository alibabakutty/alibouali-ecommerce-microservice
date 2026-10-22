
package com.alibou.authentication.controller;

import com.alibou.authentication.dto.LoginRequest;
import com.alibou.authentication.security_utils.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthenticationControllerTest {

    private AuthenticationManager authenticationManager;
    private JwtUtils jwtUtils;
    private AuthenticationController controller;

    @BeforeEach
    void setUp() {
        authenticationManager = mock(AuthenticationManager.class);
        jwtUtils = mock(JwtUtils.class);
        controller = new AuthenticationController(
                authenticationManager,
                jwtUtils
        );
    }

    @Test
    void loginShouldReturnTokenWhenCredentialsAreValid() {
        LoginRequest request = new LoginRequest("alice", "password123");

        UserDetails userDetails = User.withUsername("alice")
                .password("encoded-password")
                .authorities("ROLE_USER")
                .build();

        Authentication authentication = mock(Authentication.class);

        when(authenticationManager.authenticate(
                any(UsernamePasswordAuthenticationToken.class)
        )).thenReturn(authentication);

        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(jwtUtils.generateToken(userDetails)).thenReturn("generated-jwt");

        var response = controller.login(request);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("generated-jwt", response.getBody().token());

        verify(authenticationManager).authenticate(
                argThat(authToken ->
                        "alice".equals(authToken.getPrincipal())
                                && "password123".equals(authToken.getCredentials())
                )
        );
        verify(jwtUtils).generateToken(userDetails);
    }

    @Test
    void loginShouldPropagateAuthenticationFailure() {
        LoginRequest request = new LoginRequest("alice", "wrong-password");

        when(authenticationManager.authenticate(
                any(UsernamePasswordAuthenticationToken.class)
        )).thenThrow(new BadCredentialsException("Bad credentials"));

        assertThrows(
                BadCredentialsException.class,
                () -> controller.login(request)
        );

        verify(authenticationManager).authenticate(
                any(UsernamePasswordAuthenticationToken.class)
        );
        verifyNoInteractions(jwtUtils);
    }


    @Test
    void loginShouldFailWhenAuthenticationPrincipalIsNull() {
        LoginRequest request = new LoginRequest("alice", "password123");

        Authentication authentication = mock(Authentication.class);

        when(authenticationManager.authenticate(
                any(UsernamePasswordAuthenticationToken.class)
        )).thenReturn(authentication);

        when(authentication.getPrincipal()).thenReturn(null);

        assertThrows(
                AssertionError.class,
                () -> controller.login(request)
        );

        verify(authenticationManager).authenticate(
                any(UsernamePasswordAuthenticationToken.class)
        );

        verifyNoInteractions(jwtUtils);
    }

}
