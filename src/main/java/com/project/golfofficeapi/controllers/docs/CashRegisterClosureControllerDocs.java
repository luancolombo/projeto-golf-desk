package com.project.golfofficeapi.controllers.docs;

import com.project.golfofficeapi.dto.CashRegisterClosureDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

public interface CashRegisterClosureControllerDocs {

    @Operation(summary = "Find All Cash Register Closures", description = "Finds all cash register closures.", tags = {"Cash Register Closures"}, responses = {
            @ApiResponse(description = "Success", responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = CashRegisterClosureDTO.class)))),
            @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
    })
    List<CashRegisterClosureDTO> findAll();

    @Operation(summary = "Find Cash Register Closure by ID", description = "Finds a specific cash register closure by ID.", tags = {"Cash Register Closures"}, responses = {
            @ApiResponse(description = "Success", responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = CashRegisterClosureDTO.class))),
            @ApiResponse(responseCode = "404", ref = "#/components/responses/NotFound"),
            @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
    })
    CashRegisterClosureDTO findById(@PathVariable("id") Long id);

    @Operation(summary = "Find Closure by Business Date", description = "Finds a cash register closure by business date.", tags = {"Cash Register Closures"}, responses = {
            @ApiResponse(description = "Success", responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = CashRegisterClosureDTO.class))),
            @ApiResponse(responseCode = "404", ref = "#/components/responses/NotFound"),
            @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
    })
    CashRegisterClosureDTO findByBusinessDate(@PathVariable("businessDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate businessDate);

    @Operation(summary = "Preview Cash Register Closure", description = "Calculates a non-persisted closing preview for a business date.", tags = {"Cash Register Closures"}, responses = {
            @ApiResponse(description = "Success", responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = CashRegisterClosureDTO.class))),
            @ApiResponse(responseCode = "400", ref = "#/components/responses/BadRequest"),
            @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
    })
    CashRegisterClosureDTO preview(@RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate businessDate);

    @Operation(summary = "Create Cash Register Closure", description = "Creates a cash register closure record manually.", tags = {"Cash Register Closures"}, responses = {
            @ApiResponse(description = "Success", responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = CashRegisterClosureDTO.class))),
            @ApiResponse(responseCode = "400", ref = "#/components/responses/BadRequest"),
            @ApiResponse(responseCode = "409", ref = "#/components/responses/Conflict"),
            @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
    })
    CashRegisterClosureDTO create(@Valid @RequestBody CashRegisterClosureDTO closure);

    @Operation(summary = "Close Cash Register", description = "Calculates, persists, and closes the cash register for a business date.", tags = {"Cash Register Closures"}, responses = {
            @ApiResponse(description = "Success", responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = CashRegisterClosureDTO.class))),
            @ApiResponse(responseCode = "400", ref = "#/components/responses/BadRequest"),
            @ApiResponse(responseCode = "409", ref = "#/components/responses/Conflict"),
            @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
    })
    CashRegisterClosureDTO close(@Valid @RequestBody CashRegisterClosureDTO closure);

    @Operation(summary = "Update Cash Register Closure", description = "Updates a cash register closure record.", tags = {"Cash Register Closures"}, responses = {
            @ApiResponse(description = "Success", responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = CashRegisterClosureDTO.class))),
            @ApiResponse(responseCode = "400", ref = "#/components/responses/BadRequest"),
            @ApiResponse(responseCode = "404", ref = "#/components/responses/NotFound"),
            @ApiResponse(responseCode = "409", ref = "#/components/responses/Conflict"),
            @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
    })
    CashRegisterClosureDTO update(@Valid @RequestBody CashRegisterClosureDTO closure);

    @Operation(summary = "Delete Cash Register Closure", description = "Deletes a cash register closure by ID when allowed by the current business rules.", tags = {"Cash Register Closures"}, responses = {
            @ApiResponse(responseCode = "204", ref = "#/components/responses/NoContent"),
            @ApiResponse(responseCode = "404", ref = "#/components/responses/NotFound"),
            @ApiResponse(responseCode = "409", ref = "#/components/responses/Conflict"),
            @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
    })
    ResponseEntity<?> delete(@PathVariable("id") Long id);
}
