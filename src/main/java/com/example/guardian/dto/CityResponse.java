package com.example.guardian.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CityResponse {
	private Long id ;
	private String name;
	private String state;
	private String country;
	private BigDecimal latitude;
	private BigDecimal longitude;
}
