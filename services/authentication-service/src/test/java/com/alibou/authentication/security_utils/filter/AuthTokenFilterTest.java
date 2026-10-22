
package com.alibou.authentication.security_utils.filter;

import com.alibou.authentication.security_utils.JwtUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthTokenFilterTest {

    private JwtUtils jwtUtils;
    private UserDetailsService userDetailsService;
    private AuthTokenFilter filter;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();

        jwtUtils = mock(JwtUtils.class);
        userDetailsService = mock(UserDetailsService.class);

        filter = new AuthTokenFilter(jwtUtils, userDetailsService);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldContinueFilterChainWhenAuthorizationHeaderIsMissing()
            throws Exception {

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        filter.doFilter(request, response, chain);

        assertNotNull(chain.getRequest());
        assertNull(
                SecurityContextHolder.getContext().getAuthentication()
        );

        verifyNoInteractions(jwtUtils, userDetailsService);
    }

    @Test
    void shouldIgnoreNonBearerAuthorizationHeader() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Basic abc123");

        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        filter.doFilter(request, response, chain);

        assertNotNull(chain.getRequest());
        assertNull(
                SecurityContextHolder.getContext().getAuthentication()
        );

        verifyNoInteractions(jwtUtils, userDetailsService);
    }

    @Test
    void shouldNotAuthenticateWhenJwtIsInvalid() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer invalid-token");

        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        when(jwtUtils.validateJwtToken("invalid-token"))
                .thenReturn(false);

        filter.doFilter(request, response, chain);

        assertNotNull(chain.getRequest());
        assertNull(
                SecurityContextHolder.getContext().getAuthentication()
        );

        verify(jwtUtils).validateJwtToken("invalid-token");
        verify(jwtUtils, never()).getUserNameFromJwtToken(anyString());
        verifyNoInteractions(userDetailsService);
    }

    @Test
    void shouldAuthenticateUserWhenJwtIsValid() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("127.0.0.1");
        request.addHeader("Authorization", "Bearer valid-token");

        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        UserDetails user = User.withUsername("alice")
                .password("unused")
                .authorities("ROLE_USER")
                .build();

        when(jwtUtils.validateJwtToken("valid-token"))
                .thenReturn(true);
        when(jwtUtils.getUserNameFromJwtToken("valid-token"))
                .thenReturn("alice");
        when(userDetailsService.loadUserByUsername("alice"))
                .thenReturn(user);

        filter.doFilter(request, response, chain);

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        assertNotNull(authentication);
        assertTrue(authentication.isAuthenticated());
        assertEquals("alice", authentication.getName());
        assertEquals(user, authentication.getPrincipal());
        assertNotNull(authentication.getDetails());
        assertNotNull(chain.getRequest());

        verify(jwtUtils).validateJwtToken("valid-token");
        verify(jwtUtils).getUserNameFromJwtToken("valid-token");
        verify(userDetailsService).loadUserByUsername("alice");
    }

    @Test
    void shouldContinueChainWhenLoadingUserThrowsException()
            throws Exception {

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer valid-token");

        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        when(jwtUtils.validateJwtToken("valid-token"))
                .thenReturn(true);
        when(jwtUtils.getUserNameFromJwtToken("valid-token"))
                .thenReturn("alice");
        when(userDetailsService.loadUserByUsername("alice"))
                .thenThrow(new RuntimeException("User not found"));

        filter.doFilter(request, response, chain);

        assertNotNull(chain.getRequest());
        assertNull(
                SecurityContextHolder.getContext().getAuthentication()
        );

        verify(userDetailsService).loadUserByUsername("alice");
    }


    @Test
    void shouldContinueChainWhenJwtValidationThrowsException()
            throws Exception {

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer problematic-token");

        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        when(jwtUtils.validateJwtToken("problematic-token"))
                .thenThrow(new IllegalArgumentException("Invalid token format"));

        assertDoesNotThrow(() -> filter.doFilter(request, response, chain));

        assertNotNull(chain.getRequest());
        assertNull(
                SecurityContextHolder.getContext().getAuthentication()
        );

        verify(jwtUtils).validateJwtToken("problematic-token");
        verifyNoInteractions(userDetailsService);
    }
}
