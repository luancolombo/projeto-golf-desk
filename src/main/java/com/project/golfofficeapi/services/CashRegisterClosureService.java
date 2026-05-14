package com.project.golfofficeapi.services;

import com.project.golfofficeapi.controllers.CashRegisterClosureController;
import com.project.golfofficeapi.dto.CashRegisterClosureDTO;
import com.project.golfofficeapi.dto.CashRegisterClosureItemDTO;
import com.project.golfofficeapi.enums.BookingStatus;
import com.project.golfofficeapi.enums.CashRegisterClosureItemType;
import com.project.golfofficeapi.enums.CashRegisterClosureStatus;
import com.project.golfofficeapi.enums.PaymentMethod;
import com.project.golfofficeapi.enums.PaymentStatus;
import com.project.golfofficeapi.enums.RentalTransactionStatus;
import com.project.golfofficeapi.exceptions.BusinessException;
import com.project.golfofficeapi.exceptions.RequiredObjectIsNullException;
import com.project.golfofficeapi.exceptions.ResourceNotFoundException;
import com.project.golfofficeapi.mapper.custom.CashRegisterClosureItemMapper;
import com.project.golfofficeapi.mapper.custom.CashRegisterClosureMapper;
import com.project.golfofficeapi.model.Booking;
import com.project.golfofficeapi.model.BookingPlayer;
import com.project.golfofficeapi.model.CashRegisterClosure;
import com.project.golfofficeapi.model.Payment;
import com.project.golfofficeapi.model.Receipt;
import com.project.golfofficeapi.model.RentalTransaction;
import com.project.golfofficeapi.repository.BookingRepository;
import com.project.golfofficeapi.repository.CashRegisterClosureItemRepository;
import com.project.golfofficeapi.repository.CashRegisterClosureRepository;
import com.project.golfofficeapi.repository.PaymentRepository;
import com.project.golfofficeapi.repository.ReceiptRepository;
import com.project.golfofficeapi.repository.RentalTransactionRepository;
import com.project.golfofficeapi.services.calculation.CashRegisterClosureCalculation;
import com.project.golfofficeapi.services.calculation.CashRegisterClosureItemCalculation;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class CashRegisterClosureService {

    private final CashRegisterClosureRepository repository;
    private final CashRegisterClosureItemRepository itemRepository;
    private final PaymentRepository paymentRepository;
    private final ReceiptRepository receiptRepository;
    private final BookingRepository bookingRepository;
    private final RentalTransactionRepository rentalTransactionRepository;
    private final CashRegisterClosureMapper mapper;
    private final CashRegisterClosureItemMapper itemMapper;
    private final Logger logger = Logger.getLogger(CashRegisterClosureService.class.getName());

    public CashRegisterClosureService(
            CashRegisterClosureRepository repository,
            CashRegisterClosureItemRepository itemRepository,
            PaymentRepository paymentRepository,
            ReceiptRepository receiptRepository,
            BookingRepository bookingRepository,
            RentalTransactionRepository rentalTransactionRepository,
            CashRegisterClosureMapper mapper,
            CashRegisterClosureItemMapper itemMapper
    ) {
        this.repository = repository;
        this.itemRepository = itemRepository;
        this.paymentRepository = paymentRepository;
        this.receiptRepository = receiptRepository;
        this.bookingRepository = bookingRepository;
        this.rentalTransactionRepository = rentalTransactionRepository;
        this.mapper = mapper;
        this.itemMapper = itemMapper;
    }

    public List<CashRegisterClosureDTO> findAll() {
        logger.info("Find All Cash Register Closures");
        List<CashRegisterClosureDTO> closures = repository.findAll().stream()
                .map(this::toDTOWithItems)
                .toList();
        closures.forEach(this::addHateoasLinks);
        return closures;
    }

    public CashRegisterClosureDTO findById(Long id) {
        logger.info("Find Cash Register Closure by ID");
        CashRegisterClosure closure = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cash register closure not found"));
        CashRegisterClosureDTO dto = toDTOWithItems(closure);
        addHateoasLinks(dto);
        return dto;
    }

    public CashRegisterClosureDTO findByBusinessDate(LocalDate businessDate) {
        logger.info("Find Cash Register Closure by Business Date");
        CashRegisterClosure closure = repository.findByBusinessDate(businessDate)
                .orElseThrow(() -> new ResourceNotFoundException("Cash register closure not found"));
        CashRegisterClosureDTO dto = toDTOWithItems(closure);
        addHateoasLinks(dto);
        return dto;
    }

    @Transactional(readOnly = true)
    public CashRegisterClosureDTO preview(LocalDate businessDate) {
        logger.info("Preview Cash Register Closure");
        validateBusinessDate(businessDate);
        CashRegisterClosureDTO dto = mapper.toDTO(calculateClosure(businessDate, null, null));
        dto.setStatus(CashRegisterClosureStatus.OPEN.name());
        addHateoasLinks(dto);
        return dto;
    }

    @Transactional
    public CashRegisterClosureDTO create(CashRegisterClosureDTO closure) {
        if (closure == null) throw new RequiredObjectIsNullException();
        logger.info("Create Cash Register Closure");
        return close(closure);
    }

    @Transactional
    public CashRegisterClosureDTO close(CashRegisterClosureDTO request) {
        if (request == null) throw new RequiredObjectIsNullException();
        logger.info("Close Cash Register");
        validateBusinessDate(request.getBusinessDate());
        validateCanCloseBusinessDate(request.getBusinessDate());

        CashRegisterClosureCalculation calculated = calculateClosure(request.getBusinessDate(), request.getClosedBy(), request.getNotes());

        CashRegisterClosure entity = mapper.toEntity(calculated);
        CashRegisterClosure savedClosure = repository.save(entity);
        saveClosureItems(savedClosure, calculated.getItems());

        CashRegisterClosureDTO dto = toDTOWithItems(savedClosure);
        addHateoasLinks(dto);
        return dto;
    }

    @Transactional
    public CashRegisterClosureDTO update(CashRegisterClosureDTO closure) {
        if (closure == null) throw new RequiredObjectIsNullException();
        if (closure.getId() == null) throw new BusinessException("Cash register closure id is required");
        logger.info("Update Cash Register Closure");

        CashRegisterClosure entity = repository.findById(closure.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Cash register closure not found"));

        CashRegisterClosureStatus requestedStatus = resolveStatus(closure.getStatus());

        if (entity.getStatus() == CashRegisterClosureStatus.CANCELLED) {
            throw new BusinessException("Cancelled cash register closures cannot be changed");
        }

        if (requestedStatus == CashRegisterClosureStatus.OPEN && entity.getStatus() == CashRegisterClosureStatus.CLOSED) {
            throw new BusinessException("Closed cash register closures cannot be reopened");
        }

        entity.setClosedBy(closure.getClosedBy());
        entity.setNotes(closure.getNotes());

        if (requestedStatus == CashRegisterClosureStatus.CANCELLED) {
            entity.setStatus(CashRegisterClosureStatus.CANCELLED);
        }

        CashRegisterClosureDTO dto = toDTOWithItems(repository.save(entity));
        addHateoasLinks(dto);
        return dto;
    }

    @Transactional
    public void delete(Long id) {
        logger.info("Cancel Cash Register Closure");
        CashRegisterClosure entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cash register closure not found"));

        if (entity.getStatus() != CashRegisterClosureStatus.CANCELLED) {
            entity.setStatus(CashRegisterClosureStatus.CANCELLED);
            repository.save(entity);
        }
    }

    private CashRegisterClosureCalculation calculateClosure(LocalDate businessDate, Long closedBy, String notes) {
        ClosurePeriod period = closurePeriod(businessDate);
        ClosureSourceData sourceData = loadClosureSourceData();

        List<Payment> paidPayments = findPaymentsByStatus(sourceData.payments(), PaymentStatus.PAID, period);
        List<Payment> refundedPayments = findPaymentsByStatus(sourceData.payments(), PaymentStatus.REFUNDED, period);
        List<Receipt> issuedReceipts = findIssuedReceipts(sourceData.receipts(), period);
        List<Receipt> cancelledReceipts = findCancelledReceipts(sourceData.receipts(), period);
        List<Booking> pendingBookings = findPendingBookings(businessDate, sourceData.bookings(), sourceData.payments());
        List<RentalTransaction> unreturnedRentals = findUnreturnedRentals(businessDate, sourceData.rentalTransactions());

        PaymentTotals paymentTotals = calculatePaymentTotals(paidPayments, refundedPayments);
        List<CashRegisterClosureItemCalculation> items = buildClosureItems(
                paidPayments,
                refundedPayments,
                issuedReceipts,
                cancelledReceipts,
                pendingBookings,
                unreturnedRentals,
                sourceData.payments()
        );

        return buildClosureCalculation(
                businessDate,
                period,
                closedBy,
                notes,
                paymentTotals,
                paidPayments,
                refundedPayments,
                issuedReceipts,
                cancelledReceipts,
                pendingBookings,
                unreturnedRentals,
                items
        );
    }

    private ClosurePeriod closurePeriod(LocalDate businessDate) {
        return new ClosurePeriod(businessDate.atStartOfDay(), businessDate.atTime(LocalTime.MAX));
    }

    private ClosureSourceData loadClosureSourceData() {
        return new ClosureSourceData(
                paymentRepository.findAll(),
                receiptRepository.findAll(),
                bookingRepository.findAll(),
                rentalTransactionRepository.findAll()
        );
    }

    private List<Payment> findPaymentsByStatus(List<Payment> payments, PaymentStatus status, ClosurePeriod period) {
        return payments.stream()
                .filter(payment -> payment.getStatus() == status)
                .filter(payment -> isBetween(payment.getPaidAt(), period.start(), period.end()))
                .toList();
    }

    private PaymentTotals calculatePaymentTotals(List<Payment> paidPayments, List<Payment> refundedPayments) {
        BigDecimal paidTotal = sumPayments(paidPayments);
        BigDecimal refundedTotal = sumPayments(refundedPayments);

        return new PaymentTotals(
                sumPaymentsByMethod(paidPayments, PaymentMethod.CASH),
                sumPaymentsByMethod(paidPayments, PaymentMethod.CARD),
                sumPaymentsByMethod(paidPayments, PaymentMethod.MBWAY),
                sumPaymentsByMethod(paidPayments, PaymentMethod.TRANSFER),
                paidTotal,
                refundedTotal
        );
    }

    private List<Receipt> findIssuedReceipts(List<Receipt> receipts, ClosurePeriod period) {
        return receipts.stream()
                .filter(receipt -> !Boolean.TRUE.equals(receipt.getCancelled()))
                .filter(receipt -> isBetween(receipt.getIssuedAt(), period.start(), period.end()))
                .toList();
    }

    private List<Receipt> findCancelledReceipts(List<Receipt> receipts, ClosurePeriod period) {
        return receipts.stream()
                .filter(receipt -> Boolean.TRUE.equals(receipt.getCancelled()))
                .filter(receipt -> isBetween(resolveReceiptCancellationDate(receipt), period.start(), period.end()))
                .toList();
    }

    private List<Booking> findPendingBookings(LocalDate businessDate, List<Booking> bookings, List<Payment> payments) {
        return bookings.stream()
                .filter(booking -> booking.getStatus() != BookingStatus.CANCELLED)
                .filter(booking -> booking.getTeeTime() != null)
                .filter(booking -> businessDate.equals(booking.getTeeTime().getPlayDate()))
                .filter(booking -> calculateBookingPendingAmount(booking, payments).compareTo(BigDecimal.ZERO) > 0)
                .toList();
    }

    private List<RentalTransaction> findUnreturnedRentals(LocalDate businessDate, List<RentalTransaction> rentalTransactions) {
        return rentalTransactions.stream()
                .filter(rentalTransaction -> rentalTransaction.getStatus() == RentalTransactionStatus.RENTED)
                .filter(rentalTransaction -> rentalTransaction.getBooking() != null)
                .filter(rentalTransaction -> rentalTransaction.getBooking().getTeeTime() != null)
                .filter(rentalTransaction -> !rentalTransaction.getBooking().getTeeTime().getPlayDate().isAfter(businessDate))
                .toList();
    }

    private List<CashRegisterClosureItemCalculation> buildClosureItems(
            List<Payment> paidPayments,
            List<Payment> refundedPayments,
            List<Receipt> issuedReceipts,
            List<Receipt> cancelledReceipts,
            List<Booking> pendingBookings,
            List<RentalTransaction> unreturnedRentals,
            List<Payment> allPayments
    ) {
        List<CashRegisterClosureItemCalculation> items = new ArrayList<>();
        paidPayments.forEach(payment -> items.add(paymentItem(payment, CashRegisterClosureItemType.PAYMENT, money(payment.getAmount()))));
        refundedPayments.forEach(payment -> items.add(paymentItem(payment, CashRegisterClosureItemType.REFUND, money(payment.getAmount()).negate())));
        issuedReceipts.forEach(receipt -> items.add(receiptItem(receipt, CashRegisterClosureItemType.RECEIPT)));
        cancelledReceipts.forEach(receipt -> items.add(receiptItem(receipt, CashRegisterClosureItemType.CANCELLED_RECEIPT)));
        pendingBookings.forEach(booking -> items.add(pendingBookingItem(booking, calculateBookingPendingAmount(booking, allPayments))));
        unreturnedRentals.forEach(rentalTransaction -> items.add(unreturnedRentalItem(rentalTransaction)));
        return items;
    }

    private CashRegisterClosureCalculation buildClosureCalculation(
            LocalDate businessDate,
            ClosurePeriod period,
            Long closedBy,
            String notes,
            PaymentTotals paymentTotals,
            List<Payment> paidPayments,
            List<Payment> refundedPayments,
            List<Receipt> issuedReceipts,
            List<Receipt> cancelledReceipts,
            List<Booking> pendingBookings,
            List<RentalTransaction> unreturnedRentals,
            List<CashRegisterClosureItemCalculation> items
    ) {
        CashRegisterClosureCalculation calculation = new CashRegisterClosureCalculation();
        calculation.setBusinessDate(businessDate);
        calculation.setOpenedAt(period.start());
        calculation.setClosedAt(LocalDateTime.now());
        calculation.setStatus(CashRegisterClosureStatus.CLOSED);
        calculation.setClosedBy(closedBy);
        calculation.setCashTotal(money(paymentTotals.cashTotal()));
        calculation.setCardTotal(money(paymentTotals.cardTotal()));
        calculation.setMbwayTotal(money(paymentTotals.mbwayTotal()));
        calculation.setTransferTotal(money(paymentTotals.transferTotal()));
        calculation.setGrossTotal(money(paymentTotals.paidTotal().add(paymentTotals.refundedTotal())));
        calculation.setRefundedTotal(money(paymentTotals.refundedTotal()));
        calculation.setNetTotal(money(paymentTotals.paidTotal()));
        calculation.setPaidPaymentsCount(paidPayments.size());
        calculation.setRefundedPaymentsCount(refundedPayments.size());
        calculation.setIssuedReceiptsCount(issuedReceipts.size());
        calculation.setCancelledReceiptsCount(cancelledReceipts.size());
        calculation.setPendingBookingsCount(pendingBookings.size());
        calculation.setUnreturnedRentalsCount(unreturnedRentals.size());
        calculation.setNotes(notes == null ? null : notes.trim());
        calculation.setItems(items);
        return calculation;
    }

    private CashRegisterClosureItemCalculation paymentItem(Payment payment, CashRegisterClosureItemType type, BigDecimal amount) {
        CashRegisterClosureItemCalculation item = baseItem(type, payment.getId(), findReceiptNumber(payment), paymentDescription(payment, type), amount, payment.getPaidAt());
        item.setPaymentMethod(payment.getMethod());
        item.setPaymentStatus(payment.getStatus());
        return item;
    }

    private CashRegisterClosureItemCalculation receiptItem(Receipt receipt, CashRegisterClosureItemType type) {
        LocalDateTime occurredAt = type == CashRegisterClosureItemType.CANCELLED_RECEIPT
                ? resolveReceiptCancellationDate(receipt)
                : receipt.getIssuedAt();
        return baseItem(
                type,
                receipt.getId(),
                receipt.getReceiptNumber(),
                type == CashRegisterClosureItemType.RECEIPT
                        ? "Receipt issued - " + receipt.getPlayerNameSnapshot()
                        : "Receipt cancelled - " + receipt.getPlayerNameSnapshot(),
                money(receipt.getTotalAmount()),
                occurredAt
        );
    }

    private CashRegisterClosureItemCalculation pendingBookingItem(Booking booking, BigDecimal pendingAmount) {
        return baseItem(
                CashRegisterClosureItemType.PENDING_BOOKING,
                booking.getId(),
                booking.getCode(),
                "Pending booking balance - " + booking.getCode(),
                pendingAmount,
                LocalDateTime.now()
        );
    }

    private CashRegisterClosureItemCalculation unreturnedRentalItem(RentalTransaction rentalTransaction) {
        return baseItem(
                CashRegisterClosureItemType.UNRETURNED_RENTAL,
                rentalTransaction.getId(),
                rentalTransaction.getRentalItem() == null ? null : rentalTransaction.getRentalItem().getName(),
                "Unreturned rental item - " + (rentalTransaction.getRentalItem() == null ? "Rental #" + rentalTransaction.getId() : rentalTransaction.getRentalItem().getName()),
                money(rentalTransaction.getTotalPrice()),
                LocalDateTime.now()
        );
    }

    private CashRegisterClosureItemCalculation baseItem(
            CashRegisterClosureItemType type,
            Long referenceId,
            String referenceCode,
            String description,
            BigDecimal amount,
            LocalDateTime occurredAt
    ) {
        CashRegisterClosureItemCalculation item = new CashRegisterClosureItemCalculation();
        item.setType(type);
        item.setReferenceId(referenceId);
        item.setReferenceCode(referenceCode);
        item.setDescription(description);
        item.setAmount(money(amount));
        item.setOccurredAt(occurredAt == null ? LocalDateTime.now() : occurredAt);
        return item;
    }

    private BigDecimal calculateBookingPendingAmount(Booking booking, List<Payment> payments) {
        BigDecimal paidAmount = payments.stream()
                .filter(payment -> Objects.equals(payment.getBookingId(), booking.getId()))
                .filter(payment -> payment.getStatus() == PaymentStatus.PAID)
                .map(payment -> money(payment.getAmount()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return money(booking.getTotalAmount()).subtract(paidAmount).max(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);
    }

    private String findReceiptNumber(Payment payment) {
        return receiptRepository.findByPaymentId(payment.getId()).stream()
                .filter(receipt -> !Boolean.TRUE.equals(receipt.getCancelled()))
                .findFirst()
                .map(Receipt::getReceiptNumber)
                .orElse("PAYMENT-" + payment.getId());
    }

    private String paymentDescription(Payment payment, CashRegisterClosureItemType type) {
        String playerName = "Booking player #" + payment.getBookingPlayerId();
        BookingPlayer bookingPlayer = payment.getBookingPlayer();

        if (bookingPlayer != null && bookingPlayer.getPlayer() != null && bookingPlayer.getPlayer().getFullName() != null) {
            playerName = bookingPlayer.getPlayer().getFullName();
        }

        String action = type == CashRegisterClosureItemType.REFUND ? "Refund" : "Payment";
        return action + " " + payment.getMethod() + " - " + playerName;
    }

    private LocalDateTime resolveReceiptCancellationDate(Receipt receipt) {
        return receipt.getCancelledAt() == null ? receipt.getIssuedAt() : receipt.getCancelledAt();
    }

    private BigDecimal sumPaymentsByMethod(List<Payment> payments, PaymentMethod method) {
        return payments.stream()
                .filter(payment -> payment.getMethod() == method)
                .map(payment -> money(payment.getAmount()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal sumPayments(List<Payment> payments) {
        return payments.stream()
                .map(payment -> money(payment.getAmount()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void saveClosureItems(CashRegisterClosure closure, List<CashRegisterClosureItemCalculation> items) {
        itemRepository.deleteByCashRegisterClosureId(closure.getId());
        items.forEach(item -> {
            itemRepository.save(itemMapper.toEntity(item, closure));
        });
    }

    private CashRegisterClosureDTO toDTOWithItems(CashRegisterClosure closure) {
        List<CashRegisterClosureItemDTO> items = itemMapper.toDTOList(itemRepository.findByCashRegisterClosureId(closure.getId()));
        CashRegisterClosureDTO dto = mapper.toDTO(closure, items);
        dto.getItems().forEach(this::addItemLinks);
        return dto;
    }

    private void validateCanCloseBusinessDate(LocalDate businessDate) {
        if (repository.existsByBusinessDateAndStatus(businessDate, CashRegisterClosureStatus.CLOSED)) {
            throw new BusinessException("Cash register is already closed for this business date");
        }
    }

    private void validateBusinessDate(LocalDate businessDate) {
        if (businessDate == null) {
            throw new BusinessException("Business date is required");
        }

        if (businessDate.isAfter(LocalDate.now())) {
            throw new BusinessException("Cannot close cash register for a future date");
        }
    }

    private CashRegisterClosureStatus resolveStatus(String status) {
        try {
            return CashRegisterClosureStatus.fromString(status);
        } catch (IllegalArgumentException exception) {
            throw new BusinessException("Invalid cash register closure status");
        }
    }

    private boolean isBetween(LocalDateTime value, LocalDateTime start, LocalDateTime end) {
        return value != null && !value.isBefore(start) && !value.isAfter(end);
    }

    private BigDecimal money(BigDecimal value) {
        if (value == null) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }

        return value.setScale(2, RoundingMode.HALF_UP);
    }

    private void addHateoasLinks(CashRegisterClosureDTO dto) {
        if (dto.getId() != null) {
            dto.add(linkTo(methodOn(CashRegisterClosureController.class).findById(dto.getId())).withSelfRel().withType("GET"));
            dto.add(linkTo(methodOn(CashRegisterClosureController.class).delete(dto.getId())).withRel("delete").withType("DELETE"));
        }
        dto.add(linkTo(methodOn(CashRegisterClosureController.class).findAll()).withRel("findAll").withType("GET"));
        dto.add(linkTo(methodOn(CashRegisterClosureController.class).findByBusinessDate(dto.getBusinessDate())).withRel("findByBusinessDate").withType("GET"));
        dto.add(linkTo(methodOn(CashRegisterClosureController.class).preview(dto.getBusinessDate())).withRel("preview").withType("GET"));
        dto.add(linkTo(methodOn(CashRegisterClosureController.class).close(dto)).withRel("close").withType("POST"));
        dto.add(linkTo(methodOn(CashRegisterClosureController.class).create(dto)).withRel("create").withType("POST"));
        dto.add(linkTo(methodOn(CashRegisterClosureController.class).update(dto)).withRel("update").withType("PUT"));
    }

    private void addItemLinks(CashRegisterClosureItemDTO dto) {
        dto.add(linkTo(methodOn(com.project.golfofficeapi.controllers.CashRegisterClosureItemController.class).findById(dto.getId())).withSelfRel().withType("GET"));
    }

    private record ClosurePeriod(LocalDateTime start, LocalDateTime end) {}

    private record ClosureSourceData(
            List<Payment> payments,
            List<Receipt> receipts,
            List<Booking> bookings,
            List<RentalTransaction> rentalTransactions
    ) {}

    private record PaymentTotals(
            BigDecimal cashTotal,
            BigDecimal cardTotal,
            BigDecimal mbwayTotal,
            BigDecimal transferTotal,
            BigDecimal paidTotal,
            BigDecimal refundedTotal
    ) {}
}
