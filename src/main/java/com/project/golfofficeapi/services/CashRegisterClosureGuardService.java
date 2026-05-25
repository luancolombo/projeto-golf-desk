package com.project.golfofficeapi.services;

import com.project.golfofficeapi.enums.CashRegisterClosureStatus;
import com.project.golfofficeapi.exceptions.BusinessException;
import com.project.golfofficeapi.exceptions.ResourceNotFoundException;
import com.project.golfofficeapi.model.Booking;
import com.project.golfofficeapi.model.BookingPlayer;
import com.project.golfofficeapi.model.Payment;
import com.project.golfofficeapi.model.RentalTransaction;
import com.project.golfofficeapi.model.TeeTime;
import com.project.golfofficeapi.repository.BookingRepository;
import com.project.golfofficeapi.repository.CashRegisterClosureRepository;
import com.project.golfofficeapi.repository.TeeTimeRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class CashRegisterClosureGuardService {

    private static final String CLOSED_CASH_REGISTER_MESSAGE = "Cannot change agenda after cash register is closed for this date";

    private final CashRegisterClosureRepository cashRegisterClosureRepository;
    private final BookingRepository bookingRepository;
    private final TeeTimeRepository teeTimeRepository;

    public CashRegisterClosureGuardService(
            CashRegisterClosureRepository cashRegisterClosureRepository,
            BookingRepository bookingRepository,
            TeeTimeRepository teeTimeRepository
    ) {
        this.cashRegisterClosureRepository = cashRegisterClosureRepository;
        this.bookingRepository = bookingRepository;
        this.teeTimeRepository = teeTimeRepository;
    }

    public void ensureDateIsOpen(LocalDate playDate) {
        if (playDate == null) {
            return;
        }

        if (cashRegisterClosureRepository.existsByBusinessDateAndStatus(playDate, CashRegisterClosureStatus.CLOSED)) {
            throw new BusinessException(CLOSED_CASH_REGISTER_MESSAGE);
        }
    }

    public void ensureTeeTimeIsOpen(TeeTime teeTime) {
        if (teeTime == null) {
            throw new ResourceNotFoundException("Tee time not found");
        }

        ensureDateIsOpen(teeTime.getPlayDate());
    }

    public void ensureBookingIsOpen(Booking booking) {
        if (booking == null) {
            throw new ResourceNotFoundException("Booking not found");
        }

        ensureTeeTimeIsOpen(resolveTeeTime(booking));
    }

    public void ensureBookingPlayerIsOpen(BookingPlayer bookingPlayer) {
        if (bookingPlayer == null) {
            throw new ResourceNotFoundException("Booking player not found");
        }

        ensureBookingIsOpen(resolveBooking(bookingPlayer.getBookingId()));
    }

    public void ensureRentalTransactionIsOpen(RentalTransaction rentalTransaction) {
        if (rentalTransaction == null) {
            throw new ResourceNotFoundException("Rental transaction not found");
        }

        ensureBookingIsOpen(resolveBooking(rentalTransaction.getBookingId()));
    }

    public void ensurePaymentIsOpen(Payment payment) {
        if (payment == null) {
            throw new ResourceNotFoundException("Payment not found");
        }

        ensureBookingIsOpen(resolveBooking(payment.getBookingId()));
    }

    private Booking resolveBooking(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
    }

    private TeeTime resolveTeeTime(Booking booking) {
        if (booking.getTeeTime() != null) {
            return booking.getTeeTime();
        }

        return teeTimeRepository.findById(booking.getTeeTimeId())
                .orElseThrow(() -> new ResourceNotFoundException("Tee time not found"));
    }
}
