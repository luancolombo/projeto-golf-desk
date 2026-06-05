package com.project.golfofficeapi.controllers;

import com.project.golfofficeapi.controllers.docs.CheckInTicketControllerDocs;
import com.project.golfofficeapi.controllers.assemblers.ResourceLinkAssembler;
import com.project.golfofficeapi.dto.CheckInTicketDTO;
import com.project.golfofficeapi.services.CheckInTicketService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/check-in-ticket")
@Tag(name = "Check-in Tickets", description = "Endpoints for starter check-in tickets")
public class CheckInTicketController implements CheckInTicketControllerDocs {

    private final CheckInTicketService service;
    private final ResourceLinkAssembler links;

    public CheckInTicketController(CheckInTicketService service, ResourceLinkAssembler links) {
        this.service = service;
        this.links = links;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Override
    public Page<CheckInTicketDTO> findAll(Pageable pageable) {
        return links.checkInTickets(service.findAll(pageable));
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Override
    public CheckInTicketDTO findById(@PathVariable("id") Long id) {
        return links.checkInTicket(service.findById(id));
    }

    @GetMapping(value = "/booking-player/{bookingPlayerId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Override
    public Page<CheckInTicketDTO> findByBookingPlayerId(
            @PathVariable("bookingPlayerId") Long bookingPlayerId,
            Pageable pageable
    ) {
        return links.checkInTickets(service.findByBookingPlayerId(bookingPlayerId, pageable));
    }

    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Override
    public CheckInTicketDTO create(@Valid @RequestBody CheckInTicketDTO ticket) {
        return links.checkInTicket(service.create(ticket));
    }

    @PostMapping(value = "/booking-player/{bookingPlayerId}/issue", produces = MediaType.APPLICATION_JSON_VALUE)
    @Override
    public CheckInTicketDTO issueByBookingPlayerId(@PathVariable("bookingPlayerId") Long bookingPlayerId) {
        return links.checkInTicket(service.issueByBookingPlayerId(bookingPlayerId));
    }

    @PutMapping(value = "/{id}/cancel", produces = MediaType.APPLICATION_JSON_VALUE)
    @Override
    public CheckInTicketDTO cancel(
            @PathVariable("id") Long id,
            @RequestParam(value = "reason", required = false) String reason
    ) {
        return links.checkInTicket(service.cancel(id, reason));
    }

    @DeleteMapping(value = "/{id}")
    @Override
    public ResponseEntity<?> delete(@PathVariable("id") Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
