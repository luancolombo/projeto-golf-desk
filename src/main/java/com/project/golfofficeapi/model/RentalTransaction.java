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
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_player_id", nullable = false)
    private BookingPlayer bookingPlayer;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rental_item_id", nullable = false)
    private RentalItem rentalItem;
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
        return booking == null ? null : booking.getId();
    }

    public void setBookingId(Long bookingId) {
        if (bookingId == null) {
            this.booking = null;
            return;
        }

        Booking booking = new Booking();
        booking.setId(bookingId);
        this.booking = booking;
    }

    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }

    public Long getBookingPlayerId() {
        return bookingPlayer == null ? null : bookingPlayer.getId();
    }

    public void setBookingPlayerId(Long bookingPlayerId) {
        if (bookingPlayerId == null) {
            this.bookingPlayer = null;
            return;
        }

        BookingPlayer bookingPlayer = new BookingPlayer();
        bookingPlayer.setId(bookingPlayerId);
        this.bookingPlayer = bookingPlayer;
    }

    public BookingPlayer getBookingPlayer() {
        return bookingPlayer;
    }

    public void setBookingPlayer(BookingPlayer bookingPlayer) {
        this.bookingPlayer = bookingPlayer;
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
