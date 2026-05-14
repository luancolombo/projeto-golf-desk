package com.project.golfofficeapi.services.calculation;

import com.project.golfofficeapi.enums.CashRegisterClosureItemType;
import com.project.golfofficeapi.enums.PaymentMethod;
import com.project.golfofficeapi.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CashRegisterClosureItemCalculation {

    private CashRegisterClosureItemType type;
    private Long referenceId;
    private String referenceCode;
    private String description;
    private BigDecimal amount;
    private PaymentMethod paymentMethod;
    private PaymentStatus paymentStatus;
    private LocalDateTime occurredAt;

    public CashRegisterClosureItemType getType() {
        return type;
    }

    public void setType(CashRegisterClosureItemType type) {
        this.type = type;
    }

    public Long getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(Long referenceId) {
        this.referenceId = referenceId;
    }

    public String getReferenceCode() {
        return referenceCode;
    }

    public void setReferenceCode(String referenceCode) {
        this.referenceCode = referenceCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public LocalDateTime getOccurredAt() {
        return occurredAt;
    }

    public void setOccurredAt(LocalDateTime occurredAt) {
        this.occurredAt = occurredAt;
    }
}
