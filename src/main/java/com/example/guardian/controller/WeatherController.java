package com.example.guardian.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.guardian.dto.WeatherResponse;
import com.example.guardian.service.WeatherService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/weather")
@RequiredArgsConstructor
public class WeatherController {
	private final WeatherService weatherService;

	@GetMapping
	public ResponseEntity<WeatherResponse> getWeather(@RequestParam String name, @RequestParam String state,
			Authentication authentication) {
		WeatherResponse response = weatherService.getWeather(name, state, authentication);
		return ResponseEntity.ok(response);
	}
}
