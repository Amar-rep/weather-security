package com.example.guardian.exception;

import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.guardian.dto.ErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {
	@ExceptionHandler(MemberNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleMemberNotFound(MemberNotFoundException ex) {
		ErrorResponse response = new ErrorResponse(HttpStatus.CONFLICT.value(), ex.getMessage(), Instant.now());
		return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
	}

	@ExceptionHandler(CityAlreadyExistException.class)
	public ResponseEntity<ErrorResponse> handleCityExists(CityAlreadyExistException ex) {
		ErrorResponse response = new ErrorResponse(HttpStatus.CONFLICT.value(), ex.getMessage(), Instant.now());
		return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
	}
	
	@ExceptionHandler(CityNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleCityExists(CityNotFoundException ex) {
		ErrorResponse response = new ErrorResponse(HttpStatus.CONFLICT.value(), ex.getMessage(), Instant.now());
		return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
	}

	@ExceptionHandler(GeoCodingException.class)
	public ResponseEntity<ErrorResponse> handleGeoCoindException(GeoCodingException ex) {
		ErrorResponse response = new ErrorResponse(HttpStatus.BAD_GATEWAY.value(), ex.getMessage(), Instant.now());
		return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(response);
	}

	@ExceptionHandler(WeatherApiException.class)
	public ResponseEntity<ErrorResponse> handle(WeatherApiException ex) {
		ErrorResponse response = new ErrorResponse(HttpStatus.BAD_GATEWAY.value(), ex.getMessage(), Instant.now());
		return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(response);
	}
	
	
	@ExceptionHandler(InvalidCredentialsException.class)
	public ResponseEntity<ErrorResponse> handleInvalidCred(InvalidCredentialsException ex) {
		ErrorResponse response = new ErrorResponse(HttpStatus.UNAUTHORIZED.value(), ex.getMessage(), Instant.now());
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {

		String message = ex.getBindingResult().getFieldErrors().stream().findFirst().map(FieldError::getDefaultMessage)
				.orElse("Validation failed");

		return ResponseEntity.badRequest()
				.body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), message, Instant.now()));
	}
	
	@ExceptionHandler(RefreshTokenException.class)
	public ResponseEntity<ErrorResponse> handleRefreshTokenException(RefreshTokenException ex) {
		ErrorResponse response = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), Instant.now());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
	
	
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {

		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
				new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Something gone  wrong ", Instant.now()));
	}

}
