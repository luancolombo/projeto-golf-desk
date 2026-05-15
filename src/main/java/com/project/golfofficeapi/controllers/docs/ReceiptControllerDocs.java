package com.project.golfofficeapi.controllers.docs;

import com.project.golfofficeapi.dto.ReceiptDTO;
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

public interface ReceiptControllerDocs {

    @Operation(summary = "Find All Receipts", description = "Finds all receipts.", tags = {"Receipts"}, responses = {
            @ApiResponse(description = "Success", responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = ReceiptDTO.class)))),
            @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
    })
    List<ReceiptDTO> findAll();

    @Operation(summary = "Find Receipt by ID", description = "Finds a specific receipt by ID.", tags = {"Receipts"}, responses = {
            @ApiResponse(description = "Success", responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ReceiptDTO.class))),
            @ApiResponse(responseCode = "404", ref = "#/components/responses/NotFound"),
            @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
    })
    ReceiptDTO findById(@PathVariable("id") Long id);

    @Operation(summary = "Find Receipts by Booking", description = "Finds receipts attached to a booking.", tags = {"Receipts"}, responses = {
            @ApiResponse(description = "Success", responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = ReceiptDTO.class)))),
            @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
    })
    List<ReceiptDTO> findByBookingId(@PathVariable("bookingId") Long bookingId);

    @Operation(summary = "Find Receipts by Booking Player", description = "Finds receipts attached to a booking player.", tags = {"Receipts"}, responses = {
            @ApiResponse(description = "Success", responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = ReceiptDTO.class)))),
            @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
    })
    List<ReceiptDTO> findByBookingPlayerId(@PathVariable("bookingPlayerId") Long bookingPlayerId);

    @Operation(summary = "Find Receipts by Payment", description = "Finds receipts issued for a payment.", tags = {"Receipts"}, responses = {
            @ApiResponse(description = "Success", responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = ReceiptDTO.class)))),
            @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
    })
    List<ReceiptDTO> findByPaymentId(@PathVariable("paymentId") Long paymentId);

    @Operation(summary = "Create Receipt", description = "Creates a receipt with historical snapshots for financial record keeping.", tags = {"Receipts"}, responses = {
            @ApiResponse(description = "Success", responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ReceiptDTO.class))),
            @ApiResponse(responseCode = "400", ref = "#/components/responses/BadRequest"),
            @ApiResponse(responseCode = "404", ref = "#/components/responses/NotFound"),
            @ApiResponse(responseCode = "409", ref = "#/components/responses/Conflict"),
            @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
    })
    ReceiptDTO create(@Valid @RequestBody ReceiptDTO receipt);

    @Operation(summary = "Issue Receipt by Payment", description = "Issues a receipt for a payment using payment, player, booking, green fee, and rental snapshots.", tags = {"Receipts"}, responses = {
            @ApiResponse(description = "Success", responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ReceiptDTO.class))),
            @ApiResponse(responseCode = "404", ref = "#/components/responses/NotFound"),
            @ApiResponse(responseCode = "409", ref = "#/components/responses/Conflict"),
            @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
    })
    ReceiptDTO issueByPaymentId(@PathVariable("paymentId") Long paymentId);

    @Operation(summary = "Update Receipt", description = "Updates an existing receipt when allowed by the current business rules.", tags = {"Receipts"}, responses = {
            @ApiResponse(description = "Success", responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ReceiptDTO.class))),
            @ApiResponse(responseCode = "400", ref = "#/components/responses/BadRequest"),
            @ApiResponse(responseCode = "404", ref = "#/components/responses/NotFound"),
            @ApiResponse(responseCode = "409", ref = "#/components/responses/Conflict"),
            @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
    })
    ReceiptDTO update(@Valid @RequestBody ReceiptDTO receipt);

    @Operation(summary = "Cancel Receipt", description = "Cancels a receipt and stores an optional cancellation reason while preserving historical data.", tags = {"Receipts"}, responses = {
            @ApiResponse(description = "Success", responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ReceiptDTO.class))),
            @ApiResponse(responseCode = "404", ref = "#/components/responses/NotFound"),
            @ApiResponse(responseCode = "409", ref = "#/components/responses/Conflict"),
            @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
    })
    ReceiptDTO cancel(@PathVariable("id") Long id, @RequestParam(value = "reason", required = false) String reason);

    @Operation(summary = "Delete Receipt", description = "Deletes a receipt by ID when allowed by the current business rules.", tags = {"Receipts"}, responses = {
            @ApiResponse(responseCode = "204", ref = "#/components/responses/NoContent"),
            @ApiResponse(responseCode = "404", ref = "#/components/responses/NotFound"),
            @ApiResponse(responseCode = "409", ref = "#/components/responses/Conflict"),
            @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
    })
    ResponseEntity<?> delete(@PathVariable("id") Long id);
}
