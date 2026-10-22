
package com.alibou.authentication;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;
import static org.mockito.Mockito.mockStatic;

class AuthenticationApplicationMainTest {

    @Test
    void mainShouldRunSpringApplication() {
        String[] args = {};

        try (MockedStatic<SpringApplication> mocked =
                     mockStatic(SpringApplication.class)) {

            AuthenticationApplication.main(args);

            mocked.verify(() ->
                    SpringApplication.run(AuthenticationApplication.class, args)
            );
        }
    }
}
