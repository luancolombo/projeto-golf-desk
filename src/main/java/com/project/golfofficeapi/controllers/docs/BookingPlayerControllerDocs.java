package com.project.golfofficeapi.controllers.docs;

import com.project.golfofficeapi.dto.BookingPlayerDTO;
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

public interface BookingPlayerControllerDocs {

    @Operation(summary = "Find All Booking Players", description = "Finds all player entries attached to bookings.", tags = {"Booking Players"}, responses = {
            @ApiResponse(description = "Success", responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = BookingPlayerDTO.class)))),
            @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
    })
    List<BookingPlayerDTO> findAll();

    @Operation(summary = "Find Booking Player by ID", description = "Finds a specific booking player by ID.", tags = {"Booking Players"}, responses = {
            @ApiResponse(description = "Success", responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = BookingPlayerDTO.class))),
            @ApiResponse(responseCode = "404", ref = "#/components/responses/NotFound"),
            @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
    })
    BookingPlayerDTO findById(@PathVariable("id") Long id);

    @Operation(summary = "Create Booking Player", description = "Adds a player or group entry to a booking, applies green fee, capacity rules, and updates tee time/bookings totals.", tags = {"Booking Players"}, responses = {
            @ApiResponse(description = "Success", responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = BookingPlayerDTO.class))),
            @ApiResponse(responseCode = "400", ref = "#/components/responses/BadRequest"),
            @ApiResponse(responseCode = "404", ref = "#/components/responses/NotFound"),
            @ApiResponse(responseCode = "409", ref = "#/components/responses/Conflict"),
            @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
    })
    BookingPlayerDTO create(@Valid @RequestBody BookingPlayerDTO bookingPlayer);

    @Operation(summary = "Update Booking Player", description = "Updates a booking player, including check-in and player count rules.", tags = {"Booking Players"}, responses = {
            @ApiResponse(description = "Success", responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = BookingPlayerDTO.class))),
            @ApiResponse(responseCode = "400", ref = "#/components/responses/BadRequest"),
            @ApiResponse(responseCode = "404", ref = "#/components/responses/NotFound"),
            @ApiResponse(responseCode = "409", ref = "#/components/responses/Conflict"),
            @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
    })
    BookingPlayerDTO update(@Valid @RequestBody BookingPlayerDTO bookingPlayer);

    @Operation(summary = "Delete Booking Player", description = "Removes a player entry from a booking and recalculates tee time/bookings totals when allowed.", tags = {"Booking Players"}, responses = {
            @ApiResponse(responseCode = "204", ref = "#/components/responses/NoContent"),
            @ApiResponse(responseCode = "404", ref = "#/components/responses/NotFound"),
            @ApiResponse(responseCode = "409", ref = "#/components/responses/Conflict"),
            @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
    })
    ResponseEntity<?> delete(@PathVariable("id") Long id);
}
