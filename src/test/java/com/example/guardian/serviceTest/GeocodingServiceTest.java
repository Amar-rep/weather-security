package com.example.guardian.serviceTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.guardian.client.GeoCodingGateway;
import com.example.guardian.dto.GeocodingResponse;
import com.example.guardian.exception.GeoCodingException;
import com.example.guardian.exception.WeatherApiException;
import com.example.guardian.service.GeoCodingService;

@ExtendWith(MockitoExtension.class)
public class GeocodingServiceTest {
    @Mock
    private GeoCodingGateway geoCodingGateway;

    @InjectMocks
    private GeoCodingService geoCodingService;

    @Test
    public void getGeocoding_success() {
        GeocodingResponse response = new GeocodingResponse();
        response.setCountry("IN");
        response.setLat(new BigDecimal("12.43"));
        response.setLon(new BigDecimal("34.32"));
        response.setName("bakkalam");
        response.setState("kerala");
        List<GeocodingResponse> list = List.of(response);
        when(geoCodingService.getCoordinates("bakkalam", "kerala", "IN"))
                .thenReturn(list);
        List<GeocodingResponse> responses = geoCodingService.getCoordinates("bakkalam", "kerala", "IN");
        assertEquals(responses, list);
    }

    @Test
    public void geocoding_exception() {

        when(geoCodingService.getCoordinates("bakkalam", "kerala", "IN"))
                .thenThrow(new GeoCodingException("Unable to locate position"));
        assertThrows(GeoCodingException.class, () -> geoCodingService.getCoordinates("bakkalam", "kerala", "IN"));
    }

}
