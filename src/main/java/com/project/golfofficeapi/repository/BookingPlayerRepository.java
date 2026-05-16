package com.project.golfofficeapi.repository;

import com.project.golfofficeapi.enums.BookingPlayerStatus;
import com.project.golfofficeapi.model.BookingPlayer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookingPlayerRepository extends JpaRepository<BookingPlayer, Long> {

    List<BookingPlayer> findByBooking_TeeTime_PlayDate(LocalDate playDate);

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

    List<BookingPlayer> findByStatus(BookingPlayerStatus status);

    List<BookingPlayer> findByBooking_Id(Long bookingId);

    List<BookingPlayer> findByBooking_IdAndStatus(Long bookingId, BookingPlayerStatus status);

    long countByBooking_IdAndStatus(Long bookingId, BookingPlayerStatus status);

    default List<BookingPlayer> findByBookingId(Long bookingId) {
        return findByBooking_IdAndStatus(bookingId, BookingPlayerStatus.ACTIVE);
    }

    default long countActiveByBookingId(Long bookingId) {
        return countByBooking_IdAndStatus(bookingId, BookingPlayerStatus.ACTIVE);
    }

    @Query("""
            select coalesce(sum(bp.playerCount), 0)
            from BookingPlayer bp
            where bp.booking.id in (
                select b.id
                from Booking b
                where b.teeTime.id = :teeTimeId
                and b.status <> com.project.golfofficeapi.enums.BookingStatus.CANCELLED
            )
            and bp.status = com.project.golfofficeapi.enums.BookingPlayerStatus.ACTIVE
            """)
    long countByTeeTimeId(@Param("teeTimeId") Long teeTimeId);

    @Query("""
            select coalesce(sum(bp.greenFeeAmount * bp.playerCount), 0)
            from BookingPlayer bp
            where bp.booking.id = :bookingId
            and bp.status = com.project.golfofficeapi.enums.BookingPlayerStatus.ACTIVE
            """)
    BigDecimal sumGreenFeeAmountByBookingId(@Param("bookingId") Long bookingId);
}
