package com.example.guardian.dto;

public record TokenResponse(

		String accessToken, String refreshToken, String tokenType, long expiresIn

) {
}
