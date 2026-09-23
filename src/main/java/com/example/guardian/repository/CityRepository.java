package com.example.guardian.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.guardian.entity.City;

public interface CityRepository extends JpaRepository<City, Long> {
	List<City> findByNameIgnoreCaseAndStateIgnoreCaseAndCountryIgnoreCase(
	        String name,
	        String state,
	        String country
	);
	
	Optional<City> findByNameIgnoreCaseAndStateIgnoreCase(String name,String state);
}
