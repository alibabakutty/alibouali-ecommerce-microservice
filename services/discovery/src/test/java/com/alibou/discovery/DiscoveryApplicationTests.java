
package com.alibou.discovery;

import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;

@SpringBootTest
@ActiveProfiles("test")
class DiscoveryApplicationTests {

	@Test
	void contextLoads() {
		// Verifies that the Eureka application context starts.
	}

	@Test
	void mainInvokesSpringApplicationRun() {
		try (var mockedSpringApplication =
					 mockStatic(SpringApplication.class)) {

			DiscoveryApplication.main(new String[]{});

			mockedSpringApplication.verify(
					() -> SpringApplication.run(
							DiscoveryApplication.class,
							new String[]{}),
					times(1)
			);
		}
	}
}