package com.project.golfofficeapi.services;

import com.project.golfofficeapi.controllers.ReceiptController;
import com.project.golfofficeapi.dto.ReceiptDTO;
import com.project.golfofficeapi.exceptions.BusinessException;
import com.project.golfofficeapi.exceptions.RequiredObjectIsNullException;
import com.project.golfofficeapi.exceptions.ResourceNotFoundException;
import com.project.golfofficeapi.mapper.custom.ReceiptMapper;
import com.project.golfofficeapi.model.*;
import com.project.golfofficeapi.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.logging.Logger;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class ReceiptService {

    private static final String STATUS_PAID = "PAID";
    private final ReceiptRepository repository;
    private final ReceiptItemRepository receiptItemRepository;
    private final BookingRepository bookingRepository;
    private final BookingPlayerRepository bookingPlayerRepository;
    private final PaymentRepository paymentRepository;
    private final PlayerRepository playerRepository;
    private final TeeTimeRepository teeTimeRepository;
    private final RentalTransactionRepository rentalTransactionRepository;
    private final ReceiptMapper mapper;
    private final Logger logger = Logger.getLogger(ReceiptService.class.getName());

    public ReceiptService(
            ReceiptRepository repository,
            ReceiptItemRepository receiptItemRepository,
            BookingRepository bookingRepository,
            BookingPlayerRepository bookingPlayerRepository,
            PaymentRepository paymentRepository,
            PlayerRepository playerRepository,
            TeeTimeRepository teeTimeRepository,
            RentalTransactionRepository rentalTransactionRepository,
            ReceiptMapper mapper
    ) {
        this.repository = repository;
        this.receiptItemRepository = receiptItemRepository;
        this.bookingRepository = bookingRepository;
        this.bookingPlayerRepository = bookingPlayerRepository;
        this.paymentRepository = paymentRepository;
        this.playerRepository = playerRepository;
        this.teeTimeRepository = teeTimeRepository;
        this.rentalTransactionRepository = rentalTransactionRepository;
        this.mapper = mapper;
    }

    public List<ReceiptDTO> findAll() {
        logger.info("Find All Receipts");
        var receipts = mapper.toDTOList(repository.findAll());
        receipts.forEach(this::addHateoasLinks);
        return receipts;
    }

    public ReceiptDTO findById(Long id) {
        logger.info("Find Receipt by ID");
        var receipt = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Receipt not found"));
        var dto = mapper.toDTO(receipt);
        addHateoasLinks(dto);
        return dto;
    }

    public List<ReceiptDTO> findByBookingId(Long bookingId) {
        logger.info("Find Receipts by Booking ID");
        findBooking(bookingId);
        var receipts = mapper.toDTOList(repository.findByBookingId(bookingId));
        receipts.forEach(this::addHateoasLinks);
        return receipts;
    }

    public List<ReceiptDTO> findByBookingPlayerId(Long bookingPlayerId) {
        logger.info("Find Receipts by Booking Player ID");
        findBookingPlayer(bookingPlayerId);
        var receipts = mapper.toDTOList(repository.findByBookingPlayerId(bookingPlayerId));
        receipts.forEach(this::addHateoasLinks);
        return receipts;
    }

    public List<ReceiptDTO> findByPaymentId(Long paymentId) {
        logger.info("Find Receipts by Payment ID");
        findPayment(paymentId);
        var receipts = mapper.toDTOList(repository.findByPaymentId(paymentId));
        receipts.forEach(this::addHateoasLinks);
        return receipts;
    }

    @Transactional
    public ReceiptDTO create(ReceiptDTO receipt) {
        if (receipt == null) throw new RequiredObjectIsNullException();
        logger.info("Create Receipt");
        Payment payment = findPayment(receipt.getPaymentId());
        return issueReceiptForPayment(payment);
    }

    @Transactional
    public ReceiptDTO issueReceiptForPaymentId(Long paymentId) {
        logger.info("Issue Receipt by Payment ID");
        Payment payment = findPayment(paymentId);
        return issueReceiptForPayment(payment);
    }

    @Transactional
    public ReceiptDTO update(ReceiptDTO receipt) {
        if (receipt == null) throw new RequiredObjectIsNullException();
        if (receipt.getId() == null) throw new BusinessException("Receipt id is required");
        logger.info("Update Receipt");

        Receipt entity = repository.findById(receipt.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Receipt not found"));

        if (Boolean.TRUE.equals(entity.getCancelled())) {
            throw new BusinessException("Cancelled receipts cannot be changed");
        }

        if (Boolean.TRUE.equals(receipt.getCancelled())) {
            cancelReceiptEntity(entity, resolveCancellationReason(receipt.getCancellationReason()));
        }

        var dto = mapper.toDTO(repository.save(entity));
        addHateoasLinks(dto);
        return dto;
    }

    @Transactional
    public ReceiptDTO cancel(Long id, String reason) {
        logger.info("Cancel Receipt");
        Receipt entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Receipt not found"));
        cancelReceiptEntity(entity, resolveCancellationReason(reason));
        var dto = mapper.toDTO(repository.save(entity));
        addHateoasLinks(dto);
        return dto;
    }

    @Transactional
    public void delete(Long id) {
        logger.info("Cancel Receipt by delete request");
        Receipt entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Receipt not found"));

        if (Boolean.TRUE.equals(entity.getCancelled())) {
            return;
        }

        cancelReceiptEntity(entity, "Receipt cancelled by delete request");
        repository.save(entity);
    }

    @Transactional
    public void syncReceiptForPayment(Payment payment) {
        if (payment == null || payment.getId() == null) {
            return;
        }

        if (!STATUS_PAID.equalsIgnoreCase(payment.getStatus())) {
            cancelActiveReceiptsByPaymentId(payment.getId(), "Payment status changed to " + payment.getStatus());
            return;
        }

        var activeReceipt = repository.findFirstByPayment_IdAndCancelledFalseOrderByIdAsc(payment.getId());

        if (activeReceipt.isEmpty()) {
            issueReceiptForPayment(payment);
            return;
        }

        Receipt receipt = activeReceipt.get();
        boolean amountChanged = receipt.getTotalAmount() == null
                || receipt.getTotalAmount().compareTo(payment.getAmount()) != 0;
        boolean methodChanged = receipt.getPaymentMethod() == null
                || !receipt.getPaymentMethod().equalsIgnoreCase(payment.getMethod());

        if (amountChanged || methodChanged) {
            cancelReceiptEntity(receipt, "Payment was updated");
            repository.save(receipt);
            issueReceiptForPayment(payment);
        }
    }

    @Transactional
    public void cancelReceiptsByPaymentId(Long paymentId, String reason) {
        findPayment(paymentId);
        cancelActiveReceiptsByPaymentId(paymentId, resolveCancellationReason(reason));
    }

    private ReceiptDTO issueReceiptForPayment(Payment payment) {
        validatePaymentCanIssueReceipt(payment);

        var existingActiveReceipt = repository.findFirstByPayment_IdAndCancelledFalseOrderByIdAsc(payment.getId());
        if (existingActiveReceipt.isPresent()) {
            var dto = mapper.toDTO(existingActiveReceipt.get());
            addHateoasLinks(dto);
            return dto;
        }

        Booking booking = findBooking(payment.getBookingId());
        BookingPlayer bookingPlayer = validateBookingPlayerBelongsToBooking(payment.getBookingPlayerId(), booking.getId());
        Player player = findPlayer(bookingPlayer.getPlayerId());
        TeeTime teeTime = findTeeTime(booking.getTeeTimeId());

        BigDecimal paymentAmount = money(payment.getAmount());
        BigDecimal greenFeeAmount = money(bookingPlayer.getGreenFeeAmount());
        BigDecimal greenFeePaid = paymentAmount.min(greenFeeAmount);
        BigDecimal rentalPaid = paymentAmount.subtract(greenFeePaid).max(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);

        Receipt receipt = new Receipt();
        receipt.setReceiptNumber(generateReceiptNumber());
        receipt.setBooking(booking);
        receipt.setBookingPlayer(bookingPlayer);
        receipt.setPayment(payment);
        receipt.setPlayerNameSnapshot(player.getFullName());
        receipt.setPlayerTaxNumberSnapshot(player.getTaxNumber());
        receipt.setBookingCodeSnapshot(booking.getCode());
        receipt.setPlayDate(teeTime.getPlayDate());
        receipt.setStartTime(teeTime.getStartTime());
        receipt.setGreenFeeAmount(greenFeePaid);
        receipt.setRentalAmount(rentalPaid);
        receipt.setTotalAmount(paymentAmount);
        receipt.setPaymentMethod(payment.getMethod().trim().toUpperCase(Locale.ROOT));
        receipt.setPaymentStatus(payment.getStatus().trim().toUpperCase(Locale.ROOT));
        receipt.setIssuedAt(LocalDateTime.now());
        receipt.setCancelled(false);
        receipt.setCancelledAt(null);
        receipt.setCancellationReason(null);

        Receipt savedReceipt = repository.save(receipt);
        createReceiptItems(savedReceipt, bookingPlayer, paymentAmount);

        var dto = mapper.toDTO(savedReceipt);
        addHateoasLinks(dto);
        return dto;
    }

    private void createReceiptItems(Receipt receipt, BookingPlayer bookingPlayer, BigDecimal paymentAmount) {
        BigDecimal greenFeeAmount = money(bookingPlayer.getGreenFeeAmount());
        BigDecimal greenFeePaid = paymentAmount.min(greenFeeAmount);
        BigDecimal remainingAmount = paymentAmount.subtract(greenFeePaid).max(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);

        if (greenFeePaid.compareTo(BigDecimal.ZERO) > 0) {
            saveReceiptItem(receipt, "Green fee", 1, greenFeePaid, greenFeePaid);
        }

        List<RentalTransaction> rentals = rentalTransactionRepository.findByBookingPlayerId(bookingPlayer.getId())
                .stream()
                .filter(rental -> !"CANCELLED".equalsIgnoreCase(rental.getStatus()))
                .toList();
        BigDecimal rentalTotal = rentals.stream()
                .map(rental -> money(rental.getTotalPrice()))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);

        if (remainingAmount.compareTo(BigDecimal.ZERO) <= 0 || rentalTotal.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }

        if (remainingAmount.compareTo(rentalTotal) >= 0) {
            rentals.forEach(rental -> saveReceiptItem(
                    receipt,
                    getRentalItemName(rental),
                    rental.getQuantity(),
                    money(rental.getUnitPrice()),
                    money(rental.getTotalPrice())
            ));
            return;
        }

        saveReceiptItem(receipt, "Partial rental payment", 1, remainingAmount, remainingAmount);
    }

    private void saveReceiptItem(Receipt receipt, String description, Integer quantity, BigDecimal unitPrice, BigDecimal totalPrice) {
        ReceiptItem receiptItem = new ReceiptItem();
        receiptItem.setReceipt(receipt);
        receiptItem.setDescription(description);
        receiptItem.setQuantity(quantity);
        receiptItem.setUnitPrice(money(unitPrice));
        receiptItem.setTotalPrice(money(totalPrice));
        receiptItemRepository.save(receiptItem);
    }

    private void cancelActiveReceiptsByPaymentId(Long paymentId, String reason) {
        repository.findByPaymentId(paymentId).stream()
                .filter(receipt -> !Boolean.TRUE.equals(receipt.getCancelled()))
                .forEach(receipt -> {
                    cancelReceiptEntity(receipt, reason);
                    repository.save(receipt);
                });
    }

    private void cancelReceiptEntity(Receipt receipt, String reason) {
        if (Boolean.TRUE.equals(receipt.getCancelled())) {
            return;
        }

        receipt.setCancelled(true);
        receipt.setCancelledAt(LocalDateTime.now());
        receipt.setCancellationReason(reason);
    }

    private void validatePaymentCanIssueReceipt(Payment payment) {
        if (payment == null) {
            throw new ResourceNotFoundException("Payment not found");
        }

        if (!STATUS_PAID.equalsIgnoreCase(payment.getStatus())) {
            throw new BusinessException("Only PAID payments can issue receipts");
        }

        if (payment.getAmount() == null || payment.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Receipt payment amount must be greater than zero");
        }
    }

    private Booking findBooking(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
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

    private Payment findPayment(Long paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));
    }

    private Player findPlayer(Long playerId) {
        return playerRepository.findById(playerId)
                .orElseThrow(() -> new ResourceNotFoundException("Player not found"));
    }

    private TeeTime findTeeTime(Long teeTimeId) {
        return teeTimeRepository.findById(teeTimeId)
                .orElseThrow(() -> new ResourceNotFoundException("Tee time not found"));
    }

    private String getRentalItemName(RentalTransaction rental) {
        if (rental.getRentalItem() != null && rental.getRentalItem().getName() != null) {
            return rental.getRentalItem().getName();
        }

        return "Rental item #" + rental.getRentalItemId();
    }

    private String generateReceiptNumber() {
        String prefix = "RC-" + LocalDate.now().getYear() + "-";
        int nextSequence = repository.findByReceiptNumberStartingWith(prefix).stream()
                .map(Receipt::getReceiptNumber)
                .map(number -> extractReceiptSequence(prefix, number))
                .filter(Objects::nonNull)
                .max(Integer::compareTo)
                .orElse(0) + 1;

        String candidate = prefix + String.format("%06d", nextSequence);
        while (repository.existsByReceiptNumber(candidate)) {
            nextSequence++;
            candidate = prefix + String.format("%06d", nextSequence);
        }

        return candidate;
    }

    private Integer extractReceiptSequence(String prefix, String receiptNumber) {
        if (receiptNumber == null || !receiptNumber.startsWith(prefix)) {
            return null;
        }

        String sequence = receiptNumber.substring(prefix.length());
        int suffixStart = sequence.indexOf("-");

        if (suffixStart >= 0) {
            sequence = sequence.substring(0, suffixStart);
        }

        try {
            return Integer.parseInt(sequence);
        } catch (NumberFormatException exception) {
            return null;
        }
    }

    private String resolveCancellationReason(String reason) {
        if (reason == null || reason.isBlank()) {
            return "Receipt cancelled";
        }

        return reason.trim();
    }

    private BigDecimal money(BigDecimal value) {
        if (value == null) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }

        return value.setScale(2, RoundingMode.HALF_UP);
    }

    private void addHateoasLinks(ReceiptDTO dto) {
        dto.add(linkTo(methodOn(ReceiptController.class).findById(dto.getId())).withSelfRel().withType("GET"));
        dto.add(linkTo(methodOn(ReceiptController.class).findAll()).withRel("findAll").withType("GET"));
        dto.add(linkTo(methodOn(ReceiptController.class).findByBookingId(dto.getBookingId())).withRel("findByBooking").withType("GET"));
        dto.add(linkTo(methodOn(ReceiptController.class).findByBookingPlayerId(dto.getBookingPlayerId())).withRel("findByBookingPlayer").withType("GET"));
        dto.add(linkTo(methodOn(ReceiptController.class).findByPaymentId(dto.getPaymentId())).withRel("findByPayment").withType("GET"));
        dto.add(linkTo(methodOn(ReceiptController.class).issueByPaymentId(dto.getPaymentId())).withRel("issueByPayment").withType("POST"));
        dto.add(linkTo(methodOn(ReceiptController.class).create(dto)).withRel("create").withType("POST"));
        dto.add(linkTo(methodOn(ReceiptController.class).update(dto)).withRel("update").withType("PUT"));
        dto.add(linkTo(methodOn(ReceiptController.class).cancel(dto.getId(), dto.getCancellationReason())).withRel("cancel").withType("PUT"));
        dto.add(linkTo(methodOn(ReceiptController.class).delete(dto.getId())).withRel("delete").withType("DELETE"));
    }
}
