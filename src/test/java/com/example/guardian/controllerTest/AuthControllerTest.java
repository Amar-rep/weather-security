package com.example.guardian.controllerTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.example.guardian.controller.AuthController;
import com.example.guardian.dto.LoginRequest;
import com.example.guardian.dto.RefreshTokenRequest;
import com.example.guardian.dto.RegisterRequest;
import com.example.guardian.dto.TokenResponse;
import com.example.guardian.service.AuthService;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

	@Mock
	private AuthService authService;

	@InjectMocks
	private AuthController authController;

	@Test
	void register_shouldReturn201() {
		RegisterRequest request = new RegisterRequest("test@gmail.com", "password123", "ADMIN");
		ResponseEntity<Void> response = authController.register(request);
		assertEquals(HttpStatus.CREATED, response.getStatusCode());
		verify(authService).register(request);
	}

	@Test
	void login_shouldReturn200() {
		LoginRequest request = new LoginRequest("test@gmail.com", "password123");
		TokenResponse tokenResponse = new TokenResponse("access-token", "refresh-token", "bearer", 50);
		when(authService.login(request)).thenReturn(tokenResponse);
		ResponseEntity<TokenResponse> response = authController.login(request);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(tokenResponse, response.getBody());
		verify(authService).login(request);
	}

	@Test
	void refresh_shouldReturn200() {
		RefreshTokenRequest request = new RefreshTokenRequest("sample-refresh-token");
		TokenResponse tokenResponse = new TokenResponse("new-access-token", "new-refresh-token", "bearer", 123);
		when(authService.refresh("sample-refresh-token")).thenReturn(tokenResponse);
		ResponseEntity<TokenResponse> response = authController.refresh(request);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(tokenResponse, response.getBody());
		verify(authService).refresh("sample-refresh-token");
	}

	@Test
	void logout_shouldReturn204() {
		RefreshTokenRequest request = new RefreshTokenRequest("sample-refresh-token");
		ResponseEntity<Void> response = authController.logout(request);
		assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
		verify(authService).logout("sample-refresh-token");
	}
}