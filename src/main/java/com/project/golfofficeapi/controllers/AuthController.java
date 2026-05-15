package com.project.golfofficeapi.controllers;

import com.project.golfofficeapi.controllers.docs.AuthControllerDocs;
import com.project.golfofficeapi.dto.LoginRequestDTO;
import com.project.golfofficeapi.dto.LoginResponseDTO;
import com.project.golfofficeapi.dto.RefreshTokenRequestDTO;
import com.project.golfofficeapi.dto.RefreshTokenResponseDTO;
import com.project.golfofficeapi.services.AuthService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "Endpoints for user authentication and token issuing")
public class AuthController implements AuthControllerDocs {

    private final AuthService service;

    public AuthController(AuthService service) {
        this.service = service;
    }

    @PostMapping(
            value = "/login",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Override
    public LoginResponseDTO login(@Valid @RequestBody LoginRequestDTO request) {
        return service.login(request);
    }

    @PostMapping(
            value = "/refresh",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Override
    public RefreshTokenResponseDTO refresh(@Valid @RequestBody RefreshTokenRequestDTO request) {
        return service.refresh(request);
    }

    @PostMapping(
            value = "/logout",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    @Override
    public ResponseEntity<?> logout(@Valid @RequestBody RefreshTokenRequestDTO request) {
        service.logout(request);
        return ResponseEntity.noContent().build();
    }
}
