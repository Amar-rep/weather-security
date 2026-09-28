package com.example.guardian.repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.example.guardian.entity.RefreshToken;


public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

	Optional<RefreshToken> findByTokenHash(String tokenHash);

	void deleteById(Long id);
	List<RefreshToken> findByFamilyId(UUID familyId);
	
	@Modifying
	@Query("""
		    update RefreshToken r
		    set r.revoked = true,
		        r.revokedAt = :revokedAt
		    where r.familyId = :familyId
		""")
		void revokeFamily(
		        UUID familyId,
		        Instant revokedAt
		);
	
}
