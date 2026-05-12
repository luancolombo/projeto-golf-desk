package com.project.golfofficeapi.services;

import com.project.golfofficeapi.controllers.BookingController;
import com.project.golfofficeapi.dto.BookingDTO;
import com.project.golfofficeapi.exceptions.BusinessException;
import com.project.golfofficeapi.exceptions.RequiredObjectIsNullException;
import com.project.golfofficeapi.exceptions.ResourceNotFoundException;
import com.project.golfofficeapi.mapper.custom.BookingMapper;
import com.project.golfofficeapi.model.Booking;
import com.project.golfofficeapi.model.TeeTime;
import com.project.golfofficeapi.repository.BookingPlayerRepository;
import com.project.golfofficeapi.repository.BookingRepository;
import com.project.golfofficeapi.repository.PaymentRepository;
import com.project.golfofficeapi.repository.ReceiptRepository;
import com.project.golfofficeapi.repository.RentalTransactionRepository;
import com.project.golfofficeapi.repository.TeeTimeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class BookingService {

    @Autowired
    BookingRepository repository;

    @Autowired
    TeeTimeRepository teeTimeRepository;

    @Autowired
    BookingPlayerRepository bookingPlayerRepository;

    @Autowired
    RentalTransactionRepository rentalTransactionRepository;

    @Autowired
    PaymentRepository paymentRepository;

    @Autowired
    ReceiptRepository receiptRepository;

    @Autowired
    BookingMapper mapper;

    private final Logger logger = Logger.getLogger(BookingService.class.getName());

    public BookingService(
            BookingRepository repository,
            TeeTimeRepository teeTimeRepository,
            BookingPlayerRepository bookingPlayerRepository,
            RentalTransactionRepository rentalTransactionRepository,
            PaymentRepository paymentRepository,
            ReceiptRepository receiptRepository,
            BookingMapper mapper
    ) {
        this.repository = repository;
        this.teeTimeRepository = teeTimeRepository;
        this.bookingPlayerRepository = bookingPlayerRepository;
        this.rentalTransactionRepository = rentalTransactionRepository;
        this.paymentRepository = paymentRepository;
        this.receiptRepository = receiptRepository;
        this.mapper = mapper;
    }

    public List<BookingDTO> findAll() {
        logger.info("Find All Bookings");
        var bookings = mapper.toDTOList(repository.findAll());
        bookings.forEach(this::addHateoasLinks);
        return bookings;
    }

    public BookingDTO findById(Long id) {
        logger.info("Find Booking by ID");
        var booking = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
        var dto = mapper.toDTO(booking);
        addHateoasLinks(dto);
        return dto;
    }

    public BookingDTO create(BookingDTO booking) {
        if (booking == null) throw new RequiredObjectIsNullException();
        logger.info("Create Booking");
        TeeTime teeTime = validateTeeTime(booking.getTeeTimeId());
        prepareNewBooking(booking);

        var entity = mapper.toEntity(booking, teeTime);
        var dto = mapper.toDTO(repository.save(entity));
        addHateoasLinks(dto);
        return dto;
    }

    public BookingDTO update(BookingDTO booking) {
        if (booking == null) throw new RequiredObjectIsNullException();
        logger.info("Update Booking");
        TeeTime teeTime = validateTeeTime(booking.getTeeTimeId());

        Booking entity = repository.findById(booking.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        if (booking.getCode() != null
                && !booking.getCode().isBlank()
                && repository.existsByCodeAndIdNot(booking.getCode(), booking.getId())) {
            throw new BusinessException("Booking code already registered");
        }

        entity.setCode(resolveCodeForUpdate(booking, entity));
        entity.setStatus(booking.getStatus());
        entity.setTotalAmount(resolveTotalAmount(booking.getTotalAmount()));
        entity.setCreatedBy(booking.getCreatedBy());
        entity.setTeeTime(teeTime);
        var dto = mapper.toDTO(repository.save(entity));
        addHateoasLinks(dto);
        return dto;
    }

    @Transactional
    public void delete(Long id) {
        logger.info("Delete Booking");
        Booking entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        if (hasBookingHistory(entity.getId())) {
            entity.setStatus("CANCELLED");
            repository.save(entity);
            syncTeeTimeOccupancy(entity.getTeeTimeId());
            return;
        }

        repository.delete(entity);
        syncTeeTimeOccupancy(entity.getTeeTimeId());
    }

    private boolean hasBookingHistory(Long bookingId) {
        return bookingPlayerRepository.existsByBookingId(bookingId)
                || rentalTransactionRepository.existsByBookingId(bookingId)
                || paymentRepository.existsByBookingId(bookingId)
                || receiptRepository.existsByBookingId(bookingId);
    }

    private void syncTeeTimeOccupancy(Long teeTimeId) {
        TeeTime teeTime = teeTimeRepository.findById(teeTimeId)
                .orElseThrow(() -> new ResourceNotFoundException("Tee time not found"));
        int bookedPlayers = Math.toIntExact(bookingPlayerRepository.countByTeeTimeId(teeTimeId));
        teeTime.setBookedPlayers(bookedPlayers);

        if (!"CANCELLED".equalsIgnoreCase(teeTime.getStatus())) {
            teeTime.setStatus(bookedPlayers >= teeTime.getMaxPlayers() ? "FULL" : "AVAILABLE");
        }

        teeTimeRepository.save(teeTime);
    }

    private void prepareNewBooking(BookingDTO booking) {
        booking.setCode(resolveCodeForCreate(booking.getCode()));
        booking.setCreatedAt(LocalDateTime.now());
        booking.setStatus(resolveStatus(booking.getStatus()));
        booking.setTotalAmount(resolveTotalAmount(booking.getTotalAmount()));
    }

    private TeeTime validateTeeTime(Long teeTimeId) {
        TeeTime teeTime = teeTimeRepository.findById(teeTimeId)
                .orElseThrow(() -> new ResourceNotFoundException("Tee time not found"));

        if ("CANCELLED".equalsIgnoreCase(teeTime.getStatus())) {
            throw new BusinessException("Cannot create booking for a cancelled tee time");
        }

        return teeTime;
    }

    private String resolveCodeForCreate(String requestedCode) {
        if (requestedCode != null && !requestedCode.isBlank()) {
            if (repository.existsByCode(requestedCode)) {
                throw new BusinessException("Booking code already registered");
            }
            return requestedCode;
        }

        String code;
        do {
            code = "BK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        } while (repository.existsByCode(code));

        return code;
    }

    private String resolveCodeForUpdate(BookingDTO booking, Booking entity) {
        if (booking.getCode() == null || booking.getCode().isBlank()) {
            return entity.getCode();
        }

        return booking.getCode();
    }

    private String resolveStatus(String status) {
        if (status == null || status.isBlank()) {
            return "CREATED";
        }

        return status;
    }

    private BigDecimal resolveTotalAmount(BigDecimal totalAmount) {
        if (totalAmount == null) {
            return BigDecimal.ZERO;
        }

        return totalAmount;
    }

    private void addHateoasLinks(BookingDTO dto) {
        dto.add(linkTo(methodOn(BookingController.class).findById(dto.getId())).withSelfRel().withType("GET"));
        dto.add(linkTo(methodOn(BookingController.class).findAll()).withRel("findAll").withType("GET"));
        dto.add(linkTo(methodOn(BookingController.class).create(dto)).withRel("create").withType("POST"));
        dto.add(linkTo(methodOn(BookingController.class).update(dto)).withRel("update").withType("PUT"));
        dto.add(linkTo(methodOn(BookingController.class).delete(dto.getId())).withRel("delete").withType("DELETE"));
    }
}
