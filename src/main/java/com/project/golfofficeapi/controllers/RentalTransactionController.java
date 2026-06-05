package com.project.golfofficeapi.controllers;

import com.project.golfofficeapi.controllers.docs.RentalTransactionControllerDocs;
import com.project.golfofficeapi.controllers.assemblers.ResourceLinkAssembler;
import com.project.golfofficeapi.dto.RentalTransactionDTO;
import com.project.golfofficeapi.services.RentalTransactionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rental-transaction")
@Tag(name = "Rental Transactions", description = "Endpoints for rental reservation, return, stock, and booking totals")
public class RentalTransactionController implements RentalTransactionControllerDocs {

    private final RentalTransactionService service;
    private final ResourceLinkAssembler links;

    public RentalTransactionController(RentalTransactionService service, ResourceLinkAssembler links) {
        this.service = service;
        this.links = links;
    }

    @GetMapping(
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Override
    public Page<RentalTransactionDTO> findAll(Pageable pageable) {
        return links.rentalTransactions(service.findAll(pageable));
    }

    @GetMapping(value = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Override
    public RentalTransactionDTO findById(@PathVariable("id") Long id) {
        return links.rentalTransaction(service.findById(id));
    }

    @GetMapping(value = "/booking/{bookingId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Override
    public Page<RentalTransactionDTO> findByBookingId(@PathVariable("bookingId") Long bookingId, Pageable pageable) {
        return links.rentalTransactions(service.findByBookingId(bookingId, pageable));
    }

    @GetMapping(value = "/booking-player/{bookingPlayerId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Override
    public Page<RentalTransactionDTO> findByBookingPlayerId(@PathVariable("bookingPlayerId") Long bookingPlayerId, Pageable pageable) {
        return links.rentalTransactions(service.findByBookingPlayerId(bookingPlayerId, pageable));
    }

    @PutMapping(value = "/booking/{bookingId}/return-all",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Override
    public List<RentalTransactionDTO> returnAllByBookingId(@PathVariable("bookingId") Long bookingId) {
        return links.rentalTransactions(service.returnAllByBookingId(bookingId));
    }

    @PutMapping(value = "/return-all",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Override
    public List<RentalTransactionDTO> returnAll() {
        return links.rentalTransactions(service.returnAll());
    }

    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Override
    public RentalTransactionDTO create(@Valid @RequestBody RentalTransactionDTO rentalTransaction) {
        return links.rentalTransaction(service.create(rentalTransaction));
    }

    @PutMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Override
    public RentalTransactionDTO update(@Valid @RequestBody RentalTransactionDTO rentalTransaction) {
        return links.rentalTransaction(service.update(rentalTransaction));
    }

    @DeleteMapping(value = "/{id}")
    @Override
    public ResponseEntity<?> delete(@PathVariable("id") Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
