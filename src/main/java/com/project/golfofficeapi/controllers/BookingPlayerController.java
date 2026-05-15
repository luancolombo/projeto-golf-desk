package com.project.golfofficeapi.controllers;

import com.project.golfofficeapi.controllers.docs.BookingPlayerControllerDocs;
import com.project.golfofficeapi.dto.BookingPlayerDTO;
import com.project.golfofficeapi.services.BookingPlayerService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/booking-player")
@Tag(name = "Booking Players", description = "Endpoints for players and groups inside bookings")
public class BookingPlayerController implements BookingPlayerControllerDocs {

    private final BookingPlayerService service;

    public BookingPlayerController(BookingPlayerService service) {
        this.service = service;
    }

    @GetMapping(
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Override
    public List<BookingPlayerDTO> findAll() {
        return service.findAll();
    }

    @GetMapping(value = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Override
    public BookingPlayerDTO findById(@PathVariable("id") Long id) {
        return service.findById(id);
    }

    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Override
    public BookingPlayerDTO create(@Valid @RequestBody BookingPlayerDTO bookingPlayer) {
        return service.create(bookingPlayer);
    }

    @PutMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Override
    public BookingPlayerDTO update(@Valid @RequestBody BookingPlayerDTO bookingPlayer) {
        return service.update(bookingPlayer);
    }

    @DeleteMapping(value = "/{id}")
    @Override
    public ResponseEntity<?> delete(@PathVariable("id") Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
