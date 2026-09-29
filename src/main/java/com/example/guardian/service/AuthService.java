package com.example.guardian.service;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.guardian.dto.LoginRequest;
import com.example.guardian.dto.RegisterRequest;
import com.example.guardian.dto.TokenResponse;
import com.example.guardian.entity.AuditAction;
import com.example.guardian.entity.Member;
import com.example.guardian.entity.Role;
import com.example.guardian.exception.InvalidCredentialsException;
import com.example.guardian.exception.RegistrationException;
import com.example.guardian.repository.MemberRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
	private final AuthenticationManager authenticationManager;
	private final MemberRepository memberRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;
	private final RefreshTokenService refreshTokenService;
	private final AuditService auditService;

	@Value("${jwt.access-token-expiration}")
	private Duration expiration;
	@Value("${jwt.refresh-token-expiration}")
	private Duration expirationRefreshDuration;

	@Transactional
	public void register(RegisterRequest request) {
		if (memberRepository.existsByEmail(request.getEmail())) {
			throw new RegistrationException("Email already exists");
		}
		Member user = new Member();
		user.setEmail(request.getEmail());
		user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
		user.setRole(Role.valueOf(request.getRole()));
		user.setCreatedAt(Instant.now());
		user.setUpdatedAt(Instant.now());
		memberRepository.save(user);
		auditService.logEvent(user, AuditAction.USER_REGISTERED, "user registered");
	}

	public TokenResponse login(LoginRequest request) {
		try {
			Authentication authentication = authenticationManager
					.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

			UserDetails userDetails = (UserDetails) authentication.getPrincipal();
			Member user = memberRepository.findByEmail(userDetails.getUsername()).orElseThrow();
			String accessToken = jwtService.generateToken(userDetails);
			UUID familyId = UUID.randomUUID();
			String refreshToken = refreshTokenService.createToken(user, familyId);
			log.debug("User logged in user:" + request.getEmail());
			return new TokenResponse(accessToken, refreshToken, "Bearer", expiration.toMinutes(),
					user.getRole().name());
		} catch (BadCredentialsException e) {
			log.warn("Invalid credentials userData {}", request.getEmail());
			throw new InvalidCredentialsException("Invalid authentication info");
		}
	}

	public TokenResponse refresh(String oldRefreshToken) {
		RefreshTokenService.RefreshResult result = refreshTokenService.rotateToken(oldRefreshToken);
		Member user = result.user();
		UserDetails userDetails = User.withUsername(user.getEmail()).password(user.getPasswordHash())
				.roles(user.getRole().name()).build();
		String newAccessToken = jwtService.generateToken(userDetails);
		return new TokenResponse(newAccessToken, result.newRefreshToken(), "Bearer",
				expirationRefreshDuration.toMinutes(), user.getRole().name());
	}

	public void logout(String refreshToken) {
		refreshTokenService.revokeToken(refreshToken);
	}
}
