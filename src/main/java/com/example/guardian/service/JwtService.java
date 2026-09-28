package com.example.guardian.service;

import java.time.Duration;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {
	@Value("${jwt.secret}")
	private String secret;

	@Value("${jwt.access-token-expiration}")
	private Duration expiration;

	public String generateToken(UserDetails userDetails) {

		return Jwts.builder().subject(userDetails.getUsername()).issuedAt(new Date())
				.expiration(new Date(System.currentTimeMillis() + expiration.toMillis())).signWith(getKey()).compact();
	}

	public boolean isTokenValid(String token, UserDetails userDetails) {
		String username = extractEmail(token);
		return username.equals(userDetails.getUsername());
	}

	public String extractEmail(String token) {
		return getClaims(token).getSubject();
	}

	private Claims getClaims(String token) {
		return Jwts.parser().verifyWith(getKey()).build().parseSignedClaims(token).getPayload();
	}

	private SecretKey getKey() {
		byte[] keyBytes = Decoders.BASE64.decode(secret);
		return Keys.hmacShaKeyFor(keyBytes);
	}
}
