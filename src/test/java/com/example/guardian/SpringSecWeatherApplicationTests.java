package com.example.guardian;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
        "API_KEY=test-api-key"
})
class SpringSecWeatherApplicationTests {

	@Test
	void contextLoads() {
	}

}
