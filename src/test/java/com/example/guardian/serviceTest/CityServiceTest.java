package com.example.guardian.serviceTest;



import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.guardian.dto.CityRequest;
import com.example.guardian.dto.CityResponse;
import com.example.guardian.dto.GeocodingResponse;
import com.example.guardian.entity.AuditAction;
import com.example.guardian.entity.City;
import com.example.guardian.entity.Member;
import com.example.guardian.exception.CityAlreadyExistException;
import com.example.guardian.exception.CityNotFoundException;
import com.example.guardian.repository.CityRepository;
import com.example.guardian.service.AuditService;
import com.example.guardian.service.CityPersistenceService;
import com.example.guardian.service.CityService;
import com.example.guardian.service.GeoCodingService;

@ExtendWith(MockitoExtension.class)
class CityServiceTest {

	@Mock
	private CityRepository cityRepository;

	@Mock
	private GeoCodingService geoCodingService;

	@Mock
	private AuditService auditService;

	@Mock
	private CityPersistenceService cityPersistenceService;

	@InjectMocks
	private CityService cityService;

	@Test
	void addCity_success() {

		CityRequest request = new CityRequest();
		request.setName("Bakkalam");
		request.setState("Kerala");
		request.setCountry("IN");
		Member admin = new Member();
		GeocodingResponse geo = new GeocodingResponse();
		geo.setLat(new BigDecimal("11.98"));
		geo.setLon(new BigDecimal("75.35"));

		CityResponse response = new CityResponse(1L, "bakkalam", "kerala", "IN", geo.getLat(), geo.getLon());

		when(cityRepository.findByNameIgnoreCaseAndStateIgnoreCaseAndCountryIgnoreCase("Bakkalam", "Kerala", "IN"))
				.thenReturn(List.of());
		when(geoCodingService.getCoordinates("Bakkalam", "Kerala", "IN")).thenReturn(List.of(geo));
		when(cityPersistenceService.saveCity(request, admin, geo)).thenReturn(List.of(response));
		List<CityResponse> result = cityService.addCity(request, admin);
		assertEquals(1, result.size());
		assertEquals("bakkalam", result.get(0).getName());
		verify(cityPersistenceService).saveCity(request, admin, geo);
	}

	@Test
	void addCity_shouldThrow_whenCityExists() {

		CityRequest request = new CityRequest();
		request.setName("Bakkalam");
		request.setState("Kerala");
		request.setCountry("IN");
		when(cityRepository.findByNameIgnoreCaseAndStateIgnoreCaseAndCountryIgnoreCase("Bakkalam", "Kerala", "IN"))
				.thenReturn(List.of(new City()));
		assertThrows(CityAlreadyExistException.class, () -> cityService.addCity(request, new Member()));
		verify(geoCodingService, never()).getCoordinates(any(), any(), any());
		verify(cityPersistenceService, never()).saveCity(any(), any(), any());
	}

	@Test
	void removeCity_success() {
		Member admin = new Member();
		City city = new City();
		city.setId(1L);
		city.setName("bakkalam");
		when(cityRepository.findById(1L)).thenReturn(Optional.of(city));
		cityService.removeCity(1L, admin);
		verify(cityRepository).deleteById(1L);
		verify(auditService).logEvent(admin, AuditAction.CITY_REMOVED, "Removed city: bakkalam");
	}

	@Test
	void removeCity_shouldThrow_whenCityNotFound() {
		when(cityRepository.findById(1L)).thenReturn(Optional.empty());
		assertThrows(CityNotFoundException.class, () -> cityService.removeCity(1L, new Member()));
		verify(cityRepository, never()).deleteById(anyLong());
		verify(auditService, never()).logEvent(any(), any(), any());
	}

	@Test
	void getAllCities_success() {

		City city1 = new City();
		city1.setId(1L);
		city1.setName("bakkalam");
		city1.setState("kerala");
		city1.setCountry("IN");
		city1.setLatitude(new BigDecimal("11.98"));
		city1.setLongitude(new BigDecimal("75.35"));

		City city2 = new City();
		city2.setId(2L);
		city2.setName("kannur");
		city2.setState("kerala");
		city2.setCountry("IN");
		city2.setLatitude(new BigDecimal("11.87"));
		city2.setLongitude(new BigDecimal("75.37"));
		when(cityRepository.findAll()).thenReturn(List.of(city1, city2));
		List<CityResponse> result = cityService.getAllCities();
		assertEquals(2, result.size());
		assertEquals("bakkalam", result.get(0).getName());
		assertEquals("kannur", result.get(1).getName());
	}

	@Test
	void getAllCities_shouldReturnEmptyList() {

		when(cityRepository.findAll()).thenReturn(List.of());
		List<CityResponse> result = cityService.getAllCities();
		assertTrue(result.isEmpty());
	}

	@Test
	void findByNameAndState_success() {
		City city = new City();
		city.setId(1L);
		city.setName("bakkalam");
		city.setState("kerala");
		city.setCountry("IN");
		city.setLatitude(new BigDecimal("11.98"));
		city.setLongitude(new BigDecimal("75.35"));
		when(cityRepository.findByNameIgnoreCaseAndStateIgnoreCase("Bakkalam", "Kerala")).thenReturn(Optional.of(city));
		CityResponse result = cityService.findByNameAndState("Bakkalam", "Kerala");
		assertEquals(1L, result.getId());
		assertEquals("bakkalam", result.getName());
		assertEquals("kerala", result.getState());
	}

	@Test
	void findByNameAndState_shouldThrow_whenNotFound() {
		when(cityRepository.findByNameIgnoreCaseAndStateIgnoreCase("Unknown", "Kerala")).thenReturn(Optional.empty());
		assertThrows(CityNotFoundException.class, () -> cityService.findByNameAndState("Unknown", "Kerala"));
	}
}