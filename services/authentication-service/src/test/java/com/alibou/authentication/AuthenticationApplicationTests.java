
package com.alibou.authentication;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(
		webEnvironment = SpringBootTest.WebEnvironment.MOCK,
		properties = {
				"spring.config.import=optional:configserver:",
				"eureka.client.enabled=false"
		}
)
class AuthenticationApplicationTests {

	@Test
	void contextLoads() {
	}
}