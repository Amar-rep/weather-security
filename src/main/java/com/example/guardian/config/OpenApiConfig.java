package com.example.guardian.config;

import org.springframework.context.annotation.Bean;
import io.swagger.v3.oas.models.security.OAuthFlow;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.OAuthFlows;
@Configuration
public class OpenApiConfig {

	@Bean
	public OpenAPI openAPI() {

		String schemeName = "oauth2";

		return new OpenAPI().addSecurityItem(new SecurityRequirement().addList(schemeName)).components(
				new Components().addSecuritySchemes(schemeName, new SecurityScheme().type(SecurityScheme.Type.OAUTH2)
						.flows(new OAuthFlows().password(new OAuthFlow().tokenUrl("/auth/token")))));
	}
}