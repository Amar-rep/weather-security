package com.example.guardian.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
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
