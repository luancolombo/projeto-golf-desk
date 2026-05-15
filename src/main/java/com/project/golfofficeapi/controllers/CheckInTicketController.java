package com.project.golfofficeapi.controllers;

import com.project.golfofficeapi.controllers.docs.CheckInTicketControllerDocs;
import com.project.golfofficeapi.dto.CheckInTicketDTO;
import com.project.golfofficeapi.services.CheckInTicketService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/check-in-ticket")
@Tag(name = "Check-in Tickets", description = "Endpoints for starter check-in tickets")
public class CheckInTicketController implements CheckInTicketControllerDocs {

    private final CheckInTicketService service;

    public CheckInTicketController(CheckInTicketService service) {
        this.service = service;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Override
    public List<CheckInTicketDTO> findAll() {
        return service.findAll();
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Override
    public CheckInTicketDTO findById(@PathVariable("id") Long id) {
        return service.findById(id);
    }

    @GetMapping(value = "/booking-player/{bookingPlayerId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Override
    public List<CheckInTicketDTO> findByBookingPlayerId(@PathVariable("bookingPlayerId") Long bookingPlayerId) {
        return service.findByBookingPlayerId(bookingPlayerId);
    }

    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Override
    public CheckInTicketDTO create(@Valid @RequestBody CheckInTicketDTO ticket) {
        return service.create(ticket);
    }

    @PostMapping(value = "/booking-player/{bookingPlayerId}/issue", produces = MediaType.APPLICATION_JSON_VALUE)
    @Override
    public CheckInTicketDTO issueByBookingPlayerId(@PathVariable("bookingPlayerId") Long bookingPlayerId) {
        return service.issueByBookingPlayerId(bookingPlayerId);
    }

    @PutMapping(value = "/{id}/cancel", produces = MediaType.APPLICATION_JSON_VALUE)
    @Override
    public CheckInTicketDTO cancel(
            @PathVariable("id") Long id,
            @RequestParam(value = "reason", required = false) String reason
    ) {
        return service.cancel(id, reason);
    }

    @DeleteMapping(value = "/{id}")
    @Override
    public ResponseEntity<?> delete(@PathVariable("id") Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
