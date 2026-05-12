package com.project.golfofficeapi.model;

import jakarta.persistence.*;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "rental_transaction")
public class RentalTransaction implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "booking_id", nullable = false)
    private Long bookingId;
    @Column(name = "booking_player_id", nullable = false)
    private Long bookingPlayerId;
    @Column(name = "rental_item_id", nullable = false)
    private Long rentalItemId;
    @Column(nullable = false)
    private Integer quantity;
    @Column(nullable = false, length = 30)
    private String status;
    @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;
    @Column(name = "total_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPrice;

    public RentalTransaction() {}

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

    public Long getBookingPlayerId() {
        return bookingPlayerId;
    }

    public void setBookingPlayerId(Long bookingPlayerId) {
        this.bookingPlayerId = bookingPlayerId;
    }

    public Long getRentalItemId() {
        return rentalItemId;
    }

    public void setRentalItemId(Long rentalItemId) {
        this.rentalItemId = rentalItemId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
        RentalTransaction that = (RentalTransaction) o;
        return Objects.equals(getId(), that.getId()) && Objects.equals(getBookingId(),
                that.getBookingId()) && Objects.equals(getBookingPlayerId(),
                that.getBookingPlayerId()) && Objects.equals(getRentalItemId(),
                that.getRentalItemId()) && Objects.equals(getQuantity(),
                that.getQuantity()) && Objects.equals(getStatus(),
                that.getStatus()) && Objects.equals(getUnitPrice(), that.getUnitPrice()) && Objects.equals(getTotalPrice(),
                that.getTotalPrice());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getBookingId(), getBookingPlayerId(), getRentalItemId(), getQuantity(), getStatus(), getUnitPrice(), getTotalPrice());
    }
}
