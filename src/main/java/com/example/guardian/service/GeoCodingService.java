package com.example.guardian.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.guardian.client.GeoCodingGateway;
import com.example.guardian.dto.GeocodingResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GeoCodingService {

	private final GeoCodingGateway geoCodingGateway;

	public List<GeocodingResponse> getCoordinates(String city, String state, String country) {
		return geoCodingGateway.getGeoCoding(city, state, country);
	}
}
