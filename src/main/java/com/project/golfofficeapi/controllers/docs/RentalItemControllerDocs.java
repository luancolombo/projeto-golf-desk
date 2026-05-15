package com.project.golfofficeapi.controllers.docs;

import com.project.golfofficeapi.dto.RentalItemDTO;
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

public interface RentalItemControllerDocs {

    @Operation(
            summary = "Find All Rental Items",
            description = "Finds all rental items available in the inventory catalog.",
            tags = {"Rental Items"},
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = RentalItemDTO.class))
                            )
                    ),
                    @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
            }
    )
    List<RentalItemDTO> findAll();

    @Operation(
            summary = "Find Rental Item by ID",
            description = "Finds a specific rental item by ID.",
            tags = {"Rental Items"},
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = RentalItemDTO.class))),
                    @ApiResponse(responseCode = "404", ref = "#/components/responses/NotFound"),
                    @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
            }
    )
    RentalItemDTO findById(@PathVariable("id") Long id);

    @Operation(
            summary = "Create Rental Item",
            description = "Creates a new rentable inventory item.",
            tags = {"Rental Items"},
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = RentalItemDTO.class))),
                    @ApiResponse(responseCode = "400", ref = "#/components/responses/BadRequest"),
                    @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
            }
    )
    RentalItemDTO create(@Valid @RequestBody RentalItemDTO rentalItem);

    @Operation(
            summary = "Update Rental Item",
            description = "Updates an existing rental item, including stock, price, type, and active status.",
            tags = {"Rental Items"},
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = RentalItemDTO.class))),
                    @ApiResponse(responseCode = "400", ref = "#/components/responses/BadRequest"),
                    @ApiResponse(responseCode = "404", ref = "#/components/responses/NotFound"),
                    @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
            }
    )
    RentalItemDTO update(@Valid @RequestBody RentalItemDTO rentalItem);

    @Operation(
            summary = "Delete Rental Item",
            description = "Deletes a rental item by ID when allowed by the current business rules.",
            tags = {"Rental Items"},
            responses = {
                    @ApiResponse(responseCode = "204", ref = "#/components/responses/NoContent"),
                    @ApiResponse(responseCode = "404", ref = "#/components/responses/NotFound"),
                    @ApiResponse(responseCode = "409", ref = "#/components/responses/Conflict"),
                    @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
            }
    )
    ResponseEntity<?> delete(@PathVariable("id") Long id);
}
