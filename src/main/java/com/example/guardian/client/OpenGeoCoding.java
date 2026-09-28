package com.example.guardian.client;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.example.guardian.dto.GeocodingResponse;
import com.example.guardian.exception.GeoCodingException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OpenGeoCoding implements GeoCodingGateway {
	private final RestClient openWeatherRestClient;
	@Value("${openweather.api.key}")
	private String apiKey;

	@Override
	public List<GeocodingResponse> getGeoCoding(String name, String state, String country) {

		String query = name + "," + country;
		GeocodingResponse[] response = openWeatherRestClient
				.get().uri(uriBuilder -> uriBuilder.path("/geo/1.0/direct").queryParam("q", query)
						.queryParam("limit", 5).queryParam("appid", apiKey).build())
				.retrieve().body(GeocodingResponse[].class);

		if (response == null || response.length == 0) {
			throw new GeoCodingException("Unable to locate position");
		}
		return Arrays.stream(response).filter(loc -> loc.getState() != null && loc.getState().equalsIgnoreCase(state))
				.toList();
	}
}
