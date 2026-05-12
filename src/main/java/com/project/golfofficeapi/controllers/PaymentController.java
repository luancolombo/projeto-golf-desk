package com.project.golfofficeapi.controllers;

import com.project.golfofficeapi.dto.PaymentDTO;
import com.project.golfofficeapi.services.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payment")
public class PaymentController {

    private final PaymentService service;

    public PaymentController(PaymentService service) {
        this.service = service;
    }

    @GetMapping(
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public List<PaymentDTO> findAll() {
        return service.findAll();
    }

    @GetMapping(value = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public PaymentDTO findById(@PathVariable("id") Long id) {
        return service.findById(id);
    }

    @GetMapping(value = "/booking/{bookingId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public List<PaymentDTO> findByBookingId(@PathVariable("bookingId") Long bookingId) {
        return service.findByBookingId(bookingId);
    }

    @GetMapping(value = "/booking-player/{bookingPlayerId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public List<PaymentDTO> findByBookingPlayerId(@PathVariable("bookingPlayerId") Long bookingPlayerId) {
        return service.findByBookingPlayerId(bookingPlayerId);
    }

    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public PaymentDTO create(@Valid @RequestBody PaymentDTO payment) {
        return service.create(payment);
    }

    @PutMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public PaymentDTO update(@Valid @RequestBody PaymentDTO payment) {
        return service.update(payment);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
