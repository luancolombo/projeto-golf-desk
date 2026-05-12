package com.project.golfofficeapi.services;

import com.project.golfofficeapi.controllers.PaymentController;
import com.project.golfofficeapi.dto.PaymentDTO;
import com.project.golfofficeapi.exceptions.BusinessException;
import com.project.golfofficeapi.exceptions.RequiredObjectIsNullException;
import com.project.golfofficeapi.exceptions.ResourceNotFoundException;
import com.project.golfofficeapi.mapper.custom.PaymentMapper;
import com.project.golfofficeapi.model.Booking;
import com.project.golfofficeapi.model.BookingPlayer;
import com.project.golfofficeapi.model.Payment;
import com.project.golfofficeapi.repository.BookingPlayerRepository;
import com.project.golfofficeapi.repository.BookingRepository;
import com.project.golfofficeapi.repository.PaymentRepository;
import com.project.golfofficeapi.repository.RentalTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class PaymentService {

    private static final String STATUS_PENDING = "PENDING";
    private static final String STATUS_PAID = "PAID";
    private static final String STATUS_REFUNDED = "REFUNDED";
    private static final String STATUS_CANCELLED = "CANCELLED";
    private static final Set<String> VALID_STATUSES = Set.of(
            STATUS_PENDING,
            STATUS_PAID,
            STATUS_REFUNDED,
            STATUS_CANCELLED
    );

    @Autowired
    PaymentRepository repository;

    @Autowired
    BookingRepository bookingRepository;

    @Autowired
    BookingPlayerRepository bookingPlayerRepository;

    @Autowired
    RentalTransactionRepository rentalTransactionRepository;

    @Autowired
    BookingStatusService bookingStatusService;

    @Autowired
    ReceiptService receiptService;

    @Autowired
    PaymentMapper mapper;

    private final Logger logger = Logger.getLogger(PaymentService.class.getName());

    public PaymentService(
            PaymentRepository repository,
            BookingRepository bookingRepository,
            BookingPlayerRepository bookingPlayerRepository,
            RentalTransactionRepository rentalTransactionRepository,
            BookingStatusService bookingStatusService,
            ReceiptService receiptService,
            PaymentMapper mapper
    ) {
        this.repository = repository;
        this.bookingRepository = bookingRepository;
        this.bookingPlayerRepository = bookingPlayerRepository;
        this.rentalTransactionRepository = rentalTransactionRepository;
        this.bookingStatusService = bookingStatusService;
        this.receiptService = receiptService;
        this.mapper = mapper;
    }

    public List<PaymentDTO> findAll() {
        logger.info("Find All Payments");
        var payments = mapper.toDTOList(repository.findAll());
        payments.forEach(this::addHateoasLinks);
        return payments;
    }

    public PaymentDTO findById(Long id) {
        logger.info("Find Payment by ID");
        var payment = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));
        var dto = mapper.toDTO(payment);
        addHateoasLinks(dto);
        return dto;
    }

    public List<PaymentDTO> findByBookingId(Long bookingId) {
        logger.info("Find Payments by Booking ID");
        findBooking(bookingId);
        var payments = mapper.toDTOList(repository.findByBookingId(bookingId));
        payments.forEach(this::addHateoasLinks);
        return payments;
    }

    public List<PaymentDTO> findByBookingPlayerId(Long bookingPlayerId) {
        logger.info("Find Payments by Booking Player ID");
        findBookingPlayer(bookingPlayerId);
        var payments = mapper.toDTOList(repository.findByBookingPlayerId(bookingPlayerId));
        payments.forEach(this::addHateoasLinks);
        return payments;
    }

    @Transactional
    public PaymentDTO create(PaymentDTO payment) {
        if (payment == null) throw new RequiredObjectIsNullException();
        logger.info("Create Payment");

        Booking booking = validateBooking(payment.getBookingId());
        BookingPlayer bookingPlayer = validateBookingPlayerBelongsToBooking(payment.getBookingPlayerId(), booking.getId());
        preparePayment(payment, bookingPlayer, null, null);

        var entity = mapper.toEntity(payment, booking, bookingPlayer);
        Payment savedPayment = repository.save(entity);
        receiptService.syncReceiptForPayment(savedPayment);
        var dto = mapper.toDTO(savedPayment);
        bookingStatusService.syncBookingStatus(booking.getId());
        addHateoasLinks(dto);
        return dto;
    }

    @Transactional
    public PaymentDTO update(PaymentDTO payment) {
        if (payment == null) throw new RequiredObjectIsNullException();
        if (payment.getId() == null) throw new BusinessException("Payment id is required");
        logger.info("Update Payment");

        Payment entity = repository.findById(payment.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));
        Long oldBookingId = entity.getBookingId();
        Booking booking = validateBooking(payment.getBookingId());
        BookingPlayer bookingPlayer = validateBookingPlayerBelongsToBooking(payment.getBookingPlayerId(), booking.getId());
        preparePayment(payment, bookingPlayer, entity.getId(), entity.getPaidAt());

        entity.setBooking(booking);
        entity.setBookingPlayer(bookingPlayer);
        entity.setAmount(payment.getAmount());
        entity.setMethod(payment.getMethod());
        entity.setStatus(payment.getStatus());
        entity.setPaidAt(payment.getPaidAt());

        Payment savedPayment = repository.save(entity);
        receiptService.syncReceiptForPayment(savedPayment);
        var dto = mapper.toDTO(savedPayment);
        bookingStatusService.syncBookingStatus(oldBookingId);
        bookingStatusService.syncBookingStatus(booking.getId());
        addHateoasLinks(dto);
        return dto;
    }

    @Transactional
    public void delete(Long id) {
        logger.info("Cancel Payment");
        Payment entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));
        Long bookingId = entity.getBookingId();

        if (!STATUS_CANCELLED.equalsIgnoreCase(entity.getStatus())) {
            receiptService.cancelReceiptsByPaymentId(entity.getId(), "Payment cancelled");
            entity.setStatus(STATUS_CANCELLED);
            entity.setPaidAt(null);
            repository.save(entity);
        }

        bookingStatusService.syncBookingStatus(bookingId);
    }

    private Booking findBooking(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
    }

    private Booking validateBooking(Long bookingId) {
        Booking booking = findBooking(bookingId);

        if ("CANCELLED".equalsIgnoreCase(booking.getStatus())) {
            throw new BusinessException("Cannot add payments to a cancelled booking");
        }

        return booking;
    }

    private BookingPlayer findBookingPlayer(Long bookingPlayerId) {
        return bookingPlayerRepository.findById(bookingPlayerId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking player not found"));
    }

    private BookingPlayer validateBookingPlayerBelongsToBooking(Long bookingPlayerId, Long bookingId) {
        BookingPlayer bookingPlayer = findBookingPlayer(bookingPlayerId);

        if (!bookingPlayer.getBookingId().equals(bookingId)) {
            throw new BusinessException("Booking player does not belong to this booking");
        }

        return bookingPlayer;
    }

    private void preparePayment(
            PaymentDTO payment,
            BookingPlayer bookingPlayer,
            Long ignoredPaymentId,
            LocalDateTime currentPaidAt
    ) {
        payment.setStatus(resolveStatus(payment.getStatus()));
        validateRequiredFields(payment);
        payment.setMethod(payment.getMethod().trim().toUpperCase());
        payment.setAmount(payment.getAmount().setScale(2, RoundingMode.HALF_UP));
        validateAmount(payment.getAmount(), bookingPlayer, ignoredPaymentId, payment.getStatus());
        preparePaidAt(payment, currentPaidAt);
    }

    private void validateRequiredFields(PaymentDTO payment) {
        if (payment.getMethod() == null || payment.getMethod().isBlank()) {
            throw new BusinessException("Method is required");
        }

        if (payment.getAmount() == null) {
            throw new BusinessException("Amount is required");
        }
    }

    private String resolveStatus(String status) {
        if (status == null || status.isBlank()) {
            return STATUS_PAID;
        }

        String normalizedStatus = status.trim().toUpperCase();

        if (!VALID_STATUSES.contains(normalizedStatus)) {
            throw new BusinessException("Invalid payment status");
        }

        return normalizedStatus;
    }

    private void validateAmount(
            BigDecimal amount,
            BookingPlayer bookingPlayer,
            Long ignoredPaymentId,
            String status
    ) {
        if (amount == null) {
            throw new BusinessException("Amount is required");
        }

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Amount must be greater than zero");
        }

        BigDecimal playerTotal = calculateBookingPlayerTotal(bookingPlayer);

        if (amount.compareTo(playerTotal) > 0) {
            throw new BusinessException("Payment amount cannot be greater than booking player total");
        }

        if (!STATUS_PAID.equals(status)) {
            return;
        }

        BigDecimal alreadyPaid = repository.sumPaidAmountByBookingPlayerId(bookingPlayer.getId(), ignoredPaymentId);
        BigDecimal newPaidTotal = alreadyPaid.add(amount);

        if (newPaidTotal.compareTo(playerTotal) > 0) {
            throw new BusinessException("Payment amount exceeds booking player pending amount");
        }
    }

    private BigDecimal calculateBookingPlayerTotal(BookingPlayer bookingPlayer) {
        BigDecimal greenFeeAmount = bookingPlayer.getGreenFeeAmount() == null
                ? BigDecimal.ZERO
                : bookingPlayer.getGreenFeeAmount();
        BigDecimal rentalAmount = rentalTransactionRepository.sumTotalPriceByBookingPlayerId(bookingPlayer.getId());

        return greenFeeAmount.add(rentalAmount).setScale(2, RoundingMode.HALF_UP);
    }

    private void preparePaidAt(PaymentDTO payment, LocalDateTime currentPaidAt) {
        if (STATUS_PAID.equals(payment.getStatus())) {
            payment.setPaidAt(currentPaidAt == null ? LocalDateTime.now() : currentPaidAt);
            return;
        }

        if (STATUS_REFUNDED.equals(payment.getStatus())) {
            payment.setPaidAt(currentPaidAt == null ? payment.getPaidAt() : currentPaidAt);
            return;
        }

        payment.setPaidAt(null);
    }

    private void addHateoasLinks(PaymentDTO dto) {
        dto.add(linkTo(methodOn(PaymentController.class).findById(dto.getId())).withSelfRel().withType("GET"));
        dto.add(linkTo(methodOn(PaymentController.class).findAll()).withRel("findAll").withType("GET"));
        dto.add(linkTo(methodOn(PaymentController.class).findByBookingId(dto.getBookingId())).withRel("findByBooking").withType("GET"));
        dto.add(linkTo(methodOn(PaymentController.class).findByBookingPlayerId(dto.getBookingPlayerId())).withRel("findByBookingPlayer").withType("GET"));
        dto.add(linkTo(methodOn(PaymentController.class).create(dto)).withRel("create").withType("POST"));
        dto.add(linkTo(methodOn(PaymentController.class).update(dto)).withRel("update").withType("PUT"));
        dto.add(linkTo(methodOn(PaymentController.class).delete(dto.getId())).withRel("delete").withType("DELETE"));
    }
}
