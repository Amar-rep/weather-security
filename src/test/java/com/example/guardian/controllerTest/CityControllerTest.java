package com.example.guardian.controllerTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import com.example.guardian.controller.CityController;
import com.example.guardian.dto.CityRequest;
import com.example.guardian.dto.CityResponse;
import com.example.guardian.entity.Member;
import com.example.guardian.service.CityService;
import com.example.guardian.service.MemberService;

@ExtendWith(MockitoExtension.class)
class CityControllerTest {

	@Mock
	private CityService cityService;

	@Mock
	private MemberService memberService;

	@InjectMocks
	private CityController cityController;

	@Test
	void addCity_shouldReturn201() {
		CityRequest request = new CityRequest("Bangalore", "Karnataka", "country");
		Member admin = new Member();
		Authentication authentication = new UsernamePasswordAuthenticationToken("admin@gmail.com", null);
		when(memberService.findByEmail("admin@gmail.com")).thenReturn(admin);
		when(cityService.addCity(request, admin)).thenReturn(List.of());
		ResponseEntity<List<CityResponse>> response = cityController.addCity(request, authentication);
		assertEquals(HttpStatus.CREATED, response.getStatusCode());
		verify(memberService).findByEmail("admin@gmail.com");
		verify(cityService).addCity(request, admin);
	}

	@Test
	void findByNameAndState_shouldReturn200() {
		ResponseEntity<CityResponse> response = cityController.findByNameAndState("Bangalore", "Karnataka");
		assertEquals(HttpStatus.OK, response.getStatusCode());
		verify(cityService).findByNameAndState("Bangalore", "Karnataka");
	}

	@Test
	void getAllCities_shouldReturn200() {
		when(cityService.getAllCities()).thenReturn(List.of());
		ResponseEntity<List<CityResponse>> response = cityController.getAllCities();
		assertEquals(HttpStatus.OK, response.getStatusCode());
		verify(cityService).getAllCities();
	}

	@Test
	void deleteCity_shouldReturn204() {
		Member admin = new Member();
		Authentication authentication = new UsernamePasswordAuthenticationToken("admin@gmail.com", null);
		when(memberService.findByEmail("admin@gmail.com")).thenReturn(admin);
		ResponseEntity<Void> response = cityController.deleteCity(1L, authentication);
		assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
		verify(memberService).findByEmail("admin@gmail.com");
		verify(cityService).removeCity(1L, admin);
	}
}