package com.example.guardian.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.guardian.entity.Member;
import com.example.guardian.entity.RefreshToken;
import com.example.guardian.exception.RefreshTokenException;
import com.example.guardian.repository.RefreshTokenRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    private final SecureRandom secureRandom = new SecureRandom();

    @Value("${jwt.refresh-token-expiration}")
    private Duration refreshTokenExpiration;

    public String createToken(Member user, UUID familyId) {

        String rawToken = generateToken();

        RefreshToken refreshToken = new RefreshToken();

        refreshToken.setTokenHash(hash(rawToken));

        refreshToken.setMember(user);
        refreshToken.setFamilyId(familyId);
        refreshToken.setExpiresAt(Instant.now().plus(refreshTokenExpiration));
        refreshToken.setCreatedAt(Instant.now());
        refreshToken.setRevoked(false);

        refreshTokenRepository.save(refreshToken);

        return rawToken;
    }

    @Transactional
    public RefreshResult rotateToken(String rawToken) {

        String tokenHash = hash(rawToken);

        RefreshToken oldToken = refreshTokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        if (oldToken.isRevoked()) {
            throw new RefreshTokenException("Refresh token already revoked");
        }

        if (oldToken.getExpiresAt().isBefore(Instant.now())) {

            throw new RefreshTokenException("Refresh token expired");
        }

        // R1 becomes invalid
        oldToken.setRevoked(true);
        UUID familyId = oldToken.getFamilyId();
        refreshTokenRepository.save(oldToken);

        String newRefreshToken = createToken(oldToken.getMember(), familyId);

        return new RefreshResult(oldToken.getMember(), newRefreshToken);
    }

    @Transactional
    public void revokeToken(String rawToken) {

        String tokenHash = hash(rawToken);

        RefreshToken token = refreshTokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> new RefreshTokenException("Invalid refresh token"));
        
        token.setRevoked(true);

        refreshTokenRepository.save(token);
    }

    private String generateToken() {

        byte[] bytes = new byte[32];

        secureRandom.nextBytes(bytes);

        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String hash(String token) {

        try {

            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));

            return HexFormat.of().formatHex(hash);

        } catch (Exception e) {

            throw new RefreshTokenException(e.getMessage());
        }
    }

    public record RefreshResult(Member user, String newRefreshToken) {
    }
}
