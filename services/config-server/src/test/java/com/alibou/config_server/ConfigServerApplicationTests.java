package com.alibou.config_server;

import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;

@SpringBootTest
class ConfigServerApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
	void mainInvokesSpringApplicationRun() {
		try (var springApplication = mockStatic(SpringApplication.class)) {
			ConfigServerApplication.main(new String[]{});

			springApplication.verify(
					() -> SpringApplication.run(
							ConfigServerApplication.class,
							new String[]{}),
					times(1)
			);
		}
	}
}