package com.project.golfofficeapi.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import org.springframework.hateoas.RepresentationModel;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;

public class ReceiptDTO extends RepresentationModel<ReceiptDTO> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private String receiptNumber;
    private Long bookingId;
    private Long bookingPlayerId;
    @NotNull(message = "Payment id is Required!")
    private Long paymentId;
    private String playerNameSnapshot;
    private String playerTaxNumberSnapshot;
    private String bookingCodeSnapshot;
    private LocalDate playDate;
    private LocalTime startTime;
    @DecimalMin(value = "0.00", message = "Green fee amount cannot be negative")
    private BigDecimal greenFeeAmount;
    @DecimalMin(value = "0.00", message = "Rental amount cannot be negative")
    private BigDecimal rentalAmount;
    @DecimalMin(value = "0.00", message = "Total amount cannot be negative")
    private BigDecimal totalAmount;
    private String paymentMethod;
    private String paymentStatus;
    private LocalDateTime issuedAt;
    private Boolean cancelled;
    private LocalDateTime cancelledAt;
    private String cancellationReason;

    public ReceiptDTO() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReceiptNumber() {
        return receiptNumber;
    }

    public void setReceiptNumber(String receiptNumber) {
        this.receiptNumber = receiptNumber;
    }

    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public Long getBookingPlayerId() {
        return bookingPlayerId;
    }

    public void setBookingPlayerId(Long bookingPlayerId) {
        this.bookingPlayerId = bookingPlayerId;
    }

    public Long getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }

    public String getPlayerNameSnapshot() {
        return playerNameSnapshot;
    }

    public void setPlayerNameSnapshot(String playerNameSnapshot) {
        this.playerNameSnapshot = playerNameSnapshot;
    }

    public String getPlayerTaxNumberSnapshot() {
        return playerTaxNumberSnapshot;
    }

    public void setPlayerTaxNumberSnapshot(String playerTaxNumberSnapshot) {
        this.playerTaxNumberSnapshot = playerTaxNumberSnapshot;
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

    public BigDecimal getGreenFeeAmount() {
        return greenFeeAmount;
    }

    public void setGreenFeeAmount(BigDecimal greenFeeAmount) {
        this.greenFeeAmount = greenFeeAmount;
    }

    public BigDecimal getRentalAmount() {
        return rentalAmount;
    }

    public void setRentalAmount(BigDecimal rentalAmount) {
        this.rentalAmount = rentalAmount;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
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
        ReceiptDTO that = (ReceiptDTO) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}
