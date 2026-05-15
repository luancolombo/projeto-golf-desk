package com.project.golfofficeapi.repository;

import com.project.golfofficeapi.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByTokenHash(String tokenHash);

    List<RefreshToken> findByUser_IdAndRevokedAtIsNull(Long userId);

    List<RefreshToken> findByExpiresAtBeforeAndRevokedAtIsNull(LocalDateTime dateTime);
}
