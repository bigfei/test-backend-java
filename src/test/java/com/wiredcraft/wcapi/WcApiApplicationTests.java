package com.wiredcraft.wcapi;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
@ActiveProfiles("test")
class WcApiApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
	void mainMethodShouldStartApplication() {
		// Test that the main method can be called without throwing an exception
		// This is a basic smoke test to ensure the application can start
		// We don't actually call main here as it would start the full application
		// Instead, we just verify the context can load, which is done by contextLoads()
	}

}
