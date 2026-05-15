package com.project.golfofficeapi.controllers.docs;

import com.project.golfofficeapi.dto.LoginRequestDTO;
import com.project.golfofficeapi.dto.LoginResponseDTO;
import com.project.golfofficeapi.dto.RefreshTokenRequestDTO;
import com.project.golfofficeapi.dto.RefreshTokenResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

public interface AuthControllerDocs {

    @Operation(
            summary = "Login",
            description = "Authenticates an active system user by email and password, returning a short-lived JWT access token.",
            tags = {"Authentication"},
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = LoginResponseDTO.class))
                    ),
                    @ApiResponse(responseCode = "400", ref = "#/components/responses/BadRequest"),
                    @ApiResponse(responseCode = "401", ref = "#/components/responses/Unauthorized"),
                    @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
            }
    )
    LoginResponseDTO login(@Valid @RequestBody LoginRequestDTO request);

    @Operation(
            summary = "Refresh access token",
            description = "Validates an active refresh token, revokes it, and returns a new short-lived access token with a new refresh token.",
            tags = {"Authentication"},
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = RefreshTokenResponseDTO.class))
                    ),
                    @ApiResponse(responseCode = "400", ref = "#/components/responses/BadRequest"),
                    @ApiResponse(responseCode = "401", ref = "#/components/responses/Unauthorized"),
                    @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
            }
    )
    RefreshTokenResponseDTO refresh(@Valid @RequestBody RefreshTokenRequestDTO request);

    @Operation(
            summary = "Logout",
            description = "Revokes the provided refresh token, ending that user session.",
            tags = {"Authentication"},
            responses = {
                    @ApiResponse(responseCode = "204", description = "Session revoked"),
                    @ApiResponse(responseCode = "400", ref = "#/components/responses/BadRequest"),
                    @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
            }
    )
    ResponseEntity<?> logout(@Valid @RequestBody RefreshTokenRequestDTO request);
}
