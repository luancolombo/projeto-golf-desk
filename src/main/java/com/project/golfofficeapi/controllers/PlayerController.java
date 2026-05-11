package com.project.golfofficeapi.controllers;

import com.project.golfofficeapi.dto.PlayerDTO;
import com.project.golfofficeapi.services.PlayerService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/player")
public class PlayerController {

    private final PlayerService service;

    public PlayerController(PlayerService service) {
        this.service = service;
    }
    @GetMapping(
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public List<PlayerDTO> findAll(){
        return service.findAll();
    }
    @GetMapping(value = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public PlayerDTO findById(@PathVariable("id") Long id){
        return  service.findById(id);

    }
    @GetMapping(value = "/search",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public List<PlayerDTO> findByName(@RequestParam("name") String name){
        return service.findByName(name);
    }
    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public PlayerDTO create(@Valid @RequestBody PlayerDTO player){
        return service.create(player);
    }
    @PutMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public PlayerDTO update(@Valid @RequestBody PlayerDTO player){
        return service.update(player);
    }
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Long id){
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
