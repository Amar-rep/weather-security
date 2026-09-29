package com.example.guardian.config;

import java.time.Instant;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.guardian.dto.ErrorResponse;
import com.example.guardian.security.JwtAuthenticationFilter;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

	private final JwtAuthenticationFilter jwtAuthenticationFilter;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationProvider authenticationProvider,
			ObjectMapper objectMapper) throws Exception {

		return http

				.csrf(csrf -> csrf.disable())

				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

				.authorizeHttpRequests(auth -> auth

						.requestMatchers("/auth/register", "/auth/login", "/auth/refresh", "/auth/logout",
								"/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html")
						.permitAll()

						.requestMatchers("/admin/**").hasRole("ADMIN")

						.anyRequest().authenticated())
				.exceptionHandling(
						exception -> exception.authenticationEntryPoint(authenticationEntryPoint(objectMapper))
								.accessDeniedHandler(accessDeniedHandler(objectMapper)))

				.authenticationProvider(authenticationProvider)
				.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class).build();
	}

	@Bean
	public AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService,
			PasswordEncoder passwordEncoder) {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
		provider.setPasswordEncoder(passwordEncoder);
		return provider;
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
		return configuration.getAuthenticationManager();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	AuthenticationEntryPoint authenticationEntryPoint(ObjectMapper objectMapper) {

		return (request, response, authException) -> {
			ErrorResponse errorResponse = new ErrorResponse(HttpServletResponse.SC_UNAUTHORIZED,
					"Authentication is required", Instant.now());
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.setContentType(MediaType.APPLICATION_JSON_VALUE);
			response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
		};
	}

	@Bean
	AccessDeniedHandler accessDeniedHandler(ObjectMapper objectMapper) {

		return (request, response, accessDeniedException) -> {
			ErrorResponse errorResponse = new ErrorResponse(HttpServletResponse.SC_FORBIDDEN,
					"You do not have permission to access this", Instant.now());
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			response.setContentType(MediaType.APPLICATION_JSON_VALUE);
			response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
		};
	}

	@Bean
	ObjectMapper objectMapper() {
		return new ObjectMapper().findAndRegisterModules();
	}
}
