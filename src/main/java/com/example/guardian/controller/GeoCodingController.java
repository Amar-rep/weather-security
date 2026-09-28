package com.example.guardian.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.guardian.dto.GeocodingResponse;
import com.example.guardian.service.GeoCodingService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/geocoding")
@RequiredArgsConstructor
public class GeoCodingController {
	private final GeoCodingService geoCodingService;

	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping
	public ResponseEntity<List<GeocodingResponse>> getData(@RequestParam String city, @RequestParam String state,
			@RequestParam String country) {
		List<GeocodingResponse> response = geoCodingService.getCoordinates(city, state, country);
		return ResponseEntity.ok(response);
	}
}
