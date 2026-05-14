package com.project.golfofficeapi.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.hateoas.RepresentationModel;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

public class BookingPlayerDTO extends RepresentationModel<BookingPlayerDTO> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    @NotNull(message = "Booking id is Required!")
    private Long bookingId;
    @NotNull(message = "Player id is Required!")
    private Long playerId;
    @DecimalMin(value = "0.00", message = "Green fee amount cannot be negative")
    private BigDecimal greenFeeAmount;
    @Min(value = 1, message = "Player count must be at least 1")
    @Max(value = 4, message = "Player count cannot be greater than 4")
    private Integer playerCount;
    private Boolean checkedIn;
    private String status;

    public BookingPlayerDTO() {}

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        BookingPlayerDTO that = (BookingPlayerDTO) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}
