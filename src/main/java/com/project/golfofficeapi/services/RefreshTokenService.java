package com.project.golfofficeapi.services;

import com.project.golfofficeapi.model.RefreshToken;
import com.project.golfofficeapi.model.User;
import com.project.golfofficeapi.repository.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HexFormat;
import java.util.List;
import java.util.logging.Logger;

@Service
public class RefreshTokenService {

    private static final int TOKEN_BYTES = 64;
    private static final String HASH_ALGORITHM = "SHA-256";

    private final RefreshTokenRepository repository;
    private final SecureRandom secureRandom = new SecureRandom();
    private final long refreshTokenExpirationHours;
    private final Logger logger = Logger.getLogger(RefreshTokenService.class.getName());

    public RefreshTokenService(
            RefreshTokenRepository repository,
            @Value("${security.jwt.refresh-token-expiration-hours}") long refreshTokenExpirationHours
    ) {
        this.repository = repository;
        this.refreshTokenExpirationHours = refreshTokenExpirationHours;
    }

    @Transactional
    public String createRefreshToken(User user) {
        logger.info("Create Refresh Token");
        String rawToken = generateRawToken();
        LocalDateTime now = LocalDateTime.now();

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setTokenHash(hashToken(rawToken));
        refreshToken.setIssuedAt(now);
        refreshToken.setExpiresAt(now.plusHours(refreshTokenExpirationHours));
        repository.save(refreshToken);

        return rawToken;
    }

    @Transactional(readOnly = true)
    public RefreshToken validateRefreshToken(String rawToken) {
        logger.info("Validate Refresh Token");
        RefreshToken refreshToken = repository.findByTokenHash(hashToken(rawToken))
                .orElseThrow(() -> new BadCredentialsException("Invalid refresh token"));

        if (!refreshToken.isActive()) {
            throw new BadCredentialsException("Refresh token expired or revoked");
        }

        return refreshToken;
    }

    @Transactional
    public void revokeRefreshToken(String rawToken) {
        logger.info("Revoke Refresh Token");
        repository.findByTokenHash(hashToken(rawToken))
                .ifPresent(this::revoke);
    }

    @Transactional
    public void revokeAllByUserId(Long userId) {
        logger.info("Revoke All Refresh Tokens by User ID");
        List<RefreshToken> activeTokens = repository.findByUser_IdAndRevokedAtIsNull(userId);
        activeTokens.forEach(this::revoke);
    }

    @Transactional
    public void revokeExpiredTokens() {
        logger.info("Revoke Expired Refresh Tokens");
        List<RefreshToken> expiredTokens = repository.findByExpiresAtBeforeAndRevokedAtIsNull(LocalDateTime.now());
        expiredTokens.forEach(this::revoke);
    }

    private void revoke(RefreshToken refreshToken) {
        if (refreshToken.getRevokedAt() == null) {
            refreshToken.setRevokedAt(LocalDateTime.now());
            repository.save(refreshToken);
        }
    }

    private String generateRawToken() {
        byte[] tokenBytes = new byte[TOKEN_BYTES];
        secureRandom.nextBytes(tokenBytes);
        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(tokenBytes);
    }

    private String hashToken(String rawToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
            byte[] hash = digest.digest(rawToken.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("Refresh token hash algorithm is not available", exception);
        }
    }
}
