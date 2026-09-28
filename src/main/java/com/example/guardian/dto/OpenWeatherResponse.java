package com.example.guardian.dto;

import java.util.List;

import com.example.guardian.dto.WeatherDto.Main;
import com.example.guardian.dto.WeatherDto.Weather;
import com.example.guardian.dto.WeatherDto.Wind;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OpenWeatherResponse {

	private String name;
	private Main main;
	private Wind wind;
	private List<Weather> weather;


	

	
}


