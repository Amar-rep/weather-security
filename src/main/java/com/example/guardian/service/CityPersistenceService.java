package com.example.guardian.service;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.guardian.dto.CityRequest;
import com.example.guardian.dto.CityResponse;
import com.example.guardian.dto.GeocodingResponse;
import com.example.guardian.entity.AuditAction;
import com.example.guardian.entity.City;
import com.example.guardian.entity.Member;
import com.example.guardian.repository.CityRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CityPersistenceService {

	private final CityRepository cityRepository;
	private final AuditService auditService;

	@Transactional
	public List<CityResponse> saveCity(CityRequest request, Member admin, GeocodingResponse geo) {

		City city = City.builder().name(request.getName().trim().toLowerCase())
				.state(request.getState().trim().toLowerCase()).country(request.getCountry().trim())
				.latitude(geo.getLat()).longitude(geo.getLon()).createdBy(admin).createdAt(Instant.now()).build();

		City savedCity = cityRepository.save(city);
		auditService.logEvent(admin, AuditAction.CITY_ADDED, "Added city: " + savedCity.getName());
		CityResponse response = new CityResponse(savedCity.getId(), savedCity.getName(), savedCity.getState(),
				savedCity.getCountry(), savedCity.getLatitude(), savedCity.getLongitude());
		return List.of(response);
	}
}
