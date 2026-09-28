package com.example.guardian.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.guardian.dto.CityRequest;
import com.example.guardian.dto.CityResponse;
import com.example.guardian.entity.Member;
import com.example.guardian.service.CityService;
import com.example.guardian.service.MemberService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/city")
@RequiredArgsConstructor
public class CityController {
	private final CityService cityService;
	private final MemberService memberService;

	@PostMapping
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<List<CityResponse>> addCity(@Valid @RequestBody CityRequest request,
			Authentication authentication) {
		String email = authentication.getName();
		Member admin = memberService.findByEmail(email);
		List<CityResponse> response = cityService.addCity(request, admin);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@GetMapping("/search")
	public ResponseEntity<CityResponse> findByNameAndState(@RequestParam String name, @RequestParam String state) {
		return ResponseEntity.ok(cityService.findByNameAndState(name, state));
	}

	@GetMapping
	public ResponseEntity<List<CityResponse>> getAllCities() {
		List<CityResponse> response = cityService.getAllCities();
		return ResponseEntity.ok(response);
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Void> deleteCity(@PathVariable Long id, Authentication authentication) {
		String email = authentication.getName();
		Member admin = memberService.findByEmail(email);
		cityService.removeCity(id, admin);
		return ResponseEntity.noContent().build();
	}

}
