package com.heartrate.repository;

import com.heartrate.entity.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, UUID> {
    Optional<VerificationToken> findByTokenAndType(String token, String type);
    Optional<VerificationToken> findFirstByUserIdAndTypeOrderByCreatedAtDesc(UUID userId, String type);
}
