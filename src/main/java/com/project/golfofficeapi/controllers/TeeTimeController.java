package com.project.golfofficeapi.controllers;

import com.project.golfofficeapi.controllers.docs.TeeTimeControllerDocs;
import com.project.golfofficeapi.dto.TeeTimeDTO;
import com.project.golfofficeapi.services.TeeTimeService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tee-time")
@Tag(name = "Tee Times", description = "Endpoints for tee time schedule slots")
public class TeeTimeController implements TeeTimeControllerDocs {

    private final TeeTimeService service;

    public TeeTimeController(TeeTimeService service) {
        this.service = service;
    }

    @GetMapping(
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Override
    public List<TeeTimeDTO> findAll() {
        return service.findAll();
    }

    @GetMapping(value = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Override
    public TeeTimeDTO findById(@PathVariable("id") Long id) {
        return service.findById(id);
    }

    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Override
    public TeeTimeDTO create(@Valid @RequestBody TeeTimeDTO teeTime) {
        return service.create(teeTime);
    }

    @PutMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Override
    public TeeTimeDTO update(@Valid @RequestBody TeeTimeDTO teeTime) {
        return service.update(teeTime);
    }

    @DeleteMapping(value = "/{id}")
    @Override
    public ResponseEntity<?> delete(@PathVariable("id") Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
