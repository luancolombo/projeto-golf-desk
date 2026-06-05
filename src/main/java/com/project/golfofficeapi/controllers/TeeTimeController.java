package com.project.golfofficeapi.controllers;

import com.project.golfofficeapi.controllers.docs.TeeTimeControllerDocs;
import com.project.golfofficeapi.controllers.assemblers.ResourceLinkAssembler;
import com.project.golfofficeapi.dto.TeeTimeDTO;
import com.project.golfofficeapi.services.TeeTimeService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tee-time")
@Tag(name = "Tee Times", description = "Endpoints for tee time schedule slots")
public class TeeTimeController implements TeeTimeControllerDocs {

    private final TeeTimeService service;
    private final ResourceLinkAssembler links;

    public TeeTimeController(TeeTimeService service, ResourceLinkAssembler links) {
        this.service = service;
        this.links = links;
    }

    @GetMapping(
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Override
    public Page<TeeTimeDTO> findAll(Pageable pageable) {
        return links.teeTimes(service.findAll(pageable));
    }

    @GetMapping(value = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Override
    public TeeTimeDTO findById(@PathVariable("id") Long id) {
        return links.teeTime(service.findById(id));
    }

    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Override
    public TeeTimeDTO create(@Valid @RequestBody TeeTimeDTO teeTime) {
        return links.teeTime(service.create(teeTime));
    }

    @PutMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Override
    public TeeTimeDTO update(@Valid @RequestBody TeeTimeDTO teeTime) {
        return links.teeTime(service.update(teeTime));
    }

    @DeleteMapping(value = "/{id}")
    @Override
    public ResponseEntity<?> delete(@PathVariable("id") Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
