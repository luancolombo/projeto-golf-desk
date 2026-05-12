package com.project.golfofficeapi.repository;

import com.project.golfofficeapi.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByBooking_Id(Long bookingId);

    default List<Payment> findByBookingId(Long bookingId) {
        return findByBooking_Id(bookingId);
    }

    boolean existsByBooking_Id(Long bookingId);

    default boolean existsByBookingId(Long bookingId) {
        return existsByBooking_Id(bookingId);
    }

    List<Payment> findByBookingPlayer_Id(Long bookingPlayerId);

    default List<Payment> findByBookingPlayerId(Long bookingPlayerId) {
        return findByBookingPlayer_Id(bookingPlayerId);
    }

    boolean existsByBookingPlayer_Id(Long bookingPlayerId);

    default boolean existsByBookingPlayerId(Long bookingPlayerId) {
        return existsByBookingPlayer_Id(bookingPlayerId);
    }

    @Query("""
            select coalesce(sum(p.amount), 0)
            from Payment p
            where p.bookingPlayer.id = :bookingPlayerId
            and upper(p.status) = 'PAID'
            and (:ignoredPaymentId is null or p.id <> :ignoredPaymentId)
            """)
    BigDecimal sumPaidAmountByBookingPlayerId(
            @Param("bookingPlayerId") Long bookingPlayerId,
            @Param("ignoredPaymentId") Long ignoredPaymentId
    );
}
