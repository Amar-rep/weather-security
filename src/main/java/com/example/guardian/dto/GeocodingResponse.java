package com.example.guardian.dto;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class GeocodingResponse {

    private String name;

    private BigDecimal lat;

    private BigDecimal lon;

    private String country;

    private String state;
}