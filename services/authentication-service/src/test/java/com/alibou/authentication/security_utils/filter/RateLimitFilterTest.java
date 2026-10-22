
package com.alibou.authentication.security_utils.filter;

import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RateLimitFilterTest {

    private RateLimitFilter filter;

    @BeforeEach
    void setUp() {
        filter = new RateLimitFilter();
    }

    @Test
    void shouldContinueChainWhenRequestIsWithinLimit()
            throws Exception {

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
        assertNotEquals(429, response.getStatus());
    }

    @Test
    void shouldReturnTooManyRequestsWhenLimitIsExceeded()
            throws Exception {

        FilterChain chain = mock(FilterChain.class);
        MockHttpServletResponse lastResponse = null;

        // The bucket starts with 100 available tokens.
        for (int i = 0; i < 101; i++) {
            MockHttpServletRequest request =
                    new MockHttpServletRequest();
            MockHttpServletResponse response =
                    new MockHttpServletResponse();

            filter.doFilter(request, response, chain);

            if (i == 100) {
                lastResponse = response;
            }
        }

        assertNotNull(lastResponse);
        assertEquals(429, lastResponse.getStatus());
        assertEquals("text/plain", lastResponse.getContentType());
        assertEquals(
                "Too many requests. Please try again later.",
                lastResponse.getContentAsString()
        );

        verify(chain, times(100)).doFilter(
                any(jakarta.servlet.ServletRequest.class),
                any(jakarta.servlet.ServletResponse.class)
        );
    }
}
