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

    List<Payment> findByBookingId(Long bookingId);

    List<Payment> findByBookingPlayerId(Long bookingPlayerId);

    @Query("""
            select coalesce(sum(p.amount), 0)
            from Payment p
            where p.bookingPlayerId = :bookingPlayerId
            and upper(p.status) = 'PAID'
            and (:ignoredPaymentId is null or p.id <> :ignoredPaymentId)
            """)
    BigDecimal sumPaidAmountByBookingPlayerId(
            @Param("bookingPlayerId") Long bookingPlayerId,
            @Param("ignoredPaymentId") Long ignoredPaymentId
    );
}
