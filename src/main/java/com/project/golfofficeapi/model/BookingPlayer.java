package com.project.golfofficeapi.model;

import com.project.golfofficeapi.enums.BookingPlayerStatus;
import jakarta.persistence.*;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "booking_player")
public class BookingPlayer implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id", nullable = false)
    private Player player;
    @Column(name = "green_fee_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal greenFeeAmount;
    @Column(name = "player_count", nullable = false)
    private Integer playerCount = 1;
    @Column(name = "checked_in", nullable = false)
    private Boolean checkedIn;
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private BookingPlayerStatus status = BookingPlayerStatus.ACTIVE;

    public BookingPlayer() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }

    public Long getBookingId() {
        return booking != null ? booking.getId() : null;
    }

    public void setBookingId(Long bookingId) {
        if (bookingId == null) {
            this.booking = null;
            return;
        }

        Booking bookingReference = new Booking();
        bookingReference.setId(bookingId);
        this.booking = bookingReference;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Long getPlayerId() {
        return player != null ? player.getId() : null;
    }

    public void setPlayerId(Long playerId) {
        if (playerId == null) {
            this.player = null;
            return;
        }

        Player playerReference = new Player();
        playerReference.setId(playerId);
        this.player = playerReference;
    }

    public BigDecimal getGreenFeeAmount() {
        return greenFeeAmount;
    }

    public void setGreenFeeAmount(BigDecimal greenFeeAmount) {
        this.greenFeeAmount = greenFeeAmount;
    }

    public Integer getPlayerCount() {
        return playerCount;
    }

    public void setPlayerCount(Integer playerCount) {
        this.playerCount = playerCount;
    }

    public Boolean getCheckedIn() {
        return checkedIn;
    }

    public void setCheckedIn(Boolean checkedIn) {
        this.checkedIn = checkedIn;
    }

    public BookingPlayerStatus getStatus() {
        return status;
    }

    public void setStatus(BookingPlayerStatus status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        BookingPlayer that = (BookingPlayer) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}
