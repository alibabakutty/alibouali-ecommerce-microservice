
package com.alibou.authentication.controller;

import com.alibou.authentication.security_config.CsrfController;
import org.junit.jupiter.api.Test;
import org.springframework.security.web.csrf.CsrfToken;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CsrfControllerTest {


    @Test
    void shouldReturnCsrfTokenDetails() {
        CsrfController controller = new CsrfController();
        CsrfToken csrfToken = mock(CsrfToken.class);

        when(csrfToken.getToken()).thenReturn("test-csrf-token");
        when(csrfToken.getHeaderName()).thenReturn("X-XSRF-TOKEN");

        Map<String, String> result = controller.csrf(csrfToken);

        assertEquals(2, result.size());
        assertEquals("test-csrf-token", result.get("token"));
        assertEquals("X-XSRF-TOKEN", result.get("headerName"));
    }

}
