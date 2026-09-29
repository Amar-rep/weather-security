package com.example.guardian.serviceTest;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;

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
import com.example.guardian.repository.CityRepository;
import com.example.guardian.service.AuditService;
import com.example.guardian.service.CityPersistenceService;

@ExtendWith(MockitoExtension.class)
class CityPersistenceServiceTest {

	@Mock
	private CityRepository cityRepository;

	@Mock
	private AuditService auditService;

	@InjectMocks
	private CityPersistenceService cityPersistenceService;

	@Test
	void saveCity_success() {

		CityRequest request = new CityRequest();
		request.setName("Bakkalam");
		request.setState("Kerala");
		request.setCountry("IN");

		Member admin = new Member();
		admin.setEmail("admin@gmail.com");

		GeocodingResponse geo = new GeocodingResponse();
		geo.setLat(new BigDecimal("11.98"));
		geo.setLon(new BigDecimal("75.35"));

	
		when(cityRepository.save(any(City.class))).thenAnswer(invocation -> {

			City city = invocation.getArgument(0);
			city.setId(1L);

			return city;
		});

		List<CityResponse> result = cityPersistenceService.saveCity(request, admin, geo);

		assertEquals(1, result.size());
		assertEquals("bakkalam", result.get(0).getName());

		verify(cityRepository).save(any(City.class));

		verify(auditService).logEvent(admin, AuditAction.CITY_ADDED, "Added city: bakkalam");
	}


}