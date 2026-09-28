package com.example.guardian.serviceTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Duration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import com.example.guardian.service.JwtService;

class JwtServiceTest {

	private JwtService jwtService;
	private UserDetails userDetails;

	@BeforeEach
	void setUp() {

		jwtService = new JwtService();
		ReflectionTestUtils.setField(jwtService, "secret",
				"VGhpc0lzQVN1ZmZpY2llbnRseUxvbmdTZWNyZXRLZXlGb3JKV1RUZXN0aW5nef");
		ReflectionTestUtils.setField(jwtService, "expiration", Duration.ofMinutes(10));
		userDetails = mock(UserDetails.class);
		when(userDetails.getUsername()).thenReturn("test@gmail.com");
	}

	@Test
	void shouldGenerateToken() {
		String token = jwtService.generateToken(userDetails);
		assertNotNull(token);
	}

	@Test
	void shouldExtractEmail() {
		String token = jwtService.generateToken(userDetails);
		String email = jwtService.extractEmail(token);
		assertEquals("test@gmail.com", email);
	}

	@Test
	void shouldValidateToken() {
		String token = jwtService.generateToken(userDetails);
		assertTrue(jwtService.isTokenValid(token, userDetails));
	}
}