package com.project.golfofficeapi.controllers.docs;

import com.project.golfofficeapi.dto.CashRegisterClosureItemDTO;
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

public interface CashRegisterClosureItemControllerDocs {

    @Operation(summary = "Find All Cash Register Closure Items", description = "Finds all cash register closure line items.", tags = {"Cash Register Closure Items"}, responses = {
            @ApiResponse(description = "Success", responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = CashRegisterClosureItemDTO.class)))),
            @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
    })
    List<CashRegisterClosureItemDTO> findAll();

    @Operation(summary = "Find Cash Register Closure Item by ID", description = "Finds a specific cash register closure item by ID.", tags = {"Cash Register Closure Items"}, responses = {
            @ApiResponse(description = "Success", responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = CashRegisterClosureItemDTO.class))),
            @ApiResponse(responseCode = "404", ref = "#/components/responses/NotFound"),
            @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
    })
    CashRegisterClosureItemDTO findById(@PathVariable("id") Long id);

    @Operation(summary = "Find Items by Cash Register Closure", description = "Finds all line items for a cash register closure.", tags = {"Cash Register Closure Items"}, responses = {
            @ApiResponse(description = "Success", responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = CashRegisterClosureItemDTO.class)))),
            @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
    })
    List<CashRegisterClosureItemDTO> findByCashRegisterClosureId(@PathVariable("cashRegisterClosureId") Long cashRegisterClosureId);

    @Operation(summary = "Create Cash Register Closure Item", description = "Creates a cash register closure line item.", tags = {"Cash Register Closure Items"}, responses = {
            @ApiResponse(description = "Success", responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = CashRegisterClosureItemDTO.class))),
            @ApiResponse(responseCode = "400", ref = "#/components/responses/BadRequest"),
            @ApiResponse(responseCode = "404", ref = "#/components/responses/NotFound"),
            @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
    })
    CashRegisterClosureItemDTO create(@Valid @RequestBody CashRegisterClosureItemDTO item);

    @Operation(summary = "Update Cash Register Closure Item", description = "Updates a cash register closure line item.", tags = {"Cash Register Closure Items"}, responses = {
            @ApiResponse(description = "Success", responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = CashRegisterClosureItemDTO.class))),
            @ApiResponse(responseCode = "400", ref = "#/components/responses/BadRequest"),
            @ApiResponse(responseCode = "404", ref = "#/components/responses/NotFound"),
            @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
    })
    CashRegisterClosureItemDTO update(@Valid @RequestBody CashRegisterClosureItemDTO item);

    @Operation(summary = "Delete Cash Register Closure Item", description = "Deletes a cash register closure item by ID when allowed by the current business rules.", tags = {"Cash Register Closure Items"}, responses = {
            @ApiResponse(responseCode = "204", ref = "#/components/responses/NoContent"),
            @ApiResponse(responseCode = "404", ref = "#/components/responses/NotFound"),
            @ApiResponse(responseCode = "409", ref = "#/components/responses/Conflict"),
            @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
    })
    ResponseEntity<?> delete(@PathVariable("id") Long id);
}
