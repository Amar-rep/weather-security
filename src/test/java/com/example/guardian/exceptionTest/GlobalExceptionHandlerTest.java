package com.example.guardian.exceptionTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import com.example.guardian.dto.ErrorResponse;
import com.example.guardian.exception.CityAlreadyExistException;
import com.example.guardian.exception.CityNotFoundException;
import com.example.guardian.exception.GeoCodingException;
import com.example.guardian.exception.GlobalExceptionHandler;
import com.example.guardian.exception.InvalidCredentialsException;
import com.example.guardian.exception.MemberNotFoundException;
import com.example.guardian.exception.RefreshTokenException;
import com.example.guardian.exception.RegistrationException;
import com.example.guardian.exception.WeatherApiException;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

	private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

	@Test
	void handleMemberNotFound_shouldReturn404() {
		MemberNotFoundException exception = new MemberNotFoundException("Member not found");
		ResponseEntity<ErrorResponse> response = handler.handleMemberNotFound(exception);
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
		assertEquals("Member not found", response.getBody().message());
	}

	@Test
	void handleCityAlreadyExists_shouldReturnConflict() {
		CityAlreadyExistException ex = new CityAlreadyExistException("City already exists");
		ResponseEntity<ErrorResponse> response = handler.handleCityExists(ex);
		assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals("City already exists", response.getBody().message());
	}

	@Test
	void handleCityNotFound_shouldReturnConflict() {
		CityNotFoundException ex = new CityNotFoundException("City not found");
		ResponseEntity<ErrorResponse> response = handler.handleCityExists(ex);
		assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals("City not found", response.getBody().message());
	}

	@Test
	void handleGeoCodingException_shouldReturnBadGateway() {
		GeoCodingException ex = new GeoCodingException("Geocoding API failed");
		ResponseEntity<ErrorResponse> response = handler.handleGeoCoindException(ex);
		assertEquals(HttpStatus.BAD_GATEWAY, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals("Geocoding API failed", response.getBody().message());
	}

	@Test
	void handleWeatherApiException_shouldReturnBadGateway() {
		WeatherApiException ex = new WeatherApiException("Weather API failed");
		ResponseEntity<ErrorResponse> response = handler.handle(ex);
		assertEquals(HttpStatus.BAD_GATEWAY, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals("Weather API failed", response.getBody().message());
	}

	@Test
	void handleInvalidCredentials_shouldReturnUnauthorized() {
		InvalidCredentialsException ex = new InvalidCredentialsException("Invalid credentials");
		ResponseEntity<ErrorResponse> response = handler.handleInvalidCred(ex);
		assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals("Invalid credentials", response.getBody().message());
	}

	@Test
	void handleRefreshTokenException_shouldReturnBadRequest() {
		RefreshTokenException ex = new RefreshTokenException("Invalid refresh token");
		ResponseEntity<ErrorResponse> response = handler.handleRefreshTokenException(ex);
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals("Invalid refresh token", response.getBody().message());
	}

	@Test
	void handleAccessDenied_shouldReturnForbidden() {
		AccessDeniedException ex = new AccessDeniedException("Denied");
		ResponseEntity<ErrorResponse> response = handler.handleAccessDenied(ex);
		assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals("Access Denied", response.getBody().message());
		assertEquals(403, response.getBody().status());
	}

	@Test
	void handleRegistrationException_shouldReturnConflict() {
		RegistrationException ex = new RegistrationException("Registration failed");
		ResponseEntity<ErrorResponse> response = handler.handleRegistrationException(ex);
		assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals("Registration failed", response.getBody().message());
	}

	@Test
	void handleGeneric_shouldReturnInternalServerError() {
		Exception ex = new Exception("Something went wrong");
		ResponseEntity<ErrorResponse> response = handler.handleGeneric(ex);
		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals("Something went wrong", response.getBody().message());
	}

	@Test
	void handleValidation_shouldReturnBadRequest() {
		MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
		BindingResult bindingResult = mock(BindingResult.class);
		FieldError fieldError = new FieldError("request", "email", "Email is invalid");
		when(ex.getBindingResult()).thenReturn(bindingResult);
		when(bindingResult.getFieldErrors()).thenReturn(java.util.List.of(fieldError));
		ResponseEntity<ErrorResponse> response = handler.handleValidation(ex);
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals("Email is invalid", response.getBody().message());
	}
}
