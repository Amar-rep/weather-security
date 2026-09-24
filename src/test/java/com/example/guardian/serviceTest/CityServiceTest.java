package com.example.guardian.serviceTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
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
import com.example.guardian.exception.CityAlreadyExistException;
import com.example.guardian.repository.CityRepository;
import com.example.guardian.service.AuditService;
import com.example.guardian.service.CityService;
import com.example.guardian.service.GeoCodingService;

@ExtendWith(MockitoExtension.class)
public class CityServiceTest {
    @Mock
    private CityRepository cityRepository;

    @Mock
    private GeoCodingService geoCodingService;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private CityService cityService;

    @Test
    void addCity_success() {
        CityRequest request = new CityRequest();
        request.setName("Bakkalam");
        request.setState("Kerala");
        request.setCountry("IN");

        GeocodingResponse geo = new GeocodingResponse();
        geo.setLat(new BigDecimal("11.98"));
        geo.setLon(new BigDecimal("75.35"));

        when(cityRepository
                .findByNameIgnoreCaseAndStateIgnoreCaseAndCountryIgnoreCase(
                        "Bakkalam",
                        "Kerala",
                        "IN"))
                .thenReturn(List.of());
        Member admin = new Member();
        admin.setEmail("admin@gmail.com");
        when(geoCodingService.getCoordinates(
                "Bakkalam",
                "Kerala",
                "IN"))
                .thenReturn(List.of(geo));
        City savedCity = City.builder()
                .id(1L)
                .name("bakkalam")
                 .state("kerala")
                .country("IN")
                .latitude(new BigDecimal("11.98"))
                .longitude(new BigDecimal("75.35"))
                .createdBy(admin)
                .build();
        when(cityRepository.save(any(City.class)))
                .thenReturn(savedCity);
        List<CityResponse> result = cityService.addCity(request, admin);

        assertEquals(1, result.size());
        assertEquals("bakkalam", result.get(0).getName());

        verify(cityRepository).save(any(City.class));

        verify(auditService).logEvent(
                admin,
                AuditAction.CITY_ADDED,
                "Added city: bakkalam");
    }

    @Test
    void addCity_failure() {
        CityRequest request = new CityRequest();
        request.setName("Bakkalam");
        request.setState("Kerala");
        request.setCountry("IN");
        when(cityRepository
                .findByNameIgnoreCaseAndStateIgnoreCaseAndCountryIgnoreCase(
                        "Bakkalam",
                        "Kerala",
                        "IN"))
                .thenReturn(List.of(new City()));

        assertThrows(
                CityAlreadyExistException.class,
                () -> cityService.addCity(request, new Member()));

        verify(cityRepository, never())
                .save(any());
    }

}
