package com.project.golfofficeapi.controllers.docs;

import com.project.golfofficeapi.dto.TeeTimeDTO;
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

import java.util.List;

public interface TeeTimeControllerDocs {

    @Operation(summary = "Find All Tee Times", description = "Finds all tee times.", tags = {"Tee Times"}, responses = {
            @ApiResponse(description = "Success", responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = TeeTimeDTO.class)))),
            @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
    })
    List<TeeTimeDTO> findAll();

    @Operation(summary = "Find Tee Time by ID", description = "Finds a specific tee time by ID.", tags = {"Tee Times"}, responses = {
            @ApiResponse(description = "Success", responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = TeeTimeDTO.class))),
            @ApiResponse(responseCode = "404", ref = "#/components/responses/NotFound"),
            @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
    })
    TeeTimeDTO findById(@PathVariable("id") Long id);

    @Operation(summary = "Create Tee Time", description = "Creates a tee time slot with play date, start time, capacity, status, and base green fee.", tags = {"Tee Times"}, responses = {
            @ApiResponse(description = "Success", responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = TeeTimeDTO.class))),
            @ApiResponse(responseCode = "400", ref = "#/components/responses/BadRequest"),
            @ApiResponse(responseCode = "409", ref = "#/components/responses/Conflict"),
            @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
    })
    TeeTimeDTO create(@Valid @RequestBody TeeTimeDTO teeTime);

    @Operation(summary = "Update Tee Time", description = "Updates an existing tee time.", tags = {"Tee Times"}, responses = {
            @ApiResponse(description = "Success", responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = TeeTimeDTO.class))),
            @ApiResponse(responseCode = "400", ref = "#/components/responses/BadRequest"),
            @ApiResponse(responseCode = "404", ref = "#/components/responses/NotFound"),
            @ApiResponse(responseCode = "409", ref = "#/components/responses/Conflict"),
            @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
    })
    TeeTimeDTO update(@Valid @RequestBody TeeTimeDTO teeTime);

    @Operation(summary = "Delete Tee Time", description = "Deletes a tee time by ID when allowed by the current business rules.", tags = {"Tee Times"}, responses = {
            @ApiResponse(responseCode = "204", ref = "#/components/responses/NoContent"),
            @ApiResponse(responseCode = "404", ref = "#/components/responses/NotFound"),
            @ApiResponse(responseCode = "409", ref = "#/components/responses/Conflict"),
            @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
    })
    ResponseEntity<?> delete(@PathVariable("id") Long id);
}
