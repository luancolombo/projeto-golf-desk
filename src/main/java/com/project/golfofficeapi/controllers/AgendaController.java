package com.project.golfofficeapi.controllers;

import com.project.golfofficeapi.controllers.docs.AgendaControllerDocs;
import com.project.golfofficeapi.dto.AgendaDayDTO;
import com.project.golfofficeapi.services.AgendaQueryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/agenda")
@Tag(name = "Agenda", description = "Optimized endpoints for daily agenda reads")
public class AgendaController implements AgendaControllerDocs {

    private final AgendaQueryService service;

    public AgendaController(AgendaQueryService service) {
        this.service = service;
    }

    @GetMapping(
            value = "/day",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Override
    public AgendaDayDTO findDay(
            @RequestParam("date")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date
    ) {
        return service.findDay(date);
    }
}
