package com.project.golfofficeapi.dto;

import jakarta.validation.constraints.NotNull;
import org.springframework.hateoas.RepresentationModel;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;

public class CheckInTicketDTO extends RepresentationModel<CheckInTicketDTO> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private String ticketNumber;
    @NotNull(message = "Booking player id is Required!")
    private Long bookingPlayerId;
    private String playerNameSnapshot;
    private Integer playerCountSnapshot;
    private String bookingCodeSnapshot;
    private LocalDate playDate;
    private LocalTime startTime;
    private String startingTee;
    private String crossingTee;
    private LocalTime crossingTime;
    private LocalDateTime issuedAt;
    private Boolean cancelled;
    private LocalDateTime cancelledAt;
    private String cancellationReason;

    public CheckInTicketDTO() {}

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

    public Long getBookingPlayerId() {
        return bookingPlayerId;
    }

    public void setBookingPlayerId(Long bookingPlayerId) {
        this.bookingPlayerId = bookingPlayerId;
    }

    public String getPlayerNameSnapshot() {
        return playerNameSnapshot;
    }

    public void setPlayerNameSnapshot(String playerNameSnapshot) {
        this.playerNameSnapshot = playerNameSnapshot;
    }

    public Integer getPlayerCountSnapshot() {
        return playerCountSnapshot;
    }

    public void setPlayerCountSnapshot(Integer playerCountSnapshot) {
        this.playerCountSnapshot = playerCountSnapshot;
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
        CheckInTicketDTO that = (CheckInTicketDTO) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}
