package com.example.guardian.serviceTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.net.Authenticator;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import com.example.guardian.client.WeatherGateWay;
import com.example.guardian.dto.WeatherResponse;
import com.example.guardian.entity.AuditAction;
import com.example.guardian.entity.City;
import com.example.guardian.exception.WeatherApiException;
import com.example.guardian.repository.CityRepository;
import com.example.guardian.service.AuditService;
import com.example.guardian.service.WeatherService;

@ExtendWith(MockitoExtension.class)
public class WeatherServiceTest {
    @Mock
    private WeatherGateWay weatherGateWay;

    @Mock
    private CityRepository cityRepository;

    @Mock
    private AuditService auditService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private WeatherService weatherService;

    @Test
    public void getWeatherSuccess() {
        City city = new City();
        city.setName("bakkalam");
        city.setState("Kerala");
        city.setLatitude(new BigDecimal("11.43"));
        city.setLongitude(new BigDecimal("75.54"));

        WeatherResponse response = new WeatherResponse("bakkalam", 34.23, 45, 23.234, "cloundy");

        when(cityRepository
                .findByNameIgnoreCaseAndStateIgnoreCase(
                        "bakkalam",
                        "kerala"))
                .thenReturn(Optional.of(city));
        when(authentication.getName()).thenReturn("user@gmail.com");
        when(weatherGateWay.getWeather(city.getLatitude(), city.getLongitude())).thenReturn(response);

        WeatherResponse result = weatherService.getWeather("bakkalam", "kerala", authentication);

        assertEquals(result, response);
        verify(auditService).logEventEmail("user@gmail.com", AuditAction.WEATHER_SEARCH, "weather search...");
        verify(weatherGateWay).getWeather(
                city.getLatitude(),
                city.getLongitude());
    }

    @Test
    void getWeather_apiError() {
        City city = new City();
        city.setName("bakkalam");
        city.setLatitude(new BigDecimal("11.23"));
        city.setLongitude(new BigDecimal("13.43"));

        when(cityRepository.findByNameIgnoreCaseAndStateIgnoreCase("bakkalam", "kerala")).thenReturn(Optional.of(city));

        when(authentication.getName())
                .thenReturn("user@gmail.com");
        when(weatherGateWay.getWeather(city.getLatitude(), city.getLongitude()))
                .thenThrow(new WeatherApiException("Unable to fetch weather"));
        assertThrows(WeatherApiException.class, () -> weatherService.getWeather("bakkalam", "kerala", authentication));

    }

}
