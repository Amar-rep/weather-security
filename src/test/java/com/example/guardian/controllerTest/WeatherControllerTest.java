package com.example.guardian.controllerTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import com.example.guardian.controller.WeatherController;
import com.example.guardian.dto.WeatherResponse;
import com.example.guardian.service.WeatherService;

@ExtendWith(MockitoExtension.class)
class WeatherControllerTest {

	@Mock
	private WeatherService weatherService;

	@InjectMocks
	private WeatherController weatherController;

	@Test
	void getWeather_shouldReturn200() {
		Authentication authentication = new UsernamePasswordAuthenticationToken("test@gmail.com", null);
		ResponseEntity<WeatherResponse> response = weatherController.getWeather("Bangalore", "Karnataka",
				authentication);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		verify(weatherService).getWeather("Bangalore", "Karnataka", authentication);
	}
}