package com.project.golfofficeapi.controllers.docs;

import com.project.golfofficeapi.dto.CheckInTicketDTO;
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

public interface CheckInTicketControllerDocs {

    @Operation(summary = "Find All Check-in Tickets", description = "Finds all check-in tickets.", tags = {"Check-in Tickets"}, responses = {
            @ApiResponse(description = "Success", responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = CheckInTicketDTO.class)))),
            @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
    })
    List<CheckInTicketDTO> findAll();

    @Operation(summary = "Find Check-in Ticket by ID", description = "Finds a specific check-in ticket by ID.", tags = {"Check-in Tickets"}, responses = {
            @ApiResponse(description = "Success", responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = CheckInTicketDTO.class))),
            @ApiResponse(responseCode = "404", ref = "#/components/responses/NotFound"),
            @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
    })
    CheckInTicketDTO findById(@PathVariable("id") Long id);

    @Operation(summary = "Find Tickets by Booking Player", description = "Finds check-in tickets issued for a booking player.", tags = {"Check-in Tickets"}, responses = {
            @ApiResponse(description = "Success", responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = CheckInTicketDTO.class)))),
            @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
    })
    List<CheckInTicketDTO> findByBookingPlayerId(@PathVariable("bookingPlayerId") Long bookingPlayerId);

    @Operation(summary = "Create Check-in Ticket", description = "Creates a check-in ticket with tee 1, tee 10 crossing, and historical snapshots.", tags = {"Check-in Tickets"}, responses = {
            @ApiResponse(description = "Success", responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = CheckInTicketDTO.class))),
            @ApiResponse(responseCode = "400", ref = "#/components/responses/BadRequest"),
            @ApiResponse(responseCode = "404", ref = "#/components/responses/NotFound"),
            @ApiResponse(responseCode = "409", ref = "#/components/responses/Conflict"),
            @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
    })
    CheckInTicketDTO create(@Valid @RequestBody CheckInTicketDTO ticket);

    @Operation(summary = "Issue Ticket by Booking Player", description = "Issues a check-in ticket directly from a booking player.", tags = {"Check-in Tickets"}, responses = {
            @ApiResponse(description = "Success", responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = CheckInTicketDTO.class))),
            @ApiResponse(responseCode = "404", ref = "#/components/responses/NotFound"),
            @ApiResponse(responseCode = "409", ref = "#/components/responses/Conflict"),
            @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
    })
    CheckInTicketDTO issueByBookingPlayerId(@PathVariable("bookingPlayerId") Long bookingPlayerId);

    @Operation(summary = "Cancel Check-in Ticket", description = "Cancels a check-in ticket and stores an optional cancellation reason.", tags = {"Check-in Tickets"}, responses = {
            @ApiResponse(description = "Success", responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = CheckInTicketDTO.class))),
            @ApiResponse(responseCode = "404", ref = "#/components/responses/NotFound"),
            @ApiResponse(responseCode = "409", ref = "#/components/responses/Conflict"),
            @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
    })
    CheckInTicketDTO cancel(@PathVariable("id") Long id, @RequestParam(value = "reason", required = false) String reason);

    @Operation(summary = "Delete Check-in Ticket", description = "Deletes a check-in ticket by ID when allowed by the current business rules.", tags = {"Check-in Tickets"}, responses = {
            @ApiResponse(responseCode = "204", ref = "#/components/responses/NoContent"),
            @ApiResponse(responseCode = "404", ref = "#/components/responses/NotFound"),
            @ApiResponse(responseCode = "409", ref = "#/components/responses/Conflict"),
            @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
    })
    ResponseEntity<?> delete(@PathVariable("id") Long id);
}
