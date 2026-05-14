package com.project.golfofficeapi.services;

import com.project.golfofficeapi.controllers.CheckInTicketController;
import com.project.golfofficeapi.dto.CheckInTicketDTO;
import com.project.golfofficeapi.exceptions.BusinessException;
import com.project.golfofficeapi.exceptions.RequiredObjectIsNullException;
import com.project.golfofficeapi.exceptions.ResourceNotFoundException;
import com.project.golfofficeapi.mapper.custom.CheckInTicketMapper;
import com.project.golfofficeapi.model.Booking;
import com.project.golfofficeapi.model.BookingPlayer;
import com.project.golfofficeapi.model.CheckInTicket;
import com.project.golfofficeapi.model.Player;
import com.project.golfofficeapi.model.TeeTime;
import com.project.golfofficeapi.repository.BookingPlayerRepository;
import com.project.golfofficeapi.repository.BookingRepository;
import com.project.golfofficeapi.repository.CheckInTicketRepository;
import com.project.golfofficeapi.repository.PlayerRepository;
import com.project.golfofficeapi.repository.TeeTimeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class CheckInTicketService {

    private static final String STARTING_TEE = "TEE 1";
    private static final String CROSSING_TEE = "TEE 10";
    private final CheckInTicketRepository repository;
    private final BookingPlayerRepository bookingPlayerRepository;
    private final BookingRepository bookingRepository;
    private final PlayerRepository playerRepository;
    private final TeeTimeRepository teeTimeRepository;
    private final CheckInTicketMapper mapper;
    private final Logger logger = Logger.getLogger(CheckInTicketService.class.getName());

    public CheckInTicketService(
            CheckInTicketRepository repository,
            BookingPlayerRepository bookingPlayerRepository,
            BookingRepository bookingRepository,
            PlayerRepository playerRepository,
            TeeTimeRepository teeTimeRepository,
            CheckInTicketMapper mapper
    ) {
        this.repository = repository;
        this.bookingPlayerRepository = bookingPlayerRepository;
        this.bookingRepository = bookingRepository;
        this.playerRepository = playerRepository;
        this.teeTimeRepository = teeTimeRepository;
        this.mapper = mapper;
    }

    public List<CheckInTicketDTO> findAll() {
        logger.info("Find All Check-in Tickets");
        var tickets = mapper.toDTOList(repository.findAll());
        tickets.forEach(this::addHateoasLinks);
        return tickets;
    }

    public CheckInTicketDTO findById(Long id) {
        logger.info("Find Check-in Ticket by ID");
        CheckInTicket ticket = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Check-in ticket not found"));
        var dto = mapper.toDTO(ticket);
        addHateoasLinks(dto);
        return dto;
    }

    public List<CheckInTicketDTO> findByBookingPlayerId(Long bookingPlayerId) {
        logger.info("Find Check-in Tickets by Booking Player ID");
        findBookingPlayer(bookingPlayerId);
        var tickets = mapper.toDTOList(repository.findByBookingPlayerId(bookingPlayerId));
        tickets.forEach(this::addHateoasLinks);
        return tickets;
    }

    @Transactional
    public CheckInTicketDTO create(CheckInTicketDTO ticket) {
        if (ticket == null) throw new RequiredObjectIsNullException();
        logger.info("Create Check-in Ticket");
        return issueByBookingPlayerId(ticket.getBookingPlayerId());
    }

    @Transactional
    public CheckInTicketDTO issueByBookingPlayerId(Long bookingPlayerId) {
        logger.info("Issue Check-in Ticket by Booking Player ID");
        BookingPlayer bookingPlayer = findBookingPlayer(bookingPlayerId);
        return issueForBookingPlayer(bookingPlayer);
    }

    @Transactional
    public CheckInTicketDTO cancel(Long id, String reason) {
        logger.info("Cancel Check-in Ticket");
        CheckInTicket ticket = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Check-in ticket not found"));
        cancelTicketEntity(ticket, resolveCancellationReason(reason));
        var dto = mapper.toDTO(repository.save(ticket));
        addHateoasLinks(dto);
        return dto;
    }

    @Transactional
    public void delete(Long id) {
        logger.info("Cancel Check-in Ticket by delete request");
        CheckInTicket ticket = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Check-in ticket not found"));
        cancelTicketEntity(ticket, "Check-in ticket cancelled by delete request");
        repository.save(ticket);
    }

    @Transactional
    public void syncTicketForBookingPlayer(BookingPlayer bookingPlayer) {
        if (bookingPlayer == null || bookingPlayer.getId() == null) {
            return;
        }

        if (Boolean.TRUE.equals(bookingPlayer.getCheckedIn())) {
            issueForBookingPlayer(bookingPlayer);
            return;
        }

        cancelActiveTicketByBookingPlayerId(bookingPlayer.getId(), "Check-in was undone");
    }

    private CheckInTicketDTO issueForBookingPlayer(BookingPlayer bookingPlayer) {
        if (!Boolean.TRUE.equals(bookingPlayer.getCheckedIn())) {
            throw new BusinessException("Booking player must be checked in to issue a ticket");
        }

        Booking booking = findBooking(bookingPlayer.getBookingId());
        TeeTime teeTime = findTeeTime(booking.getTeeTimeId());
        validateTeeTimeIsNotInPast(teeTime);

        var activeTicket = repository.findFirstByBookingPlayer_IdAndCancelledFalseOrderByIdDesc(bookingPlayer.getId());
        if (activeTicket.isPresent()) {
            var dto = mapper.toDTO(activeTicket.get());
            addHateoasLinks(dto);
            return dto;
        }

        Player player = findPlayer(bookingPlayer.getPlayerId());

        CheckInTicket ticket = new CheckInTicket();
        ticket.setTicketNumber(generateTicketNumber());
        ticket.setBookingPlayer(bookingPlayer);
        ticket.setPlayerNameSnapshot(player.getFullName());
        ticket.setPlayerCountSnapshot(resolvePlayerCount(bookingPlayer));
        ticket.setBookingCodeSnapshot(booking.getCode());
        ticket.setPlayDate(teeTime.getPlayDate());
        ticket.setStartTime(teeTime.getStartTime());
        ticket.setStartingTee(STARTING_TEE);
        ticket.setCrossingTee(CROSSING_TEE);
        ticket.setCrossingTime(teeTime.getStartTime().plusHours(2).plusMinutes(15));
        ticket.setIssuedAt(LocalDateTime.now());
        ticket.setCancelled(false);
        ticket.setCancelledAt(null);
        ticket.setCancellationReason(null);

        var dto = mapper.toDTO(repository.save(ticket));
        addHateoasLinks(dto);
        return dto;
    }

    private void cancelActiveTicketByBookingPlayerId(Long bookingPlayerId, String reason) {
        repository.findFirstByBookingPlayer_IdAndCancelledFalseOrderByIdDesc(bookingPlayerId)
                .ifPresent(ticket -> {
                    cancelTicketEntity(ticket, reason);
                    repository.save(ticket);
                });
    }

    private void cancelTicketEntity(CheckInTicket ticket, String reason) {
        if (Boolean.TRUE.equals(ticket.getCancelled())) {
            return;
        }

        ticket.setCancelled(true);
        ticket.setCancelledAt(LocalDateTime.now());
        ticket.setCancellationReason(reason);
    }

    private BookingPlayer findBookingPlayer(Long bookingPlayerId) {
        return bookingPlayerRepository.findById(bookingPlayerId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking player not found"));
    }

    private Booking findBooking(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
    }

    private Player findPlayer(Long playerId) {
        return playerRepository.findById(playerId)
                .orElseThrow(() -> new ResourceNotFoundException("Player not found"));
    }

    private TeeTime findTeeTime(Long teeTimeId) {
        return teeTimeRepository.findById(teeTimeId)
                .orElseThrow(() -> new ResourceNotFoundException("Tee time not found"));
    }

    private void validateTeeTimeIsNotInPast(TeeTime teeTime) {
        if (teeTime.getPlayDate() != null && teeTime.getPlayDate().isBefore(LocalDate.now())) {
            throw new BusinessException("Cannot issue check-in ticket for a past tee time");
        }
    }

    private String generateTicketNumber() {
        String prefix = "CT-" + LocalDate.now().getYear() + "-";
        int nextSequence = repository.findByTicketNumberStartingWith(prefix).stream()
                .map(CheckInTicket::getTicketNumber)
                .map(number -> extractTicketSequence(prefix, number))
                .filter(Objects::nonNull)
                .max(Integer::compareTo)
                .orElse(0) + 1;

        String candidate = prefix + String.format("%06d", nextSequence);
        while (repository.existsByTicketNumber(candidate)) {
            nextSequence++;
            candidate = prefix + String.format("%06d", nextSequence);
        }

        return candidate;
    }

    private int resolvePlayerCount(BookingPlayer bookingPlayer) {
        return bookingPlayer.getPlayerCount() == null ? 1 : bookingPlayer.getPlayerCount();
    }

    private Integer extractTicketSequence(String prefix, String ticketNumber) {
        if (ticketNumber == null || !ticketNumber.startsWith(prefix)) {
            return null;
        }

        try {
            return Integer.parseInt(ticketNumber.substring(prefix.length()));
        } catch (NumberFormatException exception) {
            return null;
        }
    }

    private String resolveCancellationReason(String reason) {
        if (reason == null || reason.isBlank()) {
            return "Check-in ticket cancelled";
        }

        return reason.trim();
    }

    private void addHateoasLinks(CheckInTicketDTO dto) {
        dto.add(linkTo(methodOn(CheckInTicketController.class).findById(dto.getId())).withSelfRel().withType("GET"));
        dto.add(linkTo(methodOn(CheckInTicketController.class).findAll()).withRel("findAll").withType("GET"));
        dto.add(linkTo(methodOn(CheckInTicketController.class).findByBookingPlayerId(dto.getBookingPlayerId())).withRel("findByBookingPlayer").withType("GET"));
        dto.add(linkTo(methodOn(CheckInTicketController.class).issueByBookingPlayerId(dto.getBookingPlayerId())).withRel("issueByBookingPlayer").withType("POST"));
        dto.add(linkTo(methodOn(CheckInTicketController.class).create(dto)).withRel("create").withType("POST"));
        dto.add(linkTo(methodOn(CheckInTicketController.class).cancel(dto.getId(), dto.getCancellationReason())).withRel("cancel").withType("PUT"));
        dto.add(linkTo(methodOn(CheckInTicketController.class).delete(dto.getId())).withRel("delete").withType("DELETE"));
    }
}
