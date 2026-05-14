package com.project.golfofficeapi.model;

import com.project.golfofficeapi.enums.RentalDamageReportStatus;
import jakarta.persistence.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "rental_damage_report")
public class RentalDamageReport implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rental_transaction_id")
    private RentalTransaction rentalTransaction;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rental_item_id")
    private RentalItem rentalItem;
    @Column(nullable = false, length = 500)
    private String description;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private RentalDamageReportStatus status;
    @Column(name = "reported_at", nullable = false)
    private LocalDateTime reportedAt;
    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;
    @Column(name = "reported_by")
    private Long reportedBy;
    @Column(name = "resolved_by")
    private Long resolvedBy;

    public RentalDamageReport() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRentalTransactionId() {
        return rentalTransaction == null ? null : rentalTransaction.getId();
    }

    public void setRentalTransactionId(Long rentalTransactionId) {
        if (rentalTransactionId == null) {
            this.rentalTransaction = null;
            return;
        }

        RentalTransaction rentalTransaction = new RentalTransaction();
        rentalTransaction.setId(rentalTransactionId);
        this.rentalTransaction = rentalTransaction;
    }

    public RentalTransaction getRentalTransaction() {
        return rentalTransaction;
    }

    public void setRentalTransaction(RentalTransaction rentalTransaction) {
        this.rentalTransaction = rentalTransaction;
    }

    public Long getRentalItemId() {
        return rentalItem == null ? null : rentalItem.getId();
    }

    public void setRentalItemId(Long rentalItemId) {
        if (rentalItemId == null) {
            this.rentalItem = null;
            return;
        }

        RentalItem rentalItem = new RentalItem();
        rentalItem.setId(rentalItemId);
        this.rentalItem = rentalItem;
    }

    public RentalItem getRentalItem() {
        return rentalItem;
    }

    public void setRentalItem(RentalItem rentalItem) {
        this.rentalItem = rentalItem;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public RentalDamageReportStatus getStatus() {
        return status;
    }

    public void setStatus(RentalDamageReportStatus status) {
        this.status = status;
    }

    public LocalDateTime getReportedAt() {
        return reportedAt;
    }

    public void setReportedAt(LocalDateTime reportedAt) {
        this.reportedAt = reportedAt;
    }

    public LocalDateTime getResolvedAt() {
        return resolvedAt;
    }

    public void setResolvedAt(LocalDateTime resolvedAt) {
        this.resolvedAt = resolvedAt;
    }

    public Long getReportedBy() {
        return reportedBy;
    }

    public void setReportedBy(Long reportedBy) {
        this.reportedBy = reportedBy;
    }

    public Long getResolvedBy() {
        return resolvedBy;
    }

    public void setResolvedBy(Long resolvedBy) {
        this.resolvedBy = resolvedBy;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        RentalDamageReport that = (RentalDamageReport) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}
