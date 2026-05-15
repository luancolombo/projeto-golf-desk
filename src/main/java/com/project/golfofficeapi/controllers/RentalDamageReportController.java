package com.project.golfofficeapi.controllers;

import com.project.golfofficeapi.controllers.docs.RentalDamageReportControllerDocs;
import com.project.golfofficeapi.dto.RentalDamageReportDTO;
import com.project.golfofficeapi.services.RentalDamageReportService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rental-damage-report")
@Tag(name = "Rental Damage Reports", description = "Endpoints for reporting and resolving damaged rental items")
public class RentalDamageReportController implements RentalDamageReportControllerDocs {

    private final RentalDamageReportService service;

    public RentalDamageReportController(RentalDamageReportService service) {
        this.service = service;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Override
    public List<RentalDamageReportDTO> findAll() {
        return service.findAll();
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Override
    public RentalDamageReportDTO findById(@PathVariable("id") Long id) {
        return service.findById(id);
    }

    @GetMapping(value = "/status/{status}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Override
    public List<RentalDamageReportDTO> findByStatus(@PathVariable("status") String status) {
        return service.findByStatus(status);
    }

    @GetMapping(value = "/rental-item/{rentalItemId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Override
    public List<RentalDamageReportDTO> findByRentalItemId(@PathVariable("rentalItemId") Long rentalItemId) {
        return service.findByRentalItemId(rentalItemId);
    }

    @GetMapping(value = "/rental-transaction/{rentalTransactionId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Override
    public List<RentalDamageReportDTO> findByRentalTransactionId(@PathVariable("rentalTransactionId") Long rentalTransactionId) {
        return service.findByRentalTransactionId(rentalTransactionId);
    }

    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Override
    public RentalDamageReportDTO create(@Valid @RequestBody RentalDamageReportDTO report) {
        return service.create(report);
    }

    @PutMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Override
    public RentalDamageReportDTO update(@Valid @RequestBody RentalDamageReportDTO report) {
        return service.update(report);
    }

    @PutMapping(value = "/{id}/resolve", produces = MediaType.APPLICATION_JSON_VALUE)
    @Override
    public RentalDamageReportDTO resolve(@PathVariable("id") Long id) {
        return service.resolve(id);
    }

    @DeleteMapping(value = "/{id}")
    @Override
    public ResponseEntity<?> delete(@PathVariable("id") Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
