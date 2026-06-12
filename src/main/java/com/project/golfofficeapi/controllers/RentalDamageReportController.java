package com.project.golfofficeapi.controllers;

import com.project.golfofficeapi.controllers.docs.RentalDamageReportControllerDocs;
import com.project.golfofficeapi.controllers.assemblers.ResourceLinkAssembler;
import com.project.golfofficeapi.dto.RentalDamageReportDTO;
import com.project.golfofficeapi.services.RentalDamageReportService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rental-damage-report")
@Tag(name = "Rental Damage Reports", description = "Endpoints for reporting and resolving damaged rental items")
public class RentalDamageReportController implements RentalDamageReportControllerDocs {

    private final RentalDamageReportService service;
    private final ResourceLinkAssembler links;

    public RentalDamageReportController(RentalDamageReportService service, ResourceLinkAssembler links) {
        this.service = service;
        this.links = links;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Override
    public Page<RentalDamageReportDTO> findAll(Pageable pageable) {
        return links.rentalDamageReports(service.findAll(pageable));
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Override
    public RentalDamageReportDTO findById(@PathVariable("id") Long id) {
        return links.rentalDamageReport(service.findById(id));
    }

    @GetMapping(value = "/status/{status}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Override
    public Page<RentalDamageReportDTO> findByStatus(@PathVariable("status") String status, Pageable pageable) {
        return links.rentalDamageReports(service.findByStatus(status, pageable));
    }

    @GetMapping(value = "/rental-item/{rentalItemId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Override
    public Page<RentalDamageReportDTO> findByRentalItemId(@PathVariable("rentalItemId") Long rentalItemId, Pageable pageable) {
        return links.rentalDamageReports(service.findByRentalItemId(rentalItemId, pageable));
    }

    @GetMapping(value = "/rental-transaction/{rentalTransactionId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Override
    public Page<RentalDamageReportDTO> findByRentalTransactionId(@PathVariable("rentalTransactionId") Long rentalTransactionId, Pageable pageable) {
        return links.rentalDamageReports(service.findByRentalTransactionId(rentalTransactionId, pageable));
    }

    @PostMapping(
            value = "/rental-transaction/{rentalTransactionId}/damage",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Override
    public RentalDamageReportDTO reportTransactionDamage(
            @PathVariable("rentalTransactionId") Long rentalTransactionId,
            @Valid @RequestBody RentalDamageReportDTO report
    ) {
        return links.rentalDamageReport(service.reportTransactionDamage(rentalTransactionId, report));
    }

    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Override
    public RentalDamageReportDTO create(@Valid @RequestBody RentalDamageReportDTO report) {
        return links.rentalDamageReport(service.create(report));
    }

    @PutMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Override
    public RentalDamageReportDTO update(@Valid @RequestBody RentalDamageReportDTO report) {
        return links.rentalDamageReport(service.update(report));
    }

    @PutMapping(value = "/{id}/resolve", produces = MediaType.APPLICATION_JSON_VALUE)
    @Override
    public RentalDamageReportDTO resolve(@PathVariable("id") Long id) {
        return links.rentalDamageReport(service.resolve(id));
    }

    @DeleteMapping(value = "/{id}")
    @Override
    public ResponseEntity<?> delete(@PathVariable("id") Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
