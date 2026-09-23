package com.example.guardian.client;

import java.util.List;

import com.example.guardian.dto.GeocodingResponse;

public interface GeoCodingGateway {
	List<GeocodingResponse> getGeoCoding(String name,String state,String country);
}
