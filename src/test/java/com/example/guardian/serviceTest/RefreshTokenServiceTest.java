package com.example.guardian.serviceTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;
import java.time.Instant;
import java.util.HexFormat;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.example.guardian.entity.Member;
import com.example.guardian.entity.RefreshToken;
import com.example.guardian.exception.RefreshTokenException;
import com.example.guardian.repository.RefreshTokenRepository;
import com.example.guardian.service.RefreshTokenService;
import com.example.guardian.service.RefreshTokenService.RefreshResult;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

	@Mock
	private RefreshTokenRepository refreshTokenRepository;

	@InjectMocks
	private RefreshTokenService refreshTokenService;

	@BeforeEach
	void setUp() {
		ReflectionTestUtils.setField(refreshTokenService, "refreshTokenExpiration", Duration.ofDays(7));
	}

	@Test
	void createToken_shouldCreateAndSaveToken() {
		Member member = new Member();
		UUID familyId = UUID.randomUUID();
		String rawToken = refreshTokenService.createToken(member, familyId);
		assertNotNull(rawToken);
		assertFalse(rawToken.isBlank());
		ArgumentCaptor<RefreshToken> captor = ArgumentCaptor.forClass(RefreshToken.class);
		verify(refreshTokenRepository).save(captor.capture());
		RefreshToken savedToken = captor.getValue();
		assertEquals(member, savedToken.getMember());
		assertEquals(familyId, savedToken.getFamilyId());
		assertFalse(savedToken.isRevoked());
		assertEquals(hash(rawToken), savedToken.getTokenHash());
	}

	@Test
	void rotateToken_shouldCreateNewToken() {

		String rawToken = "old-refresh-token";
		Member member = new Member();
		UUID familyId = UUID.randomUUID();
		RefreshToken oldToken = new RefreshToken();
		oldToken.setMember(member);
		oldToken.setFamilyId(familyId);
		oldToken.setRevoked(false);
		oldToken.setExpiresAt(Instant.now().plusSeconds(3600));
		when(refreshTokenRepository.findByTokenHash(hash(rawToken))).thenReturn(Optional.of(oldToken));
		RefreshResult result = refreshTokenService.rotateToken(rawToken);
		assertEquals(member, result.user());
		assertNotNull(result.newRefreshToken());
		assertFalse(result.newRefreshToken().isBlank());
		assertTrue(oldToken.isRevoked());
		verify(refreshTokenRepository, times(2)).save(any(RefreshToken.class));
	}

	@Test
	void rotateToken_shouldThrowException_whenTokenNotFound() {
		String rawToken = "invalid-token";
		when(refreshTokenRepository.findByTokenHash(hash(rawToken))).thenReturn(Optional.empty());
		assertThrows(RefreshTokenException.class, () -> refreshTokenService.rotateToken(rawToken));
	}

	@Test
	void rotateToken_shouldThrowException_whenTokenAlreadyRevoked() {
		String rawToken = "revoked-token";
		UUID familyId = UUID.randomUUID();
		RefreshToken oldToken = new RefreshToken();
		oldToken.setFamilyId(familyId);
		oldToken.setRevoked(true);
		when(refreshTokenRepository.findByTokenHash(hash(rawToken))).thenReturn(Optional.of(oldToken));
		assertThrows(RefreshTokenException.class, () -> refreshTokenService.rotateToken(rawToken));
		verify(refreshTokenRepository).revokeFamily(eq(familyId), any(Instant.class));
	}

	@Test
	void rotateToken_shouldThrowException_whenTokenExpired() {
		String rawToken = "expired-token";
		RefreshToken oldToken = new RefreshToken();
		oldToken.setRevoked(false);
		oldToken.setExpiresAt(Instant.now().minusSeconds(60));
		when(refreshTokenRepository.findByTokenHash(hash(rawToken))).thenReturn(Optional.of(oldToken));
		assertThrows(RefreshTokenException.class, () -> refreshTokenService.rotateToken(rawToken));
	}

	@Test
	void revokeToken_shouldRevokeToken() {

		String rawToken = "refresh-token";
		RefreshToken token = new RefreshToken();
		token.setRevoked(false);
		when(refreshTokenRepository.findByTokenHash(hash(rawToken))).thenReturn(Optional.of(token));
		refreshTokenService.revokeToken(rawToken);
		assertTrue(token.isRevoked());
		assertNotNull(token.getRevokedAt());

		verify(refreshTokenRepository).save(token);
	}

	@Test
	void revokeToken_shouldThrowException_whenTokenNotFound() {
		String rawToken = "invalid-token";
		when(refreshTokenRepository.findByTokenHash(hash(rawToken))).thenReturn(Optional.empty());
		assertThrows(RefreshTokenException.class, () -> refreshTokenService.revokeToken(rawToken));
	}

	private String hash(String token) {

		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hashed = digest.digest(token.getBytes(StandardCharsets.UTF_8));
			return HexFormat.of().formatHex(hashed);

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}