package com.heartrate.repository;

import com.heartrate.entity.LoginAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.UUID;

@Repository
public interface LoginAttemptRepository extends JpaRepository<LoginAttempt, UUID> {
    @Query("SELECT COUNT(la) FROM LoginAttempt la WHERE la.email = :email AND la.attemptTime > :since")
    long countByEmailAndAttemptTimeAfter(String email, LocalDateTime since);

    @Query("SELECT COUNT(la) FROM LoginAttempt la WHERE la.ipAddress = :ipAddress AND la.attemptTime > :since")
    long countByIpAddressAndAttemptTimeAfter(String ipAddress, LocalDateTime since);
}
