package com.project.golfofficeapi.model;

import com.project.golfofficeapi.enums.CashRegisterClosureStatus;
import jakarta.persistence.*;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "cash_register_closure")
public class CashRegisterClosure implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "business_date", nullable = false)
    private LocalDate businessDate;

    @Column(name = "opened_at", nullable = false)
    private LocalDateTime openedAt;

    @Column(name = "closed_at")
    private LocalDateTime closedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private CashRegisterClosureStatus status;

    @Column(name = "closed_by")
    private Long closedBy;

    @Column(name = "cash_total", nullable = false, precision = 10, scale = 2)
    private BigDecimal cashTotal;

    @Column(name = "card_total", nullable = false, precision = 10, scale = 2)
    private BigDecimal cardTotal;

    @Column(name = "mbway_total", nullable = false, precision = 10, scale = 2)
    private BigDecimal mbwayTotal;

    @Column(name = "transfer_total", nullable = false, precision = 10, scale = 2)
    private BigDecimal transferTotal;

    @Column(name = "gross_total", nullable = false, precision = 10, scale = 2)
    private BigDecimal grossTotal;

    @Column(name = "refunded_total", nullable = false, precision = 10, scale = 2)
    private BigDecimal refundedTotal;

    @Column(name = "net_total", nullable = false, precision = 10, scale = 2)
    private BigDecimal netTotal;

    @Column(name = "paid_payments_count", nullable = false)
    private Integer paidPaymentsCount;

    @Column(name = "refunded_payments_count", nullable = false)
    private Integer refundedPaymentsCount;

    @Column(name = "issued_receipts_count", nullable = false)
    private Integer issuedReceiptsCount;

    @Column(name = "cancelled_receipts_count", nullable = false)
    private Integer cancelledReceiptsCount;

    @Column(name = "pending_bookings_count", nullable = false)
    private Integer pendingBookingsCount;

    @Column(name = "unreturned_rentals_count", nullable = false)
    private Integer unreturnedRentalsCount;

    @Column(length = 255)
    private String notes;

    public CashRegisterClosure() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getBusinessDate() {
        return businessDate;
    }

    public void setBusinessDate(LocalDate businessDate) {
        this.businessDate = businessDate;
    }

    public LocalDateTime getOpenedAt() {
        return openedAt;
    }

    public void setOpenedAt(LocalDateTime openedAt) {
        this.openedAt = openedAt;
    }

    public LocalDateTime getClosedAt() {
        return closedAt;
    }

    public void setClosedAt(LocalDateTime closedAt) {
        this.closedAt = closedAt;
    }

    public CashRegisterClosureStatus getStatus() {
        return status;
    }

    public void setStatus(CashRegisterClosureStatus status) {
        this.status = status;
    }

    public Long getClosedBy() {
        return closedBy;
    }

    public void setClosedBy(Long closedBy) {
        this.closedBy = closedBy;
    }

    public BigDecimal getCashTotal() {
        return cashTotal;
    }

    public void setCashTotal(BigDecimal cashTotal) {
        this.cashTotal = cashTotal;
    }

    public BigDecimal getCardTotal() {
        return cardTotal;
    }

    public void setCardTotal(BigDecimal cardTotal) {
        this.cardTotal = cardTotal;
    }

    public BigDecimal getMbwayTotal() {
        return mbwayTotal;
    }

    public void setMbwayTotal(BigDecimal mbwayTotal) {
        this.mbwayTotal = mbwayTotal;
    }

    public BigDecimal getTransferTotal() {
        return transferTotal;
    }

    public void setTransferTotal(BigDecimal transferTotal) {
        this.transferTotal = transferTotal;
    }

    public BigDecimal getGrossTotal() {
        return grossTotal;
    }

    public void setGrossTotal(BigDecimal grossTotal) {
        this.grossTotal = grossTotal;
    }

    public BigDecimal getRefundedTotal() {
        return refundedTotal;
    }

    public void setRefundedTotal(BigDecimal refundedTotal) {
        this.refundedTotal = refundedTotal;
    }

    public BigDecimal getNetTotal() {
        return netTotal;
    }

    public void setNetTotal(BigDecimal netTotal) {
        this.netTotal = netTotal;
    }

    public Integer getPaidPaymentsCount() {
        return paidPaymentsCount;
    }

    public void setPaidPaymentsCount(Integer paidPaymentsCount) {
        this.paidPaymentsCount = paidPaymentsCount;
    }

    public Integer getRefundedPaymentsCount() {
        return refundedPaymentsCount;
    }

    public void setRefundedPaymentsCount(Integer refundedPaymentsCount) {
        this.refundedPaymentsCount = refundedPaymentsCount;
    }

    public Integer getIssuedReceiptsCount() {
        return issuedReceiptsCount;
    }

    public void setIssuedReceiptsCount(Integer issuedReceiptsCount) {
        this.issuedReceiptsCount = issuedReceiptsCount;
    }

    public Integer getCancelledReceiptsCount() {
        return cancelledReceiptsCount;
    }

    public void setCancelledReceiptsCount(Integer cancelledReceiptsCount) {
        this.cancelledReceiptsCount = cancelledReceiptsCount;
    }

    public Integer getPendingBookingsCount() {
        return pendingBookingsCount;
    }

    public void setPendingBookingsCount(Integer pendingBookingsCount) {
        this.pendingBookingsCount = pendingBookingsCount;
    }

    public Integer getUnreturnedRentalsCount() {
        return unreturnedRentalsCount;
    }

    public void setUnreturnedRentalsCount(Integer unreturnedRentalsCount) {
        this.unreturnedRentalsCount = unreturnedRentalsCount;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CashRegisterClosure that = (CashRegisterClosure) o;
        return Objects.equals(getId(), that.getId()) && Objects.equals(getBusinessDate(),
                that.getBusinessDate()) && Objects.equals(getOpenedAt(),
                that.getOpenedAt()) && Objects.equals(getClosedAt(),
                that.getClosedAt()) && getStatus() == that.getStatus() && Objects.equals(getClosedBy(),
                that.getClosedBy()) && Objects.equals(getCashTotal(),
                that.getCashTotal()) && Objects.equals(getCardTotal(),
                that.getCardTotal()) && Objects.equals(getMbwayTotal(),
                that.getMbwayTotal()) && Objects.equals(getTransferTotal(),
                that.getTransferTotal()) && Objects.equals(getGrossTotal(),
                that.getGrossTotal()) && Objects.equals(getRefundedTotal(),
                that.getRefundedTotal()) && Objects.equals(getNetTotal(),
                that.getNetTotal()) && Objects.equals(getPaidPaymentsCount(),
                that.getPaidPaymentsCount()) && Objects.equals(getRefundedPaymentsCount(),
                that.getRefundedPaymentsCount()) && Objects.equals(getIssuedReceiptsCount(),
                that.getIssuedReceiptsCount()) && Objects.equals(getCancelledReceiptsCount(),
                that.getCancelledReceiptsCount()) && Objects.equals(getPendingBookingsCount(),
                that.getPendingBookingsCount()) && Objects.equals(getUnreturnedRentalsCount(),
                that.getUnreturnedRentalsCount()) && Objects.equals(getNotes(), that.getNotes());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getBusinessDate(), getOpenedAt(), getClosedAt(), getStatus(), getClosedBy(), getCashTotal(), getCardTotal(), getMbwayTotal(), getTransferTotal(), getGrossTotal(), getRefundedTotal(), getNetTotal(), getPaidPaymentsCount(), getRefundedPaymentsCount(), getIssuedReceiptsCount(), getCancelledReceiptsCount(), getPendingBookingsCount(), getUnreturnedRentalsCount(), getNotes());
    }
}
