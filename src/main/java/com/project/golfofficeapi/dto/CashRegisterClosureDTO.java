package com.project.golfofficeapi.dto;

import jakarta.validation.constraints.NotNull;
import org.springframework.hateoas.RepresentationModel;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CashRegisterClosureDTO extends RepresentationModel<CashRegisterClosureDTO> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    @NotNull(message = "Business date is Required!")
    private LocalDate businessDate;
    private LocalDateTime openedAt;
    private LocalDateTime closedAt;
    private String status;
    private Long closedBy;
    private BigDecimal cashTotal;
    private BigDecimal cardTotal;
    private BigDecimal mbwayTotal;
    private BigDecimal transferTotal;
    private BigDecimal grossTotal;
    private BigDecimal refundedTotal;
    private BigDecimal netTotal;
    private Integer paidPaymentsCount;
    private Integer refundedPaymentsCount;
    private Integer issuedReceiptsCount;
    private Integer cancelledReceiptsCount;
    private Integer pendingBookingsCount;
    private Integer unreturnedRentalsCount;
    private String notes;
    private List<CashRegisterClosureItemDTO> items = new ArrayList<>();

    public CashRegisterClosureDTO() {}

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
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

    public List<CashRegisterClosureItemDTO> getItems() {
        return items;
    }

    public void setItems(List<CashRegisterClosureItemDTO> items) {
        this.items = items;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CashRegisterClosureDTO that = (CashRegisterClosureDTO) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}
