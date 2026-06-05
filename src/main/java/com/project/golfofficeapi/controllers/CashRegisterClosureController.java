package com.project.golfofficeapi.controllers;

import com.project.golfofficeapi.controllers.docs.CashRegisterClosureControllerDocs;
import com.project.golfofficeapi.controllers.assemblers.ResourceLinkAssembler;
import com.project.golfofficeapi.dto.CashRegisterClosureDTO;
import com.project.golfofficeapi.services.CashRegisterClosureService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/cash-register-closure")
@Tag(name = "Cash Register Closures", description = "Endpoints for daily cash register preview and closing")
public class CashRegisterClosureController implements CashRegisterClosureControllerDocs {

    private final CashRegisterClosureService service;
    private final ResourceLinkAssembler links;

    public CashRegisterClosureController(CashRegisterClosureService service, ResourceLinkAssembler links) {
        this.service = service;
        this.links = links;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Override
    public Page<CashRegisterClosureDTO> findAll(Pageable pageable) {
        return links.cashRegisterClosures(service.findAll(pageable));
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Override
    public CashRegisterClosureDTO findById(@PathVariable("id") Long id) {
        return links.cashRegisterClosure(service.findById(id));
    }

    @GetMapping(value = "/date/{businessDate}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Override
    public CashRegisterClosureDTO findByBusinessDate(
            @PathVariable("businessDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate businessDate
    ) {
        return links.cashRegisterClosure(service.findByBusinessDate(businessDate));
    }

    @GetMapping(value = "/preview", produces = MediaType.APPLICATION_JSON_VALUE)
    @Override
    public CashRegisterClosureDTO preview(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate businessDate
    ) {
        return links.cashRegisterClosure(service.preview(businessDate));
    }

    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Override
    public CashRegisterClosureDTO create(@Valid @RequestBody CashRegisterClosureDTO closure) {
        return links.cashRegisterClosure(service.create(closure));
    }

    @PostMapping(
            value = "/close",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Override
    public CashRegisterClosureDTO close(@Valid @RequestBody CashRegisterClosureDTO closure) {
        return links.cashRegisterClosure(service.close(closure));
    }

    @PutMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Override
    public CashRegisterClosureDTO update(@Valid @RequestBody CashRegisterClosureDTO closure) {
        return links.cashRegisterClosure(service.update(closure));
    }

    @DeleteMapping(value = "/{id}")
    @Override
    public ResponseEntity<?> delete(@PathVariable("id") Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
