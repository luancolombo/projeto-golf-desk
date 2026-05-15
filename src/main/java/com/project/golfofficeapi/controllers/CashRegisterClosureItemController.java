package com.project.golfofficeapi.controllers;

import com.project.golfofficeapi.controllers.docs.CashRegisterClosureItemControllerDocs;
import com.project.golfofficeapi.dto.CashRegisterClosureItemDTO;
import com.project.golfofficeapi.services.CashRegisterClosureItemService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cash-register-closure-item")
@Tag(name = "Cash Register Closure Items", description = "Endpoints for cash register closing line items")
public class CashRegisterClosureItemController implements CashRegisterClosureItemControllerDocs {

    private final CashRegisterClosureItemService service;

    public CashRegisterClosureItemController(CashRegisterClosureItemService service) {
        this.service = service;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Override
    public List<CashRegisterClosureItemDTO> findAll() {
        return service.findAll();
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Override
    public CashRegisterClosureItemDTO findById(@PathVariable("id") Long id) {
        return service.findById(id);
    }

    @GetMapping(value = "/closure/{cashRegisterClosureId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Override
    public List<CashRegisterClosureItemDTO> findByCashRegisterClosureId(
            @PathVariable("cashRegisterClosureId") Long cashRegisterClosureId
    ) {
        return service.findByCashRegisterClosureId(cashRegisterClosureId);
    }

    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Override
    public CashRegisterClosureItemDTO create(@Valid @RequestBody CashRegisterClosureItemDTO item) {
        return service.create(item);
    }

    @PutMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Override
    public CashRegisterClosureItemDTO update(@Valid @RequestBody CashRegisterClosureItemDTO item) {
        return service.update(item);
    }

    @DeleteMapping(value = "/{id}")
    @Override
    public ResponseEntity<?> delete(@PathVariable("id") Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
