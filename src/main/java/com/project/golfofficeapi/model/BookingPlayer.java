package com.project.golfofficeapi.model;

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
    @Column(name = "booking_id", nullable = false)
    private Long bookingId;
    @Column(name = "player_id", nullable = false)
    private Long playerId;
    @Column(name = "green_fee_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal greenFeeAmount;
    @Column(name = "checked_in", nullable = false)
    private Boolean checkedIn;

    public BookingPlayer() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public Long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(Long playerId) {
        this.playerId = playerId;
    }

    public BigDecimal getGreenFeeAmount() {
        return greenFeeAmount;
    }

    public void setGreenFeeAmount(BigDecimal greenFeeAmount) {
        this.greenFeeAmount = greenFeeAmount;
    }

    public Boolean getCheckedIn() {
        return checkedIn;
    }

    public void setCheckedIn(Boolean checkedIn) {
        this.checkedIn = checkedIn;
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
