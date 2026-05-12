package com.project.golfofficeapi.repository;

import com.project.golfofficeapi.model.BookingPlayer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface BookingPlayerRepository extends JpaRepository<BookingPlayer, Long> {

    long countByBooking_Id(Long bookingId);

    default long countByBookingId(Long bookingId) {
        return countByBooking_Id(bookingId);
    }

    boolean existsByBooking_Id(Long bookingId);

    default boolean existsByBookingId(Long bookingId) {
        return existsByBooking_Id(bookingId);
    }

    boolean existsByPlayer_Id(Long playerId);

    default boolean existsByPlayerId(Long playerId) {
        return existsByPlayer_Id(playerId);
    }

    List<BookingPlayer> findByBooking_Id(Long bookingId);

    default List<BookingPlayer> findByBookingId(Long bookingId) {
        return findByBooking_Id(bookingId);
    }

    @Query("""
            select count(bp)
            from BookingPlayer bp
            where bp.booking.id in (
                select b.id
                from Booking b
                where b.teeTime.id = :teeTimeId
                and upper(b.status) <> 'CANCELLED'
            )
            """)
    long countByTeeTimeId(@Param("teeTimeId") Long teeTimeId);

    @Query("""
            select coalesce(sum(bp.greenFeeAmount), 0)
            from BookingPlayer bp
            where bp.booking.id = :bookingId
            """)
    BigDecimal sumGreenFeeAmountByBookingId(@Param("bookingId") Long bookingId);
}
