package com.project.golfofficeapi.controllers.docs;

import com.project.golfofficeapi.dto.AgendaDayDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

public interface AgendaControllerDocs {

    @Operation(
            summary = "Find Agenda Day",
            description = "Returns the optimized daily agenda payload with tee times, bookings, booking players, players, rental items, rental transactions, payments, receipts, receipt items, and check-in tickets for the requested date.",
            tags = {"Agenda"},
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AgendaDayDTO.class))),
                    @ApiResponse(responseCode = "400", ref = "#/components/responses/BadRequest"),
                    @ApiResponse(responseCode = "401", ref = "#/components/responses/Unauthorized"),
                    @ApiResponse(responseCode = "403", ref = "#/components/responses/Forbidden"),
                    @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
            }
    )
    AgendaDayDTO findDay(
            @Parameter(description = "Agenda date in ISO format.", example = "2026-05-16", required = true)
            @RequestParam("date")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date
    );
}
