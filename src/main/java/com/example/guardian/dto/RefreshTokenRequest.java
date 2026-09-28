package com.example.guardian.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RefreshTokenRequest {
	@NotBlank(message = "token not found")
	private String refreshToken;
}
