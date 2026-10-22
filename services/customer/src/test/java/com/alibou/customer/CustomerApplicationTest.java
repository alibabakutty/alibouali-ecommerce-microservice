
package com.alibou.customer;

import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;

import static org.mockito.Mockito.mockStatic;

class CustomerApplicationTest {

	@Test
	void mainShouldRunSpringApplication() {
		try (var springApplication = mockStatic(SpringApplication.class)) {
			CustomerApplication.main(new String[]{});

			springApplication.verify(() ->
					SpringApplication.run(
							CustomerApplication.class,
							new String[]{}
					)
			);
		}
	}
}
