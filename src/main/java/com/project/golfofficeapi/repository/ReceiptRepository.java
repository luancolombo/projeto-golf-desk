package com.project.golfofficeapi.repository;

import com.project.golfofficeapi.model.Receipt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReceiptRepository extends JpaRepository<Receipt, Long> {

    List<Receipt> findByPlayDate(LocalDate playDate);

    List<Receipt> findByBooking_Id(Long bookingId);

    default List<Receipt> findByBookingId(Long bookingId) {
        return findByBooking_Id(bookingId);
    }

    boolean existsByBooking_Id(Long bookingId);

    default boolean existsByBookingId(Long bookingId) {
        return existsByBooking_Id(bookingId);
    }

    List<Receipt> findByBookingPlayer_Id(Long bookingPlayerId);

    default List<Receipt> findByBookingPlayerId(Long bookingPlayerId) {
        return findByBookingPlayer_Id(bookingPlayerId);
    }

    boolean existsByBookingPlayer_Id(Long bookingPlayerId);

    default boolean existsByBookingPlayerId(Long bookingPlayerId) {
        return existsByBookingPlayer_Id(bookingPlayerId);
    }

    List<Receipt> findByPayment_Id(Long paymentId);

    default List<Receipt> findByPaymentId(Long paymentId) {
        return findByPayment_Id(paymentId);
    }

    List<Receipt> findByReceiptNumberStartingWith(String prefix);

    Optional<Receipt> findFirstByPayment_IdAndCancelledFalseOrderByIdAsc(Long paymentId);

    boolean existsByReceiptNumber(String receiptNumber);
}
