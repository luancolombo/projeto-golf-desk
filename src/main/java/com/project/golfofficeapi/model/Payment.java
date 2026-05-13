package com.project.golfofficeapi.model;

import com.project.golfofficeapi.enums.PaymentMethod;
import com.project.golfofficeapi.enums.PaymentStatus;
import jakarta.persistence.*;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "payment")
public class Payment implements Serializable {

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
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private PaymentMethod method;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private PaymentStatus status;
    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    public Payment() {}

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

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public PaymentMethod getMethod() {
        return method;
    }

    public void setMethod(PaymentMethod method) {
        this.method = method;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public LocalDateTime getPaidAt() {
        return paidAt;
    }

    public void setPaidAt(LocalDateTime paidAt) {
        this.paidAt = paidAt;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Payment payment = (Payment) o;
        return Objects.equals(getId(), payment.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}
