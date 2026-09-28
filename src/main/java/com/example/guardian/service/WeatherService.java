package com.example.guardian.service;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.example.guardian.client.WeatherGateWay;
import com.example.guardian.dto.WeatherResponse;
import com.example.guardian.entity.AuditAction;
import com.example.guardian.entity.City;
import com.example.guardian.exception.CityNotFoundException;
import com.example.guardian.repository.CityRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class WeatherService {

	private final WeatherGateWay weatherGateWay;
	private final CityRepository cityRepository;
	private final AuditService auditService;

	public WeatherResponse getWeather(String name, String state, Authentication authentication) {

		City city = cityRepository.findByNameIgnoreCaseAndStateIgnoreCase(name, state)
				.orElseThrow(() -> new CityNotFoundException("City not configured"));
		auditService.logEventEmail(authentication.getName(), AuditAction.WEATHER_SEARCH, "weather search...");
		return weatherGateWay.getWeather(city.getLatitude(), city.getLongitude());
	}
}
