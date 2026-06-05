package com.project.golfofficeapi.controllers;

import com.project.golfofficeapi.controllers.docs.BookingControllerDocs;
import com.project.golfofficeapi.controllers.assemblers.ResourceLinkAssembler;
import com.project.golfofficeapi.dto.BookingDTO;
import com.project.golfofficeapi.services.BookingService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/booking")
@Tag(name = "Bookings", description = "Endpoints for tee time bookings")
public class BookingController implements BookingControllerDocs {

    private final BookingService service;
    private final ResourceLinkAssembler links;

    public BookingController(BookingService service, ResourceLinkAssembler links) {
        this.service = service;
        this.links = links;
    }

    @GetMapping(
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Override
    public List<BookingDTO> findAll() {
        return links.bookings(service.findAll());
    }

    @GetMapping(value = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Override
    public BookingDTO findById(@PathVariable("id") Long id) {
        return links.booking(service.findById(id));
    }

    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Override
    public BookingDTO create(@Valid @RequestBody BookingDTO booking) {
        return links.booking(service.create(booking));
    }

    @PutMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Override
    public BookingDTO update(@Valid @RequestBody BookingDTO booking) {
        return links.booking(service.update(booking));
    }

    @DeleteMapping(value = "/{id}")
    @Override
    public ResponseEntity<?> delete(@PathVariable("id") Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
