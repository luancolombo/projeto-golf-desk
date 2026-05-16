package com.project.golfofficeapi.repository;

import com.project.golfofficeapi.model.CheckInTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CheckInTicketRepository extends JpaRepository<CheckInTicket, Long> {

    List<CheckInTicket> findByPlayDate(LocalDate playDate);

    List<CheckInTicket> findByBookingPlayer_Id(Long bookingPlayerId);

    default List<CheckInTicket> findByBookingPlayerId(Long bookingPlayerId) {
        return findByBookingPlayer_Id(bookingPlayerId);
    }

    boolean existsByBookingPlayer_Id(Long bookingPlayerId);

    default boolean existsByBookingPlayerId(Long bookingPlayerId) {
        return existsByBookingPlayer_Id(bookingPlayerId);
    }

    Optional<CheckInTicket> findFirstByBookingPlayer_IdAndCancelledFalseOrderByIdDesc(Long bookingPlayerId);

    List<CheckInTicket> findByTicketNumberStartingWith(String prefix);

    boolean existsByTicketNumber(String ticketNumber);
}
