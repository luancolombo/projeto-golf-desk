package com.project.golfofficeapi.model;

import jakarta.persistence.*;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "receipt_item")
public class ReceiptItem implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "receipt_id", nullable = false)
    private Long receiptId;
    @Column(nullable = false, length = 120)
    private String description;
    @Column(nullable = false)
    private Integer quantity;
    @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;
    @Column(name = "total_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPrice;

    public ReceiptItem() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getReceiptId() {
        return receiptId;
    }

    public void setReceiptId(Long receiptId) {
        this.receiptId = receiptId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ReceiptItem that = (ReceiptItem) o;
        return Objects.equals(getId(), that.getId()) && Objects.equals(getReceiptId(),
                that.getReceiptId()) && Objects.equals(getDescription(),
                that.getDescription()) && Objects.equals(getQuantity(),
                that.getQuantity()) && Objects.equals(getUnitPrice(),
                that.getUnitPrice()) && Objects.equals(getTotalPrice(),
                that.getTotalPrice());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getReceiptId(), getDescription(), getQuantity(), getUnitPrice(), getTotalPrice());
    }
}
