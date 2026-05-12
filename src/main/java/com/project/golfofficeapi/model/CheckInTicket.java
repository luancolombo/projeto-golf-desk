package com.project.golfofficeapi.model;

import jakarta.persistence.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;

@Entity
@Table(name = "check_in_ticket")
public class CheckInTicket implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "ticket_number", nullable = false, unique = true, length = 40)
    private String ticketNumber;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_player_id", nullable = false)
    private BookingPlayer bookingPlayer;
    @Column(name = "player_name_snapshot", nullable = false, length = 100)
    private String playerNameSnapshot;
    @Column(name = "booking_code_snapshot", nullable = false, length = 40)
    private String bookingCodeSnapshot;
    @Column(name = "play_date", nullable = false)
    private LocalDate playDate;
    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;
    @Column(name = "starting_tee", nullable = false, length = 20)
    private String startingTee;
    @Column(name = "crossing_tee", nullable = false, length = 20)
    private String crossingTee;
    @Column(name = "crossing_time", nullable = false)
    private LocalTime crossingTime;
    @Column(name = "issued_at", nullable = false)
    private LocalDateTime issuedAt;
    @Column(nullable = false)
    private Boolean cancelled;
    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;
    @Column(name = "cancellation_reason", length = 255)
    private String cancellationReason;

    public CheckInTicket() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTicketNumber() {
        return ticketNumber;
    }

    public void setTicketNumber(String ticketNumber) {
        this.ticketNumber = ticketNumber;
    }

    public BookingPlayer getBookingPlayer() {
        return bookingPlayer;
    }

    public void setBookingPlayer(BookingPlayer bookingPlayer) {
        this.bookingPlayer = bookingPlayer;
    }

    public Long getBookingPlayerId() {
        return bookingPlayer == null ? null : bookingPlayer.getId();
    }

    public void setBookingPlayerId(Long bookingPlayerId) {
        if (bookingPlayerId == null) {
            this.bookingPlayer = null;
            return;
        }

        BookingPlayer bookingPlayer = new BookingPlayer();
        bookingPlayer.setId(bookingPlayerId);
        this.bookingPlayer = bookingPlayer;
    }

    public String getPlayerNameSnapshot() {
        return playerNameSnapshot;
    }

    public void setPlayerNameSnapshot(String playerNameSnapshot) {
        this.playerNameSnapshot = playerNameSnapshot;
    }

    public String getBookingCodeSnapshot() {
        return bookingCodeSnapshot;
    }

    public void setBookingCodeSnapshot(String bookingCodeSnapshot) {
        this.bookingCodeSnapshot = bookingCodeSnapshot;
    }

    public LocalDate getPlayDate() {
        return playDate;
    }

    public void setPlayDate(LocalDate playDate) {
        this.playDate = playDate;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public String getStartingTee() {
        return startingTee;
    }

    public void setStartingTee(String startingTee) {
        this.startingTee = startingTee;
    }

    public String getCrossingTee() {
        return crossingTee;
    }

    public void setCrossingTee(String crossingTee) {
        this.crossingTee = crossingTee;
    }

    public LocalTime getCrossingTime() {
        return crossingTime;
    }

    public void setCrossingTime(LocalTime crossingTime) {
        this.crossingTime = crossingTime;
    }

    public LocalDateTime getIssuedAt() {
        return issuedAt;
    }

    public void setIssuedAt(LocalDateTime issuedAt) {
        this.issuedAt = issuedAt;
    }

    public Boolean getCancelled() {
        return cancelled;
    }

    public void setCancelled(Boolean cancelled) {
        this.cancelled = cancelled;
    }

    public LocalDateTime getCancelledAt() {
        return cancelledAt;
    }

    public void setCancelledAt(LocalDateTime cancelledAt) {
        this.cancelledAt = cancelledAt;
    }

    public String getCancellationReason() {
        return cancellationReason;
    }

    public void setCancellationReason(String cancellationReason) {
        this.cancellationReason = cancellationReason;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CheckInTicket that = (CheckInTicket) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}
