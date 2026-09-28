package com.example.guardian.config;

import java.time.Duration;

import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.benmanes.caffeine.cache.Caffeine;

@Configuration
public class CacheConfig {

	@Bean
	public Caffeine<Object, Object> caffeine() {
		return Caffeine.newBuilder().expireAfterWrite(Duration.ofMinutes(10)).maximumSize(1000);
	}

	@Bean
	public CacheManager cacheManager(Caffeine<Object, Object> caffeine) {

		CaffeineCacheManager cacheManager = new CaffeineCacheManager("weather");
		cacheManager.setCaffeine(caffeine);
		return cacheManager;
	}
}