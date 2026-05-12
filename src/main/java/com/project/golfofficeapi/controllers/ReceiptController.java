package com.project.golfofficeapi.controllers;

import com.project.golfofficeapi.dto.ReceiptDTO;
import com.project.golfofficeapi.services.ReceiptService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/receipt")
public class ReceiptController {

    private final ReceiptService service;

    public ReceiptController(ReceiptService service) {
        this.service = service;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ReceiptDTO> findAll() {
        return service.findAll();
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ReceiptDTO findById(@PathVariable("id") Long id) {
        return service.findById(id);
    }

    @GetMapping(value = "/booking/{bookingId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ReceiptDTO> findByBookingId(@PathVariable("bookingId") Long bookingId) {
        return service.findByBookingId(bookingId);
    }

    @GetMapping(value = "/booking-player/{bookingPlayerId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ReceiptDTO> findByBookingPlayerId(@PathVariable("bookingPlayerId") Long bookingPlayerId) {
        return service.findByBookingPlayerId(bookingPlayerId);
    }

    @GetMapping(value = "/payment/{paymentId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ReceiptDTO> findByPaymentId(@PathVariable("paymentId") Long paymentId) {
        return service.findByPaymentId(paymentId);
    }

    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ReceiptDTO create(@Valid @RequestBody ReceiptDTO receipt) {
        return service.create(receipt);
    }

    @PostMapping(value = "/payment/{paymentId}/issue", produces = MediaType.APPLICATION_JSON_VALUE)
    public ReceiptDTO issueByPaymentId(@PathVariable("paymentId") Long paymentId) {
        return service.issueReceiptForPaymentId(paymentId);
    }

    @PutMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ReceiptDTO update(@Valid @RequestBody ReceiptDTO receipt) {
        return service.update(receipt);
    }

    @PutMapping(value = "/{id}/cancel", produces = MediaType.APPLICATION_JSON_VALUE)
    public ReceiptDTO cancel(
            @PathVariable("id") Long id,
            @RequestParam(value = "reason", required = false) String reason
    ) {
        return service.cancel(id, reason);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
