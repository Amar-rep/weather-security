package com.example.guardian.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class WeatherResponse {
    private String city;
    private Double temperature;
    private Integer humidity;
    private Double windSpeed;
    private String condition;
}
