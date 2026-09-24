package com.example.guardian;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class SpringSecWeatherApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringSecWeatherApplication.class, args);
	}

}
