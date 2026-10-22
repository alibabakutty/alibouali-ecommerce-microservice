
package com.alibou.gateway;

import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;

@SpringBootTest
@ActiveProfiles("test")
class GatewayApplicationTests {

    @Test
    void contextLoads() {
        // Verifies that the Gateway application context starts successfully.
    }

    @Test
    void mainInvokesSpringApplicationRun() {
        try (var mockedSpringApplication =
                     mockStatic(SpringApplication.class)) {

            GatewayApplication.main(new String[]{});

            mockedSpringApplication.verify(
                    () -> SpringApplication.run(
                            GatewayApplication.class,
                            new String[]{}),
                    times(1)
            );
        }
    }
}