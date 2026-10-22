
package com.alibou.authentication;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(
		webEnvironment = SpringBootTest.WebEnvironment.NONE,
		properties = {
				"spring.config.import=optional:configserver:",
				"spring.cloud.config.enabled=false",
				"eureka.client.enabled=false"
		}
)
@ActiveProfiles("test")
class AuthenticationApplicationTests {

	@Test
	void contextLoads() {
	}
}
