package com.example.guardian.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OpenWeatherResponse {

    private String name;

    private Main main;

    private Wind wind;

    private List<Weather> weather;

    @Getter
    @Setter
    public static class Main {

        private Double temp;

        private Integer humidity;
    }

    @Getter
    @Setter
    public static class Wind {

        private Double speed;
    }

    @Getter
    @Setter
    public static class Weather {

        private String main;

        private String description;
    }
}