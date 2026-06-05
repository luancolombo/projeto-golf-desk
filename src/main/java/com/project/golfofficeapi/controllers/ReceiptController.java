package com.project.golfofficeapi.controllers;

import com.project.golfofficeapi.controllers.docs.ReceiptControllerDocs;
import com.project.golfofficeapi.controllers.assemblers.ResourceLinkAssembler;
import com.project.golfofficeapi.dto.ReceiptDTO;
import com.project.golfofficeapi.services.ReceiptService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/receipt")
@Tag(name = "Receipts", description = "Endpoints for receipts and historical financial snapshots")
public class ReceiptController implements ReceiptControllerDocs {

    private final ReceiptService service;
    private final ResourceLinkAssembler links;

    public ReceiptController(ReceiptService service, ResourceLinkAssembler links) {
        this.service = service;
        this.links = links;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Override
    public Page<ReceiptDTO> findAll(Pageable pageable) {
        return links.receipts(service.findAll(pageable));
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Override
    public ReceiptDTO findById(@PathVariable("id") Long id) {
        return links.receipt(service.findById(id));
    }

    @GetMapping(value = "/booking/{bookingId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Override
    public Page<ReceiptDTO> findByBookingId(@PathVariable("bookingId") Long bookingId, Pageable pageable) {
        return links.receipts(service.findByBookingId(bookingId, pageable));
    }

    @GetMapping(value = "/booking-player/{bookingPlayerId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Override
    public Page<ReceiptDTO> findByBookingPlayerId(@PathVariable("bookingPlayerId") Long bookingPlayerId, Pageable pageable) {
        return links.receipts(service.findByBookingPlayerId(bookingPlayerId, pageable));
    }

    @GetMapping(value = "/payment/{paymentId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Override
    public Page<ReceiptDTO> findByPaymentId(@PathVariable("paymentId") Long paymentId, Pageable pageable) {
        return links.receipts(service.findByPaymentId(paymentId, pageable));
    }

    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Override
    public ReceiptDTO create(@Valid @RequestBody ReceiptDTO receipt) {
        return links.receipt(service.create(receipt));
    }

    @PostMapping(value = "/payment/{paymentId}/issue", produces = MediaType.APPLICATION_JSON_VALUE)
    @Override
    public ReceiptDTO issueByPaymentId(@PathVariable("paymentId") Long paymentId) {
        return links.receipt(service.issueReceiptForPaymentId(paymentId));
    }

    @PutMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Override
    public ReceiptDTO update(@Valid @RequestBody ReceiptDTO receipt) {
        return links.receipt(service.update(receipt));
    }

    @PutMapping(value = "/{id}/cancel", produces = MediaType.APPLICATION_JSON_VALUE)
    @Override
    public ReceiptDTO cancel(
            @PathVariable("id") Long id,
            @RequestParam(value = "reason", required = false) String reason
    ) {
        return links.receipt(service.cancel(id, reason));
    }

    @DeleteMapping(value = "/{id}")
    @Override
    public ResponseEntity<?> delete(@PathVariable("id") Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
