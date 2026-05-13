package com.project.golfofficeapi.services;

import com.project.golfofficeapi.enums.BookingStatus;
import com.project.golfofficeapi.exceptions.ResourceNotFoundException;
import com.project.golfofficeapi.model.Booking;
import com.project.golfofficeapi.model.BookingPlayer;
import com.project.golfofficeapi.repository.BookingPlayerRepository;
import com.project.golfofficeapi.repository.BookingRepository;
import com.project.golfofficeapi.repository.PaymentRepository;
import com.project.golfofficeapi.repository.RentalTransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class BookingStatusService {

    private final BookingRepository bookingRepository;
    private final BookingPlayerRepository bookingPlayerRepository;
    private final RentalTransactionRepository rentalTransactionRepository;
    private final PaymentRepository paymentRepository;

    public BookingStatusService(
            BookingRepository bookingRepository,
            BookingPlayerRepository bookingPlayerRepository,
            RentalTransactionRepository rentalTransactionRepository,
            PaymentRepository paymentRepository
    ) {
        this.bookingRepository = bookingRepository;
        this.bookingPlayerRepository = bookingPlayerRepository;
        this.rentalTransactionRepository = rentalTransactionRepository;
        this.paymentRepository = paymentRepository;
    }

    public void syncBookingStatus(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            return;
        }

        boolean readyToConfirm = isReadyToConfirm(bookingId);

        if (readyToConfirm) {
            booking.setStatus(BookingStatus.CONFIRMED);
        } else if (booking.getStatus() == BookingStatus.CONFIRMED) {
            booking.setStatus(BookingStatus.CREATED);
        }

        bookingRepository.save(booking);
    }

    private boolean isReadyToConfirm(Long bookingId) {
        List<BookingPlayer> bookingPlayers = bookingPlayerRepository.findByBookingId(bookingId);

        if (bookingPlayers.isEmpty()) {
            return false;
        }

        return bookingPlayers.stream()
                .allMatch(bookingPlayer -> Boolean.TRUE.equals(bookingPlayer.getCheckedIn())
                        && isBookingPlayerPaid(bookingPlayer));
    }

    private boolean isBookingPlayerPaid(BookingPlayer bookingPlayer) {
        BigDecimal bookingPlayerTotal = calculateBookingPlayerTotal(bookingPlayer);
        BigDecimal paidAmount = paymentRepository.sumPaidAmountByBookingPlayerId(bookingPlayer.getId(), null);

        return paidAmount.compareTo(bookingPlayerTotal) >= 0;
    }

    private BigDecimal calculateBookingPlayerTotal(BookingPlayer bookingPlayer) {
        BigDecimal greenFeeAmount = bookingPlayer.getGreenFeeAmount() == null
                ? BigDecimal.ZERO
                : bookingPlayer.getGreenFeeAmount();
        BigDecimal rentalAmount = rentalTransactionRepository.sumTotalPriceByBookingPlayerId(bookingPlayer.getId());

        return greenFeeAmount.add(rentalAmount).setScale(2, RoundingMode.HALF_UP);
    }
}
