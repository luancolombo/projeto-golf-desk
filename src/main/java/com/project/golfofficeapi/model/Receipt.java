package com.project.golfofficeapi.model;

import jakarta.persistence.*;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;

@Entity
@Table(name = "receipt")
public class Receipt implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "receipt_number", nullable = false, unique = true, length = 40)
    private String receiptNumber;
    @Column(name = "booking_id", nullable = false)
    private Long bookingId;
    @Column(name = "booking_player_id", nullable = false)
    private Long bookingPlayerId;
    @Column(name = "payment_id", nullable = false)
    private Long paymentId;
    @Column(name = "player_name_snapshot", nullable = false, length = 100)
    private String playerNameSnapshot;
    @Column(name = "player_tax_number_snapshot", length = 50)
    private String playerTaxNumberSnapshot;
    @Column(name = "booking_code_snapshot", nullable = false, length = 40)
    private String bookingCodeSnapshot;
    @Column(name = "play_date", nullable = false)
    private LocalDate playDate;
    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;
    @Column(name = "green_fee_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal greenFeeAmount;
    @Column(name = "rental_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal rentalAmount;
    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;
    @Column(name = "payment_method", nullable = false, length = 30)
    private String paymentMethod;
    @Column(name = "payment_status", nullable = false, length = 30)
    private String paymentStatus;
    @Column(name = "issued_at", nullable = false)
    private LocalDateTime issuedAt;
    @Column(nullable = false)
    private Boolean cancelled;
    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;
    @Column(name = "cancellation_reason", length = 255)
    private String cancellationReason;

    public Receipt() {}

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
        Receipt receipt = (Receipt) o;
        return Objects.equals(getId(), receipt.getId()) && Objects.equals(getReceiptNumber(),
                receipt.getReceiptNumber()) && Objects.equals(getBookingId(),
                receipt.getBookingId()) && Objects.equals(getBookingPlayerId(),
                receipt.getBookingPlayerId()) && Objects.equals(getPaymentId(),
                receipt.getPaymentId()) && Objects.equals(getPlayerNameSnapshot(),
                receipt.getPlayerNameSnapshot()) && Objects.equals(getPlayerTaxNumberSnapshot(),
                receipt.getPlayerTaxNumberSnapshot()) && Objects.equals(getBookingCodeSnapshot(),
                receipt.getBookingCodeSnapshot()) && Objects.equals(getPlayDate(),
                receipt.getPlayDate()) && Objects.equals(getStartTime(),
                receipt.getStartTime()) && Objects.equals(getGreenFeeAmount(),
                receipt.getGreenFeeAmount()) && Objects.equals(getRentalAmount(),
                receipt.getRentalAmount()) && Objects.equals(getTotalAmount(),
                receipt.getTotalAmount()) && Objects.equals(getPaymentMethod(),
                receipt.getPaymentMethod()) && Objects.equals(getPaymentStatus(),
                receipt.getPaymentStatus()) && Objects.equals(getIssuedAt(),
                receipt.getIssuedAt()) && Objects.equals(getCancelled(),
                receipt.getCancelled()) && Objects.equals(getCancelledAt(),
                receipt.getCancelledAt()) && Objects.equals(getCancellationReason(),
                receipt.getCancellationReason());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getReceiptNumber(), getBookingId(), getBookingPlayerId(), getPaymentId(), getPlayerNameSnapshot(), getPlayerTaxNumberSnapshot(), getBookingCodeSnapshot(), getPlayDate(), getStartTime(), getGreenFeeAmount(), getRentalAmount(), getTotalAmount(), getPaymentMethod(), getPaymentStatus(), getIssuedAt(), getCancelled(), getCancelledAt(), getCancellationReason());
    }
}
