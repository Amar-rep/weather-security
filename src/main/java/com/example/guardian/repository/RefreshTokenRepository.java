package com.example.guardian.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.guardian.entity.RefreshToken;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

	Optional<RefreshToken> findByTokenHash(String tokenHash);

	void deleteById(Long id);
}
