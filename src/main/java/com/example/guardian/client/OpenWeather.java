package com.example.guardian.client;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.example.guardian.dto.OpenWeatherResponse;
import com.example.guardian.dto.WeatherResponse;
import com.example.guardian.exception.WeatherApiException;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OpenWeather implements WeatherGateWay {

	private final RestClient openWeatherRestClient;
	@Value("${openweather.api.key}")
	private String apiKey;

	
   @Override
    public WeatherResponse getWeather(
            BigDecimal latitude,
            BigDecimal longitude) {

        OpenWeatherResponse response = openWeatherRestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/data/2.5/weather")
                        .queryParam("lat", latitude)
                        .queryParam("lon", longitude)
                        .queryParam("appid", apiKey)
                        .queryParam("units", "metric")
                        .build())
                .retrieve()
                .body(OpenWeatherResponse.class);

        if (response == null) {
            throw new WeatherApiException("Unable to fetch weather");
        }

        return new WeatherResponse(
                response.getName(),
                response.getMain().getTemp(),
                response.getMain().getHumidity(),
                response.getWind().getSpeed(),
                response.getWeather().get(0).getDescription()
        );
	
	
	

	}
}
