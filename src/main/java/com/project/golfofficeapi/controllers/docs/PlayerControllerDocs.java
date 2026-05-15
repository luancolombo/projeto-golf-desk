package com.project.golfofficeapi.controllers.docs;

import com.project.golfofficeapi.dto.PlayerDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface PlayerControllerDocs {

    @Operation(
            summary = "Find All Players",
            description = "Finds all registered players.",
            tags = {"Players"},
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = PlayerDTO.class))
                            )
                    ),
                    @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
            }
    )
    List<PlayerDTO> findAll();

    @Operation(
            summary = "Find Player by ID",
            description = "Finds a specific player by ID.",
            tags = {"Players"},
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = PlayerDTO.class))
                    ),
                    @ApiResponse(responseCode = "404", ref = "#/components/responses/NotFound"),
                    @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
            }
    )
    PlayerDTO findById(
            @PathVariable("id") Long id
    );

    @Operation(
            summary = "Find Players by Name",
            description = "Finds registered players whose full name matches the informed search text.",
            tags = {"Players"},
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = PlayerDTO.class))
                            )
                    ),
                    @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
            }
    )
    List<PlayerDTO> findByName(
            @RequestParam("name") String name
    );

    @Operation(
            summary = "Create Player",
            description = "Creates a new player.",
            tags = {"Players"},
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = PlayerDTO.class))
                    ),
                    @ApiResponse(responseCode = "400", ref = "#/components/responses/BadRequest"),
                    @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
            }
    )
    PlayerDTO create(@Valid @RequestBody PlayerDTO player);

    @Operation(
            summary = "Update Player",
            description = "Updates an existing player.",
            tags = {"Players"},
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = PlayerDTO.class))
                    ),
                    @ApiResponse(responseCode = "400", ref = "#/components/responses/BadRequest"),
                    @ApiResponse(responseCode = "404", ref = "#/components/responses/NotFound"),
                    @ApiResponse(responseCode = "409", ref = "#/components/responses/Conflict"),
                    @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
            }
    )
    PlayerDTO update(@Valid @RequestBody PlayerDTO player);

    @Operation(
            summary = "Delete Player",
            description = "Deletes a player by ID.",
            tags = {"Players"},
            responses = {
                    @ApiResponse(responseCode = "204", ref = "#/components/responses/NoContent"),
                    @ApiResponse(responseCode = "404", ref = "#/components/responses/NotFound"),
                    @ApiResponse(responseCode = "409", ref = "#/components/responses/Conflict"),
                    @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
            }
    )
    ResponseEntity<?> delete(
            @PathVariable("id") Long id
    );
}
