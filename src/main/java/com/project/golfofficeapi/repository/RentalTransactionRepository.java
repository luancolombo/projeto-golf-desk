package com.project.golfofficeapi.repository;

import com.project.golfofficeapi.model.RentalTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface RentalTransactionRepository extends JpaRepository<RentalTransaction, Long> {

    List<RentalTransaction> findByBooking_TeeTime_PlayDate(LocalDate playDate);

    List<RentalTransaction> findByBooking_Id(Long bookingId);

    default List<RentalTransaction> findByBookingId(Long bookingId) {
        return findByBooking_Id(bookingId);
    }

    boolean existsByBooking_Id(Long bookingId);

    default boolean existsByBookingId(Long bookingId) {
        return existsByBooking_Id(bookingId);
    }

    List<RentalTransaction> findByBookingPlayer_Id(Long bookingPlayerId);

    default List<RentalTransaction> findByBookingPlayerId(Long bookingPlayerId) {
        return findByBookingPlayer_Id(bookingPlayerId);
    }

    boolean existsByBookingPlayer_Id(Long bookingPlayerId);

    default boolean existsByBookingPlayerId(Long bookingPlayerId) {
        return existsByBookingPlayer_Id(bookingPlayerId);
    }

    boolean existsByRentalItem_Id(Long rentalItemId);

    default boolean existsByRentalItemId(Long rentalItemId) {
        return existsByRentalItem_Id(rentalItemId);
    }

    @Query("""
            select coalesce(sum(rt.totalPrice), 0)
            from RentalTransaction rt
            where rt.booking.id = :bookingId
            and rt.status <> com.project.golfofficeapi.enums.RentalTransactionStatus.CANCELLED
            and rt.bookingPlayer.status = com.project.golfofficeapi.enums.BookingPlayerStatus.ACTIVE
            """)
    BigDecimal sumTotalPriceByBookingId(@Param("bookingId") Long bookingId);

    @Query("""
            select coalesce(sum(rt.totalPrice), 0)
            from RentalTransaction rt
            where rt.bookingPlayer.id = :bookingPlayerId
            and rt.status <> com.project.golfofficeapi.enums.RentalTransactionStatus.CANCELLED
            """)
    BigDecimal sumTotalPriceByBookingPlayerId(@Param("bookingPlayerId") Long bookingPlayerId);
}
