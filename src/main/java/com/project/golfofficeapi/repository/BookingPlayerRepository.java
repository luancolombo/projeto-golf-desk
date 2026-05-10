package com.project.golfofficeapi.repository;

import com.project.golfofficeapi.model.BookingPlayer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface BookingPlayerRepository extends JpaRepository<BookingPlayer, Long> {

    long countByBookingId(Long bookingId);

    @Query("""
            select count(bp)
            from BookingPlayer bp
            where bp.bookingId in (
                select b.id
                from Booking b
                where b.teeTimeId = :teeTimeId
            )
            """)
    long countByTeeTimeId(@Param("teeTimeId") Long teeTimeId);

    @Query("""
            select coalesce(sum(bp.greenFeeAmount), 0)
            from BookingPlayer bp
            where bp.bookingId = :bookingId
            """)
    BigDecimal sumGreenFeeAmountByBookingId(@Param("bookingId") Long bookingId);
}
