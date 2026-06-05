package com.project.golfofficeapi.controllers;

import com.project.golfofficeapi.controllers.docs.PaymentControllerDocs;
import com.project.golfofficeapi.controllers.assemblers.ResourceLinkAssembler;
import com.project.golfofficeapi.dto.PaymentDTO;
import com.project.golfofficeapi.services.PaymentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payment")
@Tag(name = "Payments", description = "Endpoints for player-level payments and refunds")
public class PaymentController implements PaymentControllerDocs {

    private final PaymentService service;
    private final ResourceLinkAssembler links;

    public PaymentController(PaymentService service, ResourceLinkAssembler links) {
        this.service = service;
        this.links = links;
    }

    @GetMapping(
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Override
    public Page<PaymentDTO> findAll(Pageable pageable) {
        return links.payments(service.findAll(pageable));
    }

    @GetMapping(value = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Override
    public PaymentDTO findById(@PathVariable("id") Long id) {
        return links.payment(service.findById(id));
    }

    @GetMapping(value = "/booking/{bookingId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Override
    public Page<PaymentDTO> findByBookingId(@PathVariable("bookingId") Long bookingId, Pageable pageable) {
        return links.payments(service.findByBookingId(bookingId, pageable));
    }

    @GetMapping(value = "/booking-player/{bookingPlayerId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Override
    public Page<PaymentDTO> findByBookingPlayerId(@PathVariable("bookingPlayerId") Long bookingPlayerId, Pageable pageable) {
        return links.payments(service.findByBookingPlayerId(bookingPlayerId, pageable));
    }

    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Override
    public PaymentDTO create(@Valid @RequestBody PaymentDTO payment) {
        return links.payment(service.create(payment));
    }

    @PutMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Override
    public PaymentDTO update(@Valid @RequestBody PaymentDTO payment) {
        return links.payment(service.update(payment));
    }

    @DeleteMapping(value = "/{id}")
    @Override
    public ResponseEntity<?> delete(@PathVariable("id") Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
