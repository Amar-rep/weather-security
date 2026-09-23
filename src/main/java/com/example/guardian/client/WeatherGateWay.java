package com.example.guardian.client;

import java.math.BigDecimal;

import com.example.guardian.dto.WeatherResponse;

public interface WeatherGateWay {
	WeatherResponse getWeather(BigDecimal lat,BigDecimal lon);
}
