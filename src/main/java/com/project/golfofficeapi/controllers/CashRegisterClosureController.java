package com.project.golfofficeapi.controllers;

import com.project.golfofficeapi.controllers.docs.CashRegisterClosureControllerDocs;
import com.project.golfofficeapi.dto.CashRegisterClosureDTO;
import com.project.golfofficeapi.services.CashRegisterClosureService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/cash-register-closure")
@Tag(name = "Cash Register Closures", description = "Endpoints for daily cash register preview and closing")
public class CashRegisterClosureController implements CashRegisterClosureControllerDocs {

    private final CashRegisterClosureService service;

    public CashRegisterClosureController(CashRegisterClosureService service) {
        this.service = service;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Override
    public List<CashRegisterClosureDTO> findAll() {
        return service.findAll();
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Override
    public CashRegisterClosureDTO findById(@PathVariable("id") Long id) {
        return service.findById(id);
    }

    @GetMapping(value = "/date/{businessDate}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Override
    public CashRegisterClosureDTO findByBusinessDate(
            @PathVariable("businessDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate businessDate
    ) {
        return service.findByBusinessDate(businessDate);
    }

    @GetMapping(value = "/preview", produces = MediaType.APPLICATION_JSON_VALUE)
    @Override
    public CashRegisterClosureDTO preview(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate businessDate
    ) {
        return service.preview(businessDate);
    }

    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Override
    public CashRegisterClosureDTO create(@Valid @RequestBody CashRegisterClosureDTO closure) {
        return service.create(closure);
    }

    @PostMapping(
            value = "/close",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Override
    public CashRegisterClosureDTO close(@Valid @RequestBody CashRegisterClosureDTO closure) {
        return service.close(closure);
    }

    @PutMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Override
    public CashRegisterClosureDTO update(@Valid @RequestBody CashRegisterClosureDTO closure) {
        return service.update(closure);
    }

    @DeleteMapping(value = "/{id}")
    @Override
    public ResponseEntity<?> delete(@PathVariable("id") Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
