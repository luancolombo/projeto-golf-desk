package com.project.golfofficeapi.controllers.docs;

import com.project.golfofficeapi.dto.RentalDamageReportDTO;
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

public interface RentalDamageReportControllerDocs {

    @Operation(
            summary = "Find All Rental Damage Reports",
            description = "Finds all rental damage reports.",
            tags = {"Rental Damage Reports"},
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = RentalDamageReportDTO.class)))),
                    @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
            }
    )
    List<RentalDamageReportDTO> findAll();

    @Operation(
            summary = "Find Rental Damage Report by ID",
            description = "Finds a specific rental damage report by ID.",
            tags = {"Rental Damage Reports"},
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = RentalDamageReportDTO.class))),
                    @ApiResponse(responseCode = "404", ref = "#/components/responses/NotFound"),
                    @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
            }
    )
    RentalDamageReportDTO findById(@PathVariable("id") Long id);

    @Operation(
            summary = "Find Damage Reports by Status",
            description = "Finds rental damage reports by status.",
            tags = {"Rental Damage Reports"},
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = RentalDamageReportDTO.class)))),
                    @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
            }
    )
    List<RentalDamageReportDTO> findByStatus(@PathVariable("status") String status);

    @Operation(
            summary = "Find Damage Reports by Rental Item",
            description = "Finds rental damage reports attached to a rental item.",
            tags = {"Rental Damage Reports"},
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = RentalDamageReportDTO.class)))),
                    @ApiResponse(responseCode = "404", ref = "#/components/responses/NotFound"),
                    @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
            }
    )
    List<RentalDamageReportDTO> findByRentalItemId(@PathVariable("rentalItemId") Long rentalItemId);

    @Operation(
            summary = "Find Damage Reports by Rental Transaction",
            description = "Finds rental damage reports attached to a rental transaction.",
            tags = {"Rental Damage Reports"},
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = RentalDamageReportDTO.class)))),
                    @ApiResponse(responseCode = "404", ref = "#/components/responses/NotFound"),
                    @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
            }
    )
    List<RentalDamageReportDTO> findByRentalTransactionId(@PathVariable("rentalTransactionId") Long rentalTransactionId);

    @Operation(
            summary = "Create Rental Damage Report",
            description = "Creates a rental damage report during item return inspection.",
            tags = {"Rental Damage Reports"},
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = RentalDamageReportDTO.class))),
                    @ApiResponse(responseCode = "400", ref = "#/components/responses/BadRequest"),
                    @ApiResponse(responseCode = "404", ref = "#/components/responses/NotFound"),
                    @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
            }
    )
    RentalDamageReportDTO create(@Valid @RequestBody RentalDamageReportDTO report);

    @Operation(
            summary = "Update Rental Damage Report",
            description = "Updates an existing rental damage report.",
            tags = {"Rental Damage Reports"},
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = RentalDamageReportDTO.class))),
                    @ApiResponse(responseCode = "400", ref = "#/components/responses/BadRequest"),
                    @ApiResponse(responseCode = "404", ref = "#/components/responses/NotFound"),
                    @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
            }
    )
    RentalDamageReportDTO update(@Valid @RequestBody RentalDamageReportDTO report);

    @Operation(
            summary = "Resolve Rental Damage Report",
            description = "Marks a rental damage report as resolved.",
            tags = {"Rental Damage Reports"},
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = RentalDamageReportDTO.class))),
                    @ApiResponse(responseCode = "404", ref = "#/components/responses/NotFound"),
                    @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
            }
    )
    RentalDamageReportDTO resolve(@PathVariable("id") Long id);

    @Operation(
            summary = "Delete Rental Damage Report",
            description = "Deletes a rental damage report by ID.",
            tags = {"Rental Damage Reports"},
            responses = {
                    @ApiResponse(responseCode = "204", ref = "#/components/responses/NoContent"),
                    @ApiResponse(responseCode = "404", ref = "#/components/responses/NotFound"),
                    @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
            }
    )
    ResponseEntity<?> delete(@PathVariable("id") Long id);
}
