package com.project.golfofficeapi.services;

import com.project.golfofficeapi.controllers.PlayerController;
import com.project.golfofficeapi.dto.PlayerDTO;
import com.project.golfofficeapi.exceptions.BusinessException;
import com.project.golfofficeapi.exceptions.RequiredObjectIsNullException;
import com.project.golfofficeapi.exceptions.ResourceNotFoundException;
import com.project.golfofficeapi.model.Player;
import com.project.golfofficeapi.repository.BookingPlayerRepository;
import com.project.golfofficeapi.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.logging.Logger;

import static com.project.golfofficeapi.mapper.ObjectMapper.parseListObject;
import static com.project.golfofficeapi.mapper.ObjectMapper.parseObject;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class PlayerService {

    @Autowired
    PlayerRepository repository;

    @Autowired
    BookingPlayerRepository bookingPlayerRepository;

    private final Logger logger =  Logger.getLogger(PlayerService.class.getName());

    public PlayerService(PlayerRepository repository, BookingPlayerRepository bookingPlayerRepository) {
        this.repository = repository;
        this.bookingPlayerRepository = bookingPlayerRepository;
    }

    public List<PlayerDTO> findAll() {
        logger.info("Find All Players");
        var players = parseListObject(repository.findAll(), PlayerDTO.class);
        players.forEach(this::addHateoasLinks);
        return players;
    }
    public PlayerDTO findById(Long id) {
        logger.info("Find Player by ID");
        var player = repository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Player not found"));
        var dto = parseObject(player, PlayerDTO.class);
        addHateoasLinks(dto);
        return dto;
    }

    public List<PlayerDTO> findByName(String name) {
        logger.info("Find Players by name");
        var players = parseListObject(repository.findByFullNameContainingIgnoreCase(name), PlayerDTO.class);
        players.forEach(this::addHateoasLinks);
        return players;
    }

    public PlayerDTO create(PlayerDTO player) {
        if (player == null) throw new RequiredObjectIsNullException();
        logger.info("Create Player");
        if (repository.existsByTaxNumber(player.getTaxNumber())) {
            throw new BusinessException("Tax number already registered");
        }

        if (repository.existsByEmail(player.getEmail())) {
            throw new BusinessException("Email already registered");
        }

        if (repository.existsByPhone(player.getPhone())) {
            throw new BusinessException("Phone already registered");
        }
        var entity = parseObject(player, Player.class);
        var dto = parseObject(repository.save(entity), PlayerDTO.class);
        addHateoasLinks(dto);
        return dto;
    }
    public PlayerDTO update(PlayerDTO player) {
        if (player == null) throw new RequiredObjectIsNullException();
        logger.info("Update Player");
        Player entity = repository.findById(player.getId())
                .orElseThrow(()-> new ResourceNotFoundException("Player not found"));
        if (repository.existsByTaxNumberAndIdNot(player.getTaxNumber(), player.getId())) {
            throw new BusinessException("Tax number already registered");
        }
        if (repository.existsByEmailAndIdNot(player.getEmail(), player.getId())) {
            throw new BusinessException("Email already registered");
        }

        if (repository.existsByPhoneAndIdNot(player.getPhone(), player.getId())) {
            throw new BusinessException("Phone already registered");
        }
        entity.setFullName(player.getFullName());
        entity.setTaxNumber(player.getTaxNumber());
        entity.setEmail(player.getEmail());
        entity.setPhone(player.getPhone());
        entity.setHandCap(player.getHandCap());
        entity.setMember(player.isMember());
        entity.setNotes(player.getNotes());
        var dto =  parseObject(repository.save(entity), PlayerDTO.class);
        addHateoasLinks(dto);
        return dto;
    }
    public void delete(Long id) {
        logger.info("Delete Player");
        Player entity = repository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Player not found"));

        if (bookingPlayerRepository.existsByPlayerId(entity.getId())) {
            throw new BusinessException("Cannot delete player with booking history");
        }

        repository.delete(entity);
    }

    private void addHateoasLinks(PlayerDTO dto) {
        dto.add(linkTo(methodOn(PlayerController.class).findById(dto.getId())).withSelfRel().withType("GET"));
        dto.add(linkTo(methodOn(PlayerController.class).findAll()).withRel("findAll").withType("GET"));
        dto.add(linkTo(methodOn(PlayerController.class).findByName(dto.getFullName())).withRel("findByName").withType("GET"));
        dto.add(linkTo(methodOn(PlayerController.class).create(dto)).withRel("create").withType("POST"));
        dto.add(linkTo(methodOn(PlayerController.class).update(dto)).withRel("update").withType("PUT"));
        dto.add(linkTo(methodOn(PlayerController.class).delete(dto.getId())).withRel("delete").withType("DELETE"));
    }

}
