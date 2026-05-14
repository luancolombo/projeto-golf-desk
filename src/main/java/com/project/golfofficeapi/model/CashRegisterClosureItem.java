package com.project.golfofficeapi.model;

import com.project.golfofficeapi.enums.CashRegisterClosureItemType;
import com.project.golfofficeapi.enums.PaymentMethod;
import com.project.golfofficeapi.enums.PaymentStatus;
import jakarta.persistence.*;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "cash_register_closure_item")
public class CashRegisterClosureItem implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cash_register_closure_id", nullable = false)
    private CashRegisterClosure cashRegisterClosure;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private CashRegisterClosureItemType type;

    @Column(name = "reference_id")
    private Long referenceId;

    @Column(name = "reference_code", length = 60)
    private String referenceCode;

    @Column(nullable = false, length = 255)
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", length = 30)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", length = 30)
    private PaymentStatus paymentStatus;

    @Column(name = "occurred_at", nullable = false)
    private LocalDateTime occurredAt;

    public CashRegisterClosureItem() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCashRegisterClosureId() {
        return cashRegisterClosure == null ? null : cashRegisterClosure.getId();
    }

    public void setCashRegisterClosureId(Long cashRegisterClosureId) {
        if (cashRegisterClosureId == null) {
            this.cashRegisterClosure = null;
            return;
        }

        CashRegisterClosure closure = new CashRegisterClosure();
        closure.setId(cashRegisterClosureId);
        this.cashRegisterClosure = closure;
    }

    public CashRegisterClosure getCashRegisterClosure() {
        return cashRegisterClosure;
    }

    public void setCashRegisterClosure(CashRegisterClosure cashRegisterClosure) {
        this.cashRegisterClosure = cashRegisterClosure;
    }

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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CashRegisterClosureItem that = (CashRegisterClosureItem) o;
        return Objects.equals(getId(), that.getId()) && Objects.equals(getCashRegisterClosureId(),
                that.getCashRegisterClosureId()) && getType() == that.getType() && Objects.equals(getReferenceId(),
                that.getReferenceId()) && Objects.equals(getReferenceCode(),
                that.getReferenceCode()) && Objects.equals(getDescription(),
                that.getDescription()) && Objects.equals(getAmount(),
                that.getAmount()) && getPaymentMethod() == that.getPaymentMethod() && getPaymentStatus() == that.getPaymentStatus() && Objects.equals(getOccurredAt(),
                that.getOccurredAt());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getCashRegisterClosureId(), getType(), getReferenceId(), getReferenceCode(), getDescription(), getAmount(), getPaymentMethod(), getPaymentStatus(), getOccurredAt());
    }
}
