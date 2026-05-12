package com.project.golfofficeapi.services;

import com.project.golfofficeapi.controllers.BookingPlayerController;
import com.project.golfofficeapi.dto.BookingPlayerDTO;
import com.project.golfofficeapi.exceptions.BusinessException;
import com.project.golfofficeapi.exceptions.RequiredObjectIsNullException;
import com.project.golfofficeapi.exceptions.ResourceNotFoundException;
import com.project.golfofficeapi.model.Booking;
import com.project.golfofficeapi.model.BookingPlayer;
import com.project.golfofficeapi.model.TeeTime;
import com.project.golfofficeapi.repository.BookingPlayerRepository;
import com.project.golfofficeapi.repository.BookingRepository;
import com.project.golfofficeapi.repository.PlayerRepository;
import com.project.golfofficeapi.repository.RentalTransactionRepository;
import com.project.golfofficeapi.repository.TeeTimeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.logging.Logger;

import static com.project.golfofficeapi.mapper.ObjectMapper.parseListObject;
import static com.project.golfofficeapi.mapper.ObjectMapper.parseObject;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class BookingPlayerService {

    @Autowired
    BookingPlayerRepository repository;

    @Autowired
    BookingRepository bookingRepository;

    @Autowired
    PlayerRepository playerRepository;

    @Autowired
    TeeTimeRepository teeTimeRepository;

    @Autowired
    RentalTransactionRepository rentalTransactionRepository;

    @Autowired
    BookingStatusService bookingStatusService;

    private final Logger logger = Logger.getLogger(BookingPlayerService.class.getName());

    public BookingPlayerService(
            BookingPlayerRepository repository,
            BookingRepository bookingRepository,
            PlayerRepository playerRepository,
            TeeTimeRepository teeTimeRepository,
            RentalTransactionRepository rentalTransactionRepository,
            BookingStatusService bookingStatusService
    ) {
        this.repository = repository;
        this.bookingRepository = bookingRepository;
        this.playerRepository = playerRepository;
        this.teeTimeRepository = teeTimeRepository;
        this.rentalTransactionRepository = rentalTransactionRepository;
        this.bookingStatusService = bookingStatusService;
    }

    public List<BookingPlayerDTO> findAll() {
        logger.info("Find All Booking Players");
        var bookingPlayers = parseListObject(repository.findAll(), BookingPlayerDTO.class);
        bookingPlayers.forEach(this::addHateoasLinks);
        return bookingPlayers;
    }

    public BookingPlayerDTO findById(Long id) {
        logger.info("Find Booking Player by ID");
        var bookingPlayer = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking player not found"));
        var dto = parseObject(bookingPlayer, BookingPlayerDTO.class);
        addHateoasLinks(dto);
        return dto;
    }

    @Transactional
    public BookingPlayerDTO create(BookingPlayerDTO bookingPlayer) {
        if (bookingPlayer == null) throw new RequiredObjectIsNullException();
        logger.info("Create Booking Player");

        Booking booking = validateBooking(bookingPlayer.getBookingId());
        validatePlayer(bookingPlayer.getPlayerId());
        TeeTime teeTime = validateTeeTimeForNewPlayer(booking.getTeeTimeId());
        validateCapacity(teeTime);
        prepareNewBookingPlayer(bookingPlayer, teeTime);

        var entity = parseObject(bookingPlayer, BookingPlayer.class);
        var dto = parseObject(repository.save(entity), BookingPlayerDTO.class);
        syncBookingTotal(booking.getId());
        bookingStatusService.syncBookingStatus(booking.getId());
        syncTeeTimeOccupancy(teeTime.getId());
        addHateoasLinks(dto);
        return dto;
    }

    @Transactional
    public BookingPlayerDTO update(BookingPlayerDTO bookingPlayer) {
        if (bookingPlayer == null) throw new RequiredObjectIsNullException();
        logger.info("Update Booking Player");

        BookingPlayer entity = repository.findById(bookingPlayer.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Booking player not found"));

        Booking oldBooking = validateBooking(entity.getBookingId());
        TeeTime oldTeeTime = validateTeeTime(oldBooking.getTeeTimeId());
        Booking newBooking = validateBooking(bookingPlayer.getBookingId());
        validatePlayer(bookingPlayer.getPlayerId());
        TeeTime newTeeTime = validateTeeTimeForUpdate(newBooking.getTeeTimeId(), oldTeeTime.getId());

        if (!oldTeeTime.getId().equals(newTeeTime.getId())) {
            validateCapacity(newTeeTime);
        }

        entity.setBookingId(bookingPlayer.getBookingId());
        entity.setPlayerId(bookingPlayer.getPlayerId());
        entity.setGreenFeeAmount(resolveGreenFeeAmount(bookingPlayer.getGreenFeeAmount(), newTeeTime));
        entity.setCheckedIn(resolveCheckedIn(bookingPlayer.getCheckedIn()));
        var dto = parseObject(repository.save(entity), BookingPlayerDTO.class);

        syncBookingTotal(oldBooking.getId());
        syncBookingTotal(newBooking.getId());
        bookingStatusService.syncBookingStatus(oldBooking.getId());
        bookingStatusService.syncBookingStatus(newBooking.getId());
        syncTeeTimeOccupancy(oldTeeTime.getId());
        syncTeeTimeOccupancy(newTeeTime.getId());
        addHateoasLinks(dto);
        return dto;
    }

    @Transactional
    public void delete(Long id) {
        logger.info("Delete Booking Player");
        BookingPlayer entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking player not found"));
        Booking booking = findBooking(entity.getBookingId());
        TeeTime teeTime = validateTeeTime(booking.getTeeTimeId());

        if (!rentalTransactionRepository.findByBookingPlayerId(entity.getId()).isEmpty()) {
            throw new BusinessException("Cannot remove player with rental items. Remove rental items first");
        }

        repository.delete(entity);
        syncBookingTotal(booking.getId());
        bookingStatusService.syncBookingStatus(booking.getId());
        syncTeeTimeOccupancy(teeTime.getId());
    }

    private Booking findBooking(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
    }

    private Booking validateBooking(Long bookingId) {
        Booking booking = findBooking(bookingId);

        if ("CANCELLED".equalsIgnoreCase(booking.getStatus())) {
            throw new BusinessException("Cannot add players to a cancelled booking");
        }

        return booking;
    }

    private void validatePlayer(Long playerId) {
        if (!playerRepository.existsById(playerId)) {
            throw new ResourceNotFoundException("Player not found");
        }
    }

    private TeeTime validateTeeTimeForNewPlayer(Long teeTimeId) {
        TeeTime teeTime = validateTeeTime(teeTimeId);

        if ("CANCELLED".equalsIgnoreCase(teeTime.getStatus())) {
            throw new BusinessException("Cannot add players to a cancelled tee time");
        }

        return teeTime;
    }

    private TeeTime validateTeeTimeForUpdate(Long teeTimeId, Long oldTeeTimeId) {
        TeeTime teeTime = validateTeeTime(teeTimeId);

        if (teeTime.getId().equals(oldTeeTimeId)) {
            return teeTime;
        }

        if ("CANCELLED".equalsIgnoreCase(teeTime.getStatus())) {
            throw new BusinessException("Cannot move player to a cancelled tee time");
        }

        return teeTime;
    }

    private TeeTime validateTeeTime(Long teeTimeId) {
        return teeTimeRepository.findById(teeTimeId)
                .orElseThrow(() -> new ResourceNotFoundException("Tee time not found"));
    }

    private void validateCapacity(TeeTime teeTime) {
        long bookedPlayers = repository.countByTeeTimeId(teeTime.getId());

        if (bookedPlayers >= teeTime.getMaxPlayers()) {
            throw new BusinessException("Tee time is full");
        }
    }

    private void prepareNewBookingPlayer(BookingPlayerDTO bookingPlayer, TeeTime teeTime) {
        bookingPlayer.setGreenFeeAmount(resolveGreenFeeAmount(bookingPlayer.getGreenFeeAmount(), teeTime));
        bookingPlayer.setCheckedIn(resolveCheckedIn(bookingPlayer.getCheckedIn()));
    }

    private BigDecimal resolveGreenFeeAmount(BigDecimal greenFeeAmount, TeeTime teeTime) {
        if (greenFeeAmount == null) {
            return teeTime.getBaseGreenFee();
        }

        return greenFeeAmount;
    }

    private Boolean resolveCheckedIn(Boolean checkedIn) {
        if (checkedIn == null) {
            return false;
        }

        return checkedIn;
    }

    private void syncBookingTotal(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
        BigDecimal greenFeeTotal = repository.sumGreenFeeAmountByBookingId(bookingId);
        BigDecimal rentalTotal = rentalTransactionRepository.sumTotalPriceByBookingId(bookingId);
        booking.setTotalAmount(greenFeeTotal.add(rentalTotal));
        bookingRepository.save(booking);
    }

    private void syncTeeTimeOccupancy(Long teeTimeId) {
        TeeTime teeTime = validateTeeTime(teeTimeId);
        int bookedPlayers = Math.toIntExact(repository.countByTeeTimeId(teeTimeId));

        teeTime.setBookedPlayers(bookedPlayers);

        if (!"CANCELLED".equalsIgnoreCase(teeTime.getStatus())) {
            if (bookedPlayers >= teeTime.getMaxPlayers()) {
                teeTime.setStatus("FULL");
            } else {
                teeTime.setStatus("AVAILABLE");
            }
        }

        teeTimeRepository.save(teeTime);
    }

    private void addHateoasLinks(BookingPlayerDTO dto) {
        dto.add(linkTo(methodOn(BookingPlayerController.class).findById(dto.getId())).withSelfRel().withType("GET"));
        dto.add(linkTo(methodOn(BookingPlayerController.class).findAll()).withRel("findAll").withType("GET"));
        dto.add(linkTo(methodOn(BookingPlayerController.class).create(dto)).withRel("create").withType("POST"));
        dto.add(linkTo(methodOn(BookingPlayerController.class).update(dto)).withRel("update").withType("PUT"));
        dto.add(linkTo(methodOn(BookingPlayerController.class).delete(dto.getId())).withRel("delete").withType("DELETE"));
    }
}
