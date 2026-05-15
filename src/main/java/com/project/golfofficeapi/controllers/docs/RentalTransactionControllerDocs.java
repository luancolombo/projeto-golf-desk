package com.project.golfofficeapi.controllers.docs;

import com.project.golfofficeapi.dto.RentalTransactionDTO;
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

public interface RentalTransactionControllerDocs {

    @Operation(
            summary = "Find All Rental Transactions",
            description = "Finds all rental transactions.",
            tags = {"Rental Transactions"},
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = RentalTransactionDTO.class)))),
                    @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
            }
    )
    List<RentalTransactionDTO> findAll();

    @Operation(
            summary = "Find Rental Transaction by ID",
            description = "Finds a specific rental transaction by ID.",
            tags = {"Rental Transactions"},
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = RentalTransactionDTO.class))),
                    @ApiResponse(responseCode = "404", ref = "#/components/responses/NotFound"),
                    @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
            }
    )
    RentalTransactionDTO findById(@PathVariable("id") Long id);

    @Operation(
            summary = "Find Rentals by Booking",
            description = "Finds rental transactions attached to a booking.",
            tags = {"Rental Transactions"},
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = RentalTransactionDTO.class)))),
                    @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
            }
    )
    List<RentalTransactionDTO> findByBookingId(@PathVariable("bookingId") Long bookingId);

    @Operation(
            summary = "Find Rentals by Booking Player",
            description = "Finds rental transactions attached to a specific booking player.",
            tags = {"Rental Transactions"},
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = RentalTransactionDTO.class)))),
                    @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
            }
    )
    List<RentalTransactionDTO> findByBookingPlayerId(@PathVariable("bookingPlayerId") Long bookingPlayerId);

    @Operation(
            summary = "Return All Rentals by Booking",
            description = "Marks all rented items from a booking as returned and restores stock.",
            tags = {"Rental Transactions"},
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = RentalTransactionDTO.class)))),
                    @ApiResponse(responseCode = "404", ref = "#/components/responses/NotFound"),
                    @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
            }
    )
    List<RentalTransactionDTO> returnAllByBookingId(@PathVariable("bookingId") Long bookingId);

    @Operation(
            summary = "Return All Rentals",
            description = "Marks all currently rented items as returned and restores stock.",
            tags = {"Rental Transactions"},
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = RentalTransactionDTO.class)))),
                    @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
            }
    )
    List<RentalTransactionDTO> returnAll();

    @Operation(
            summary = "Create Rental Transaction",
            description = "Creates a rental transaction for a booking player, calculates rental price, reserves stock, and updates booking totals.",
            tags = {"Rental Transactions"},
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = RentalTransactionDTO.class))),
                    @ApiResponse(responseCode = "400", ref = "#/components/responses/BadRequest"),
                    @ApiResponse(responseCode = "404", ref = "#/components/responses/NotFound"),
                    @ApiResponse(responseCode = "409", ref = "#/components/responses/Conflict"),
                    @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
            }
    )
    RentalTransactionDTO create(@Valid @RequestBody RentalTransactionDTO rentalTransaction);

    @Operation(
            summary = "Update Rental Transaction",
            description = "Updates a rental transaction while preserving stock and booking total rules.",
            tags = {"Rental Transactions"},
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = RentalTransactionDTO.class))),
                    @ApiResponse(responseCode = "400", ref = "#/components/responses/BadRequest"),
                    @ApiResponse(responseCode = "404", ref = "#/components/responses/NotFound"),
                    @ApiResponse(responseCode = "409", ref = "#/components/responses/Conflict"),
                    @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
            }
    )
    RentalTransactionDTO update(@Valid @RequestBody RentalTransactionDTO rentalTransaction);

    @Operation(
            summary = "Delete Rental Transaction",
            description = "Deletes a rental transaction when allowed by the current business rules.",
            tags = {"Rental Transactions"},
            responses = {
                    @ApiResponse(responseCode = "204", ref = "#/components/responses/NoContent"),
                    @ApiResponse(responseCode = "404", ref = "#/components/responses/NotFound"),
                    @ApiResponse(responseCode = "409", ref = "#/components/responses/Conflict"),
                    @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
            }
    )
    ResponseEntity<?> delete(@PathVariable("id") Long id);
}
