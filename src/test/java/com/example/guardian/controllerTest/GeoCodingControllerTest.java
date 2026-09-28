package com.example.guardian.controllerTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.example.guardian.controller.GeoCodingController;
import com.example.guardian.dto.GeocodingResponse;
import com.example.guardian.service.GeoCodingService;

@ExtendWith(MockitoExtension.class)
class GeoCodingControllerTest {

	@Mock
	private GeoCodingService geoCodingService;

	@InjectMocks
	private GeoCodingController geoCodingController;

	@Test
	void getData_shouldReturn200() {
		ResponseEntity<List<GeocodingResponse>> response = geoCodingController.getData("Bangalore", "Karnataka",
				"India");
		assertEquals(HttpStatus.OK, response.getStatusCode());
		verify(geoCodingService).getCoordinates("Bangalore", "Karnataka", "India");
	}
}