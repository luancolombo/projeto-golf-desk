package com.project.golfofficeapi.repository;

import com.project.golfofficeapi.model.RentalTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface RentalTransactionRepository extends JpaRepository<RentalTransaction, Long> {

    List<RentalTransaction> findByBookingId(Long bookingId);

    List<RentalTransaction> findByBookingPlayerId(Long bookingPlayerId);

    @Query("""
            select coalesce(sum(rt.totalPrice), 0)
            from RentalTransaction rt
            where rt.bookingId = :bookingId
            and upper(coalesce(rt.status, 'RENTED')) <> 'CANCELLED'
            """)
    BigDecimal sumTotalPriceByBookingId(@Param("bookingId") Long bookingId);

    @Query("""
            select coalesce(sum(rt.totalPrice), 0)
            from RentalTransaction rt
            where rt.bookingPlayerId = :bookingPlayerId
            and upper(coalesce(rt.status, 'RENTED')) <> 'CANCELLED'
            """)
    BigDecimal sumTotalPriceByBookingPlayerId(@Param("bookingPlayerId") Long bookingPlayerId);
}
