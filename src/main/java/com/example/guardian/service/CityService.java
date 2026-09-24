package com.example.guardian.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.guardian.dto.CityRequest;
import com.example.guardian.dto.CityResponse;
import com.example.guardian.dto.GeocodingResponse;
import com.example.guardian.entity.AuditAction;
import com.example.guardian.entity.City;
import com.example.guardian.entity.Member;
import com.example.guardian.exception.CityAlreadyExistException;
import com.example.guardian.exception.CityNotFoundException;
import com.example.guardian.repository.CityRepository;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class CityService {

	private final CityRepository cityRepository;
	private final GeoCodingService geoCodingService;
	private final AuditService auditService;

	@Transactional
	public List<CityResponse> addCity(CityRequest request, Member admin) {
		List<City> cityExist = cityRepository.findByNameIgnoreCaseAndStateIgnoreCaseAndCountryIgnoreCase(
				request.getName(), request.getState(), request.getCountry());

		if (!cityExist.isEmpty()) {
			log.warn("city already exists :"+request.getName());
			throw new CityAlreadyExistException("City already exists");
		}
		List<GeocodingResponse> geoList = geoCodingService.getCoordinates(request.getName(), request.getState(),
				request.getCountry());

		List<CityResponse> responseList = new ArrayList<>();
		GeocodingResponse geo = geoList.get(0);

		City city = City.builder().name(request.getName().trim().toLowerCase())
				.state(request.getState().trim().toLowerCase()).country(request.getCountry().trim())
				.latitude(geo.getLat()).longitude(geo.getLon()).createdBy(admin).createdAt(Instant.now()).build();

		City savedCity = cityRepository.save(city);

		auditService.logEvent(admin, AuditAction.CITY_ADDED, "Added city: " + savedCity.getName());

		CityResponse response = new CityResponse(savedCity.getId(), savedCity.getName(), savedCity.getState(),
				savedCity.getCountry(), savedCity.getLatitude(), savedCity.getLongitude());
		
		return List.of(response);

	}

	@Transactional
	public void removeCity(Long cityId, Member admin) {

		City city = cityRepository.findById(cityId).orElseThrow(() -> new CityNotFoundException("city not found"));
		cityRepository.deleteById(cityId);
		auditService.logEvent(admin, AuditAction.CITY_REMOVED, "Removed city: " + city.getName());

	};

	public List<CityResponse> getAllCities() {

		return cityRepository.findAll().stream().map(city -> new CityResponse(city.getId(), city.getName(),
				city.getState(), city.getCountry(), city.getLatitude(), city.getLongitude())).toList();
	}

	public CityResponse findByNameAndState(String name, String state) {

		City city = cityRepository.findByNameIgnoreCaseAndStateIgnoreCase(name, state)
				.orElseThrow(() -> new CityNotFoundException("unable to find City by name and state"));
		return new CityResponse(city.getId(), city.getName(), city.getState(), city.getCountry(), city.getLatitude(),
				city.getLongitude());
	}

}
