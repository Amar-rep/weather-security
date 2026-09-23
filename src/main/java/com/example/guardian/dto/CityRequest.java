package com.example.guardian.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CityRequest {

	@NotBlank(message = "name is required")
	@Size(max = 100)
	private String name;

	@NotBlank(message = "state is required")
	@Size(max = 100)
	private String state;

	@NotBlank(message = "country is required")
	@Size(max = 100)
	private String country;
}
