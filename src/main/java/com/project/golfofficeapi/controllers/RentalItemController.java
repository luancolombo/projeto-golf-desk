package com.project.golfofficeapi.controllers;

import com.project.golfofficeapi.controllers.docs.RentalItemControllerDocs;
import com.project.golfofficeapi.controllers.assemblers.ResourceLinkAssembler;
import com.project.golfofficeapi.dto.RentalItemDTO;
import com.project.golfofficeapi.services.RentalItemService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rental-item")
@Tag(name = "Rental Items", description = "Endpoints for managing rentable inventory items")
public class RentalItemController implements RentalItemControllerDocs {

    private final RentalItemService service;
    private final ResourceLinkAssembler links;

    public RentalItemController(RentalItemService service, ResourceLinkAssembler links) {
        this.service = service;
        this.links = links;
    }

    @GetMapping(
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Override
    public List<RentalItemDTO> findAll() {
        return links.rentalItems(service.findAll());
    }

    @GetMapping(value = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Override
    public RentalItemDTO findById(@PathVariable("id") Long id) {
        return links.rentalItem(service.findById(id));
    }

    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Override
    public RentalItemDTO create(@Valid @RequestBody RentalItemDTO rentalItem) {
        return links.rentalItem(service.create(rentalItem));
    }

    @PutMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Override
    public RentalItemDTO update(@Valid @RequestBody RentalItemDTO rentalItem) {
        return links.rentalItem(service.update(rentalItem));
    }

    @DeleteMapping(value = "/{id}")
    @Override
    public ResponseEntity<?> delete(@PathVariable("id") Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
