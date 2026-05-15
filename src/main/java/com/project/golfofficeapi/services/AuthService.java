package com.project.golfofficeapi.services;

import com.project.golfofficeapi.dto.LoginRequestDTO;
import com.project.golfofficeapi.dto.LoginResponseDTO;
import com.project.golfofficeapi.dto.RefreshTokenRequestDTO;
import com.project.golfofficeapi.dto.RefreshTokenResponseDTO;
import com.project.golfofficeapi.model.RefreshToken;
import com.project.golfofficeapi.model.User;
import com.project.golfofficeapi.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.logging.Logger;

@Service
public class AuthService {

    private static final String TOKEN_TYPE = "Bearer";

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final Logger logger = Logger.getLogger(AuthService.class.getName());

    public AuthService(
            AuthenticationManager authenticationManager,
            UserService userService,
            JwtService jwtService,
            RefreshTokenService refreshTokenService
    ) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
    }

    public LoginResponseDTO login(LoginRequestDTO request) {
        logger.info("Login User");

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
        } catch (AuthenticationException exception) {
            throw new BadCredentialsException("Invalid email or password");
        }

        User user = userService.findActiveEntityByEmail(request.getEmail());
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = refreshTokenService.createRefreshToken(user);

        LoginResponseDTO response = new LoginResponseDTO();
        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshToken);
        response.setTokenType(TOKEN_TYPE);
        response.setExpiresIn(jwtService.getAccessTokenExpirationSeconds());
        response.setUserId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole().name());
        return response;
    }

    @Transactional
    public RefreshTokenResponseDTO refresh(RefreshTokenRequestDTO request) {
        logger.info("Refresh User Session");

        RefreshToken currentRefreshToken = refreshTokenService.validateRefreshToken(request.getRefreshToken());
        User user = currentRefreshToken.getUser();

        if (user == null || Boolean.FALSE.equals(user.getActive())) {
            throw new BadCredentialsException("User is inactive or unavailable");
        }

        refreshTokenService.revokeRefreshToken(request.getRefreshToken());

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = refreshTokenService.createRefreshToken(user);

        RefreshTokenResponseDTO response = new RefreshTokenResponseDTO();
        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshToken);
        response.setTokenType(TOKEN_TYPE);
        response.setExpiresIn(jwtService.getAccessTokenExpirationSeconds());
        return response;
    }

    @Transactional
    public void logout(RefreshTokenRequestDTO request) {
        logger.info("Logout User Session");
        refreshTokenService.revokeRefreshToken(request.getRefreshToken());
    }
}
