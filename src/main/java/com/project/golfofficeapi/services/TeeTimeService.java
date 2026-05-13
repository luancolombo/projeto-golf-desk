package com.project.golfofficeapi.services;

import com.project.golfofficeapi.controllers.TeeTimeController;
import com.project.golfofficeapi.dto.TeeTimeDTO;
import com.project.golfofficeapi.enums.TeeTimeStatus;
import com.project.golfofficeapi.exceptions.BusinessException;
import com.project.golfofficeapi.exceptions.RequiredObjectIsNullException;
import com.project.golfofficeapi.exceptions.ResourceNotFoundException;
import com.project.golfofficeapi.mapper.custom.TeeTimeMapper;
import com.project.golfofficeapi.model.TeeTime;
import com.project.golfofficeapi.repository.BookingPlayerRepository;
import com.project.golfofficeapi.repository.BookingRepository;
import com.project.golfofficeapi.repository.TeeTimeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.util.List;
import java.util.logging.Logger;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class TeeTimeService {

    @Autowired
    TeeTimeRepository repository;

    @Autowired
    BookingRepository bookingRepository;

    @Autowired
    BookingPlayerRepository bookingPlayerRepository;

    @Autowired
    TeeTimeMapper mapper;

    private final Logger logger = Logger.getLogger(TeeTimeService.class.getName());

    public TeeTimeService(
            TeeTimeRepository repository,
            BookingRepository bookingRepository,
            BookingPlayerRepository bookingPlayerRepository,
            TeeTimeMapper mapper
    ) {
        this.repository = repository;
        this.bookingRepository = bookingRepository;
        this.bookingPlayerRepository = bookingPlayerRepository;
        this.mapper = mapper;
    }

    public List<TeeTimeDTO> findAll() {
        logger.info("Find All Tee Times");
        var teeTimes = mapper.toDTOList(repository.findAll());
        teeTimes.forEach(this::addHateoasLinks);
        return teeTimes;
    }

    public TeeTimeDTO findById(Long id) {
        logger.info("Find Tee Time by ID");
        var teeTime = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tee time not found"));
        var dto = mapper.toDTO(teeTime);
        addHateoasLinks(dto);
        return dto;
    }

    public TeeTimeDTO create(TeeTimeDTO teeTime) {
        if (teeTime == null) throw new RequiredObjectIsNullException();
        logger.info("Create Tee Time");
        if (teeTime.getMaxPlayers() == null) {
            teeTime.setMaxPlayers(4);
        }
        teeTime.setBaseGreenFee(
                calculateBaseGreenFee(teeTime.getPlayDate(), teeTime.getStartTime())
        );
        teeTime.setStatus(resolveStatus(teeTime.getStatus()).name());
        validatePlayerCapacity(teeTime);

        if (repository.existsByPlayDateAndStartTime(teeTime.getPlayDate(), teeTime.getStartTime())) {
            throw new BusinessException("Tee time already registered for this date and start time");
        }

        var entity = mapper.toEntity(teeTime);
        var dto = mapper.toDTO(repository.save(entity));
        addHateoasLinks(dto);
        return dto;
    }

    public TeeTimeDTO update(TeeTimeDTO teeTime) {
        if (teeTime == null) throw new RequiredObjectIsNullException();
        logger.info("Update Tee Time");
        if (teeTime.getMaxPlayers() == null) {
            teeTime.setMaxPlayers(4);
        }
        teeTime.setBaseGreenFee(
                calculateBaseGreenFee(teeTime.getPlayDate(), teeTime.getStartTime())
        );
        TeeTimeStatus status = resolveStatus(teeTime.getStatus());
        validatePlayerCapacity(teeTime);

        TeeTime entity = repository.findById(teeTime.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Tee time not found"));

        if (repository.existsByPlayDateAndStartTimeAndIdNot(
                teeTime.getPlayDate(),
                teeTime.getStartTime(),
                teeTime.getId()
        )) {
            throw new BusinessException("Tee time already registered for this date and start time");
        }

        entity.setPlayDate(teeTime.getPlayDate());
        entity.setStartTime(teeTime.getStartTime());
        entity.setMaxPlayers(teeTime.getMaxPlayers());
        entity.setBookedPlayers(teeTime.getBookedPlayers());
        entity.setStatus(status);
        entity.setBaseGreenFee(teeTime.getBaseGreenFee());
        var dto = mapper.toDTO(repository.save(entity));
        addHateoasLinks(dto);
        return dto;
    }

    public void delete(Long id) {
        logger.info("Delete Tee Time");
        TeeTime entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tee time not found"));

        if (bookingRepository.existsByTeeTimeId(entity.getId())) {
            entity.setStatus(TeeTimeStatus.CANCELLED);
            entity.setBookedPlayers(Math.toIntExact(bookingPlayerRepository.countByTeeTimeId(entity.getId())));
            repository.save(entity);
            return;
        }

        repository.delete(entity);
    }

    private void validatePlayerCapacity(TeeTimeDTO teeTime) {
        if (teeTime.getBookedPlayers() != null
                && teeTime.getMaxPlayers() != null
                && teeTime.getBookedPlayers() > teeTime.getMaxPlayers()) {
            throw new BusinessException("Booked players cannot be greater than max players");
        }
    }

    private TeeTimeStatus resolveStatus(String status) {
        try {
            return TeeTimeStatus.fromString(status);
        } catch (IllegalArgumentException exception) {
            throw new BusinessException("Invalid tee time status");
        }
    }

    private BigDecimal calculateBaseGreenFee(LocalDate playDate, LocalTime startTime) {
        if (!startTime.isBefore(LocalTime.of(16, 0))) {
            return new BigDecimal("35.00");
        }

        if (isHighSeason(playDate)) {
            return new BigDecimal("80.00");
        }

        return new BigDecimal("50.00");
    }

    private boolean isHighSeason(LocalDate playDate) {
        Month month = playDate.getMonth();

        return month == Month.APRIL
                || month == Month.MAY
                || month == Month.SEPTEMBER
                || month == Month.OCTOBER;
    }

    private void addHateoasLinks(TeeTimeDTO dto) {
        dto.add(linkTo(methodOn(TeeTimeController.class).findById(dto.getId())).withSelfRel().withType("GET"));
        dto.add(linkTo(methodOn(TeeTimeController.class).findAll()).withRel("findAll").withType("GET"));
        dto.add(linkTo(methodOn(TeeTimeController.class).create(dto)).withRel("create").withType("POST"));
        dto.add(linkTo(methodOn(TeeTimeController.class).update(dto)).withRel("update").withType("PUT"));
        dto.add(linkTo(methodOn(TeeTimeController.class).delete(dto.getId())).withRel("delete").withType("DELETE"));
    }
}
