package com.project.golfofficeapi.services;

import com.project.golfofficeapi.controllers.RentalTransactionController;
import com.project.golfofficeapi.dto.RentalTransactionDTO;
import com.project.golfofficeapi.exceptions.BusinessException;
import com.project.golfofficeapi.exceptions.RequiredObjectIsNullException;
import com.project.golfofficeapi.exceptions.ResourceNotFoundException;
import com.project.golfofficeapi.mapper.custom.RentalTransactionMapper;
import com.project.golfofficeapi.model.Booking;
import com.project.golfofficeapi.model.BookingPlayer;
import com.project.golfofficeapi.model.Player;
import com.project.golfofficeapi.model.RentalItem;
import com.project.golfofficeapi.model.RentalTransaction;
import com.project.golfofficeapi.model.TeeTime;
import com.project.golfofficeapi.repository.BookingPlayerRepository;
import com.project.golfofficeapi.repository.BookingRepository;
import com.project.golfofficeapi.repository.RentalItemRepository;
import com.project.golfofficeapi.repository.RentalTransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.Normalizer;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.function.Predicate;
import java.util.logging.Logger;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class RentalTransactionService {

    private static final String STATUS_RENTED = "RENTED";
    private static final String STATUS_RETURNED = "RETURNED";
    private static final String STATUS_LOST = "LOST";
    private static final String STATUS_DAMAGED = "DAMAGED";
    private static final String STATUS_CANCELLED = "CANCELLED";
    private static final Set<String> VALID_STATUSES = Set.of(
            STATUS_RENTED,
            STATUS_RETURNED,
            STATUS_LOST,
            STATUS_DAMAGED,
            STATUS_CANCELLED
    );

    private final RentalTransactionRepository repository;
    private final BookingRepository bookingRepository;
    private final BookingPlayerRepository bookingPlayerRepository;
    private final RentalItemRepository rentalItemRepository;
    private final BookingStatusService bookingStatusService;
    private final RentalTransactionMapper mapper;
    private final Logger logger = Logger.getLogger(RentalTransactionService.class.getName());

    public RentalTransactionService(
            RentalTransactionRepository repository,
            BookingRepository bookingRepository,
            BookingPlayerRepository bookingPlayerRepository,
            RentalItemRepository rentalItemRepository,
            BookingStatusService bookingStatusService,
            RentalTransactionMapper mapper
    ) {
        this.repository = repository;
        this.bookingRepository = bookingRepository;
        this.bookingPlayerRepository = bookingPlayerRepository;
        this.rentalItemRepository = rentalItemRepository;
        this.bookingStatusService = bookingStatusService;
        this.mapper = mapper;
    }

    public List<RentalTransactionDTO> findAll() {
        logger.info("Find All Rental Transactions");
        return toDTOListWithLinks(repository.findAll());
    }

    public RentalTransactionDTO findById(Long id) {
        logger.info("Find Rental Transaction by ID");
        var rentalTransaction = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rental transaction not found"));
        var dto = mapper.toDTO(rentalTransaction);
        addHateoasLinks(dto);
        return dto;
    }

    public List<RentalTransactionDTO> findByBookingId(Long bookingId) {
        logger.info("Find Rental Transactions by Booking ID");
        findBooking(bookingId);
        return toDTOListWithLinks(repository.findByBookingId(bookingId));
    }

    public List<RentalTransactionDTO> findByBookingPlayerId(Long bookingPlayerId) {
        logger.info("Find Rental Transactions by Booking Player ID");
        findBookingPlayer(bookingPlayerId);
        return toDTOListWithLinks(repository.findByBookingPlayerId(bookingPlayerId));
    }

    @Transactional
    public List<RentalTransactionDTO> returnAllByBookingId(Long bookingId) {
        logger.info("Return All Rental Transactions by Booking ID");
        Booking booking = findBooking(bookingId);
        returnRentalTransactions(repository.findByBookingId(bookingId), this::isRented);
        syncBookingTotal(booking.getId());
        return toDTOListWithLinks(repository.findByBookingId(bookingId));
    }

    @Transactional
    public List<RentalTransactionDTO> returnAll() {
        logger.info("Return All Rental Transactions");
        Set<Long> bookingIdsToSync = returnRentalTransactions(repository.findAll(), this::isRented);
        bookingIdsToSync.forEach(this::syncBookingTotal);
        return toDTOListWithLinks(repository.findAll());
    }

    @Transactional
    public List<RentalTransactionDTO> returnOverdueRentals(LocalDate currentDate) {
        logger.info("Return Overdue Rental Transactions");
        if (currentDate == null) {
            throw new BusinessException("Current date is required");
        }

        Set<Long> bookingIdsToSync = returnRentalTransactions(
                repository.findAll(),
                rentalTransaction -> isRented(rentalTransaction)
                        && isRentalFromPreviousDay(rentalTransaction, currentDate)
        );
        bookingIdsToSync.forEach(this::syncBookingTotal);
        return toDTOListWithLinks(repository.findAll());
    }

    private Set<Long> returnRentalTransactions(
            List<RentalTransaction> rentalTransactions,
            Predicate<RentalTransaction> shouldReturn
    ) {
        Set<Long> bookingIdsToSync = new HashSet<>();

        rentalTransactions.stream()
                .filter(shouldReturn)
                .forEach(rentalTransaction -> {
                    returnRentalTransaction(rentalTransaction);
                    bookingIdsToSync.add(rentalTransaction.getBookingId());
                });

        return bookingIdsToSync;
    }

    private void returnRentalTransaction(RentalTransaction rentalTransaction) {
        RentalItem rentalItem = findRentalItem(rentalTransaction.getRentalItemId());
        increaseAvailableStock(rentalItem, rentalTransaction.getQuantity());
        rentalTransaction.setStatus(STATUS_RETURNED);
        repository.save(rentalTransaction);
    }

    private boolean isRented(RentalTransaction rentalTransaction) {
        return STATUS_RENTED.equalsIgnoreCase(resolveStatus(rentalTransaction.getStatus()));
    }

    @Transactional
    public RentalTransactionDTO create(RentalTransactionDTO rentalTransaction) {
        if (rentalTransaction == null) throw new RequiredObjectIsNullException();
        logger.info("Create Rental Transaction");

        Booking booking = validateBooking(rentalTransaction.getBookingId());
        BookingPlayer bookingPlayer = validateBookingPlayerBelongsToBooking(rentalTransaction.getBookingPlayerId(), booking.getId());
        RentalItem rentalItem = validateRentalItem(rentalTransaction.getRentalItemId());
        validateQuantity(rentalTransaction.getQuantity());
        rentalTransaction.setStatus(resolveStatus(rentalTransaction.getStatus()));

        if (isStockReserved(rentalTransaction.getStatus())) {
            validateAvailableStock(rentalItem, rentalTransaction.getQuantity());
            decreaseAvailableStock(rentalItem, rentalTransaction.getQuantity());
        }

        preparePrices(rentalTransaction, rentalItem, booking, bookingPlayer);

        var entity = mapper.toEntity(rentalTransaction, booking, bookingPlayer, rentalItem);
        var dto = mapper.toDTO(repository.save(entity));
        syncBookingTotal(booking.getId());
        bookingStatusService.syncBookingStatus(booking.getId());
        addHateoasLinks(dto);
        return dto;
    }

    @Transactional
    public RentalTransactionDTO update(RentalTransactionDTO rentalTransaction) {
        if (rentalTransaction == null) throw new RequiredObjectIsNullException();
        logger.info("Update Rental Transaction");

        RentalTransaction entity = repository.findById(rentalTransaction.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Rental transaction not found"));

        Booking oldBooking = findBooking(entity.getBookingId());
        Booking newBooking = validateBooking(rentalTransaction.getBookingId());
        BookingPlayer newBookingPlayer = validateBookingPlayerBelongsToBooking(rentalTransaction.getBookingPlayerId(), newBooking.getId());
        RentalItem oldRentalItem = findRentalItem(entity.getRentalItemId());
        RentalItem newRentalItem = findRentalItem(rentalTransaction.getRentalItemId());

        validateQuantity(rentalTransaction.getQuantity());
        rentalTransaction.setStatus(resolveStatus(rentalTransaction.getStatus()));
        validateRentalItemCanReserveStock(newRentalItem, rentalTransaction.getStatus());
        adjustStockForUpdate(entity, oldRentalItem, newRentalItem, rentalTransaction.getQuantity(), rentalTransaction.getStatus());
        preparePrices(rentalTransaction, newRentalItem, newBooking, newBookingPlayer);

        entity.setBooking(newBooking);
        entity.setBookingPlayer(newBookingPlayer);
        entity.setRentalItem(newRentalItem);
        entity.setQuantity(rentalTransaction.getQuantity());
        entity.setStatus(rentalTransaction.getStatus());
        entity.setUnitPrice(rentalTransaction.getUnitPrice());
        entity.setTotalPrice(rentalTransaction.getTotalPrice());

        var dto = mapper.toDTO(repository.save(entity));
        syncBookingTotal(oldBooking.getId());
        syncBookingTotal(newBooking.getId());
        bookingStatusService.syncBookingStatus(oldBooking.getId());
        bookingStatusService.syncBookingStatus(newBooking.getId());
        addHateoasLinks(dto);
        return dto;
    }

    @Transactional
    public void delete(Long id) {
        logger.info("Cancel Rental Transaction");
        RentalTransaction entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rental transaction not found"));
        Booking booking = findBooking(entity.getBookingId());

        if (!STATUS_CANCELLED.equalsIgnoreCase(entity.getStatus())) {
            if (isStockReserved(entity.getStatus())) {
                RentalItem rentalItem = findRentalItem(entity.getRentalItemId());
                increaseAvailableStock(rentalItem, entity.getQuantity());
            }

            entity.setStatus(STATUS_CANCELLED);
            repository.save(entity);
        }

        syncBookingTotal(booking.getId());
        bookingStatusService.syncBookingStatus(booking.getId());
    }

    private Booking findBooking(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
    }

    private Booking validateBooking(Long bookingId) {
        Booking booking = findBooking(bookingId);

        if ("CANCELLED".equalsIgnoreCase(booking.getStatus())) {
            throw new BusinessException("Cannot add rental items to a cancelled booking");
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

    private RentalItem findRentalItem(Long rentalItemId) {
        return rentalItemRepository.findById(rentalItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Rental item not found"));
    }

    private RentalItem validateRentalItem(Long rentalItemId) {
        RentalItem rentalItem = findRentalItem(rentalItemId);
        validateRentalItemCanReserveStock(rentalItem, STATUS_RENTED);
        return rentalItem;
    }

    private void validateRentalItemCanReserveStock(RentalItem rentalItem, String status) {
        if (isStockReserved(status) && !Boolean.TRUE.equals(rentalItem.getActive())) {
            throw new BusinessException("Cannot rent an inactive rental item");
        }
    }

    private void validateQuantity(Integer quantity) {
        if (quantity == null) {
            throw new BusinessException("Quantity is required");
        }

        if (quantity < 1) {
            throw new BusinessException("Quantity must be at least 1");
        }
    }

    private String resolveStatus(String status) {
        if (status == null || status.isBlank()) {
            return STATUS_RENTED;
        }

        String normalizedStatus = status.trim().toUpperCase();

        if (!VALID_STATUSES.contains(normalizedStatus)) {
            throw new BusinessException("Invalid rental transaction status");
        }

        return normalizedStatus;
    }

    private boolean isStockReserved(String status) {
        String normalizedStatus = resolveStatus(status);
        return STATUS_RENTED.equals(normalizedStatus)
                || STATUS_LOST.equals(normalizedStatus)
                || STATUS_DAMAGED.equals(normalizedStatus);
    }

    private void validateAvailableStock(RentalItem rentalItem, Integer quantity) {
        if (rentalItem.getAvailableStock() < quantity) {
            throw new BusinessException("Not enough available stock for this rental item");
        }
    }

    private void preparePrices(
            RentalTransactionDTO rentalTransaction,
            RentalItem rentalItem,
            Booking booking,
            BookingPlayer bookingPlayer
    ) {
        BigDecimal unitPrice = calculateRentalUnitPrice(rentalItem, booking, bookingPlayer);
        rentalTransaction.setUnitPrice(unitPrice);
        rentalTransaction.setTotalPrice(unitPrice
                .multiply(BigDecimal.valueOf(rentalTransaction.getQuantity()))
                .setScale(2, RoundingMode.HALF_UP));
    }

    private BigDecimal calculateRentalUnitPrice(RentalItem rentalItem, Booking booking, BookingPlayer bookingPlayer) {
        BigDecimal unitPrice = rentalItem.getRentalPrice();

        if (!isBuggyOrElectricTrolley(rentalItem)) {
            return unitPrice.setScale(2, RoundingMode.HALF_UP);
        }

        if (isTwilight(booking)) {
            unitPrice = unitPrice.multiply(new BigDecimal("0.60"));
        }

        if (isMember(bookingPlayer)) {
            unitPrice = unitPrice.multiply(new BigDecimal("0.50"));
        }

        return unitPrice.setScale(2, RoundingMode.HALF_UP);
    }

    private boolean isTwilight(Booking booking) {
        TeeTime teeTime = booking.getTeeTime();

        if (teeTime == null) {
            throw new ResourceNotFoundException("Tee time not found");
        }

        return !teeTime.getStartTime().isBefore(LocalTime.of(16, 0));
    }

    private boolean isRentalFromPreviousDay(RentalTransaction rentalTransaction, LocalDate currentDate) {
        Booking booking = findBooking(rentalTransaction.getBookingId());
        TeeTime teeTime = booking.getTeeTime();

        if (teeTime == null || teeTime.getPlayDate() == null) {
            throw new ResourceNotFoundException("Tee time not found");
        }

        return teeTime.getPlayDate().isBefore(currentDate);
    }

    private boolean isMember(BookingPlayer bookingPlayer) {
        Player player = bookingPlayer.getPlayer();

        if (player == null) {
            throw new ResourceNotFoundException("Player not found");
        }

        return player.isMember();
    }

    private boolean isBuggyOrElectricTrolley(RentalItem rentalItem) {
        String searchableText = normalizeRentalText(rentalItem.getName() + " " + rentalItem.getType());

        return searchableText.contains("BUGGY")
                || (searchableText.contains("TROLLEY")
                && (searchableText.contains("ELECTRIC")
                || searchableText.contains("ELETRICO")));
    }

    private String normalizeRentalText(String value) {
        return Normalizer.normalize(value == null ? "" : value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toUpperCase(Locale.ROOT);
    }

    private void decreaseAvailableStock(RentalItem rentalItem, Integer quantity) {
        rentalItem.setAvailableStock(rentalItem.getAvailableStock() - quantity);
        rentalItemRepository.save(rentalItem);
    }

    private void increaseAvailableStock(RentalItem rentalItem, Integer quantity) {
        int availableStock = rentalItem.getAvailableStock() + quantity;
        rentalItem.setAvailableStock(Math.min(availableStock, rentalItem.getTotalStock()));
        rentalItemRepository.save(rentalItem);
    }

    private void adjustStockForUpdate(
            RentalTransaction entity,
            RentalItem oldRentalItem,
            RentalItem newRentalItem,
            Integer newQuantity,
            String newStatus
    ) {
        if (isStockReserved(entity.getStatus())) {
            increaseAvailableStock(oldRentalItem, entity.getQuantity());
        }

        if (isStockReserved(newStatus)) {
            RentalItem refreshedNewRentalItem = findRentalItem(newRentalItem.getId());
            validateAvailableStock(refreshedNewRentalItem, newQuantity);
            decreaseAvailableStock(refreshedNewRentalItem, newQuantity);
        }
    }

    private void syncBookingTotal(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
        BigDecimal greenFeeTotal = bookingPlayerRepository.sumGreenFeeAmountByBookingId(bookingId);
        BigDecimal rentalTotal = repository.sumTotalPriceByBookingId(bookingId);
        booking.setTotalAmount(greenFeeTotal.add(rentalTotal));
        bookingRepository.save(booking);
    }

    private List<RentalTransactionDTO> toDTOListWithLinks(List<RentalTransaction> rentalTransactions) {
        var dtos = mapper.toDTOList(rentalTransactions);
        dtos.forEach(this::addHateoasLinks);
        return dtos;
    }

    private void addHateoasLinks(RentalTransactionDTO dto) {
        dto.add(linkTo(methodOn(RentalTransactionController.class).findById(dto.getId())).withSelfRel().withType("GET"));
        dto.add(linkTo(methodOn(RentalTransactionController.class).findAll()).withRel("findAll").withType("GET"));
        dto.add(linkTo(methodOn(RentalTransactionController.class).findByBookingId(dto.getBookingId())).withRel("findByBooking").withType("GET"));
        dto.add(linkTo(methodOn(RentalTransactionController.class).findByBookingPlayerId(dto.getBookingPlayerId())).withRel("findByBookingPlayer").withType("GET"));
        dto.add(linkTo(methodOn(RentalTransactionController.class).returnAllByBookingId(dto.getBookingId())).withRel("returnAllByBooking").withType("PUT"));
        dto.add(linkTo(methodOn(RentalTransactionController.class).returnAll()).withRel("returnAll").withType("PUT"));
        dto.add(linkTo(methodOn(RentalTransactionController.class).create(dto)).withRel("create").withType("POST"));
        dto.add(linkTo(methodOn(RentalTransactionController.class).update(dto)).withRel("update").withType("PUT"));
        dto.add(linkTo(methodOn(RentalTransactionController.class).delete(dto.getId())).withRel("delete").withType("DELETE"));
    }
}
