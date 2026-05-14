package com.project.golfofficeapi.controllers;

import com.project.golfofficeapi.dto.RentalDamageReportDTO;
import com.project.golfofficeapi.services.RentalDamageReportService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rental-damage-report")
public class RentalDamageReportController {

    private final RentalDamageReportService service;

    public RentalDamageReportController(RentalDamageReportService service) {
        this.service = service;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<RentalDamageReportDTO> findAll() {
        return service.findAll();
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public RentalDamageReportDTO findById(@PathVariable("id") Long id) {
        return service.findById(id);
    }

    @GetMapping(value = "/status/{status}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<RentalDamageReportDTO> findByStatus(@PathVariable("status") String status) {
        return service.findByStatus(status);
    }

    @GetMapping(value = "/rental-item/{rentalItemId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<RentalDamageReportDTO> findByRentalItemId(@PathVariable("rentalItemId") Long rentalItemId) {
        return service.findByRentalItemId(rentalItemId);
    }

    @GetMapping(value = "/rental-transaction/{rentalTransactionId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<RentalDamageReportDTO> findByRentalTransactionId(@PathVariable("rentalTransactionId") Long rentalTransactionId) {
        return service.findByRentalTransactionId(rentalTransactionId);
    }

    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public RentalDamageReportDTO create(@Valid @RequestBody RentalDamageReportDTO report) {
        return service.create(report);
    }

    @PutMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public RentalDamageReportDTO update(@Valid @RequestBody RentalDamageReportDTO report) {
        return service.update(report);
    }

    @PutMapping(value = "/{id}/resolve", produces = MediaType.APPLICATION_JSON_VALUE)
    public RentalDamageReportDTO resolve(@PathVariable("id") Long id) {
        return service.resolve(id);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
