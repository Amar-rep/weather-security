package com.example.guardian.service;

import org.springframework.stereotype.Service;

import com.example.guardian.client.WeatherGateWay;
import com.example.guardian.dto.WeatherResponse;
import com.example.guardian.entity.City;
import com.example.guardian.repository.CityRepository;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WeatherService {

	private final WeatherGateWay weatherGateWay;
	private final CityRepository cityRepository;

	public WeatherResponse getWeather(String name, String state) {

		City city = cityRepository.findByNameIgnoreCaseAndStateIgnoreCase(name, state)
				.orElseThrow(() -> new RuntimeException("City not configured"));

		return weatherGateWay.getWeather(city.getLatitude(), city.getLongitude());
	}
}
